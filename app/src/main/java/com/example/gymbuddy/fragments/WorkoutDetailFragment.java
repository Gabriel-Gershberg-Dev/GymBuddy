package com.example.gymbuddy.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gymbuddy.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class WorkoutDetailFragment extends Fragment implements OnMapReadyCallback {


    String workoutId;
    TextView nameTv,dateTv,descTv,buddyTv,locationTv;
    ProgressBar progressBar;
    LinearLayout buddyLl;
    NavController navController;
    SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    String lat=null;
    String lng=null;
    String location;

    public WorkoutDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            workoutId = bundle.getString("workoutId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_detail, container, false);

        nameTv=view.findViewById(R.id.nameTv);
        dateTv=view.findViewById(R.id.dateTv);
        descTv=view.findViewById(R.id.descTv);
        buddyTv=view.findViewById(R.id.buddyTv);
        buddyLl=view.findViewById(R.id.buddyLl);
        locationTv=view.findViewById(R.id.locationTv);
        progressBar=view.findViewById(R.id.progress_bar);
        navController= Navigation.findNavController(getActivity(),R.id.notesNavHostFragment);



        getData();



        return view;
    }


    //Getting the workout from firestore and showing all workout details to user
    private void getData() {
        FirebaseFirestore.getInstance().collection("Workouts").document(workoutId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                 nameTv.setText(documentSnapshot.getString("title"));
                 descTv.setText(documentSnapshot.getString("desc"));
                 dateTv.setText(documentSnapshot.getString("date"));
                 dateTv.setText(documentSnapshot.getString("date"));
                 if (documentSnapshot.getString("from").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                     getBuddy(documentSnapshot.getString("to"));
                 }else {
                     getBuddy(documentSnapshot.getString("from"));
                 }
                    lat=documentSnapshot.getString("lat");
                    lng=documentSnapshot.getString("lng");
                    location=documentSnapshot.getString("location");
                    locationTv.setText(location);
                    supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMapFragment);
                    supportMapFragment.getMapAsync(WorkoutDetailFragment.this::onMapReady);

                }
            }
        });
    }

    private void getBuddy(String buddyId) {
        FirebaseFirestore.getInstance().collection("Users").document(buddyId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    buddyTv.setText(documentSnapshot.getString("name"));
                    buddyLl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle=new Bundle();
                            bundle.putString("userId",documentSnapshot.getString("uid"));
                            navController.navigate(R.id.showUserDetailsFragment,bundle);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Showing location of workout in map fragment with lat and lng values.............

        mMap=googleMap;
        LatLng latLng=new LatLng(Double.valueOf(lat),Double.valueOf(lng));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.addMarker(markerOptions);
    }
}