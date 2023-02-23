package com.example.gymbuddy.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gymbuddy.R;
import com.example.gymbuddy.SelectLocActivity;
import com.example.gymbuddy.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RequestWorkoutFragment extends Fragment {


    String userId,lat,lng;
    EditText locationEt,titleEt,descEt,dateEt;
    Button sendBtn;
    ProgressBar progress_bar;
    NavController navController;

    public RequestWorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if (bundle!=null){
            userId=bundle.getString("userId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_request_workout, container, false);
        locationEt=view.findViewById(R.id.locationEt);
        titleEt=view.findViewById(R.id.titleEt);
        descEt=view.findViewById(R.id.descEt);
        dateEt=view.findViewById(R.id.dateEt);
        sendBtn=view.findViewById(R.id.sendBtn);
        progress_bar=view.findViewById(R.id.progress_bar);
        navController= Navigation.findNavController(getActivity(),R.id.notesNavHostFragment);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });
        locationEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusCheck();
            }
        });


        return view;
    }

    private void sendRequest() {

        //Validating user inputs to make sure all fields are filled

        if (titleEt.getText().toString().length() < 1) {
            titleEt.setError("Name field must be filled");
            return;
        }
        if (descEt.getText().toString().length() < 1) {
            descEt.setError("Description field must be filled");
            return;
        }
        if (dateEt.getText().toString().length() < 1) {
            dateEt.setError("Date field must be filled");
            return;
        }
        if (locationEt.getText().toString().length() < 1) {
            locationEt.setError("Location must be selected");
            return;
        }

        progress_bar.setVisibility(View.VISIBLE);
        String id=String.valueOf(System.currentTimeMillis());
        Map map=new HashMap();
        map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("to",userId);
        map.put("title",titleEt.getText().toString());
        map.put("desc",descEt.getText().toString());
        map.put("date",dateEt.getText().toString());
        map.put("location",locationEt.getText().toString());
        map.put("lat",lat);
        map.put("lng",lng);
        map.put("status","pending");
        map.put("id",id);


        //Uploading a new workout request to firestore...............

        FirebaseFirestore.getInstance().collection("Workouts").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progress_bar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                navController.popBackStack();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress_bar.setVisibility(View.GONE);
                Toast.makeText(getContext(), ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void statusCheck() {

        //Checking that if location of device is enabled or disabled................

        final LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
        else {

            //Sending user to location activity to select location of workout............

            Intent i = new Intent(getContext(), SelectLocActivity.class);
            startActivityForResult(i, 1122);

        }
    }

    private void buildAlertMessageNoGps() {

        // Showing alert dialog to user for enabling the gps location..............

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Intent i = new Intent(getContext(), SelectLocActivity.class);
                        startActivityForResult(i, 1122);

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //receiving the location sent by loaction activity for workout

        if (requestCode == 1122) {
            if(resultCode == Activity.RESULT_OK){
                if (data!=null){
                    locationEt.setText(data.getStringExtra("add"));
                    lat=data.getStringExtra("lat");
                    lng=data.getStringExtra("lng");
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getContext(), "No location selected", Toast.LENGTH_SHORT).show();
            }
        }


    }
}