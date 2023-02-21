package com.example.gymbuddy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymbuddy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ShowUserDetailsFragment extends Fragment {

    String userId;
    TextView nameTv, emailTv, dobTv, genderTv, heightTv,sportsTv;
    ProgressBar progress_bar;
    Button actionBtn;


    public ShowUserDetailsFragment() {
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
        View view= inflater.inflate(R.layout.fragment_show_user_details, container, false);

        nameTv=view.findViewById(R.id.nameTv);
        emailTv=view.findViewById(R.id.emailTv);
        dobTv=view.findViewById(R.id.dobTv);
        genderTv=view.findViewById(R.id.genderTv);
        heightTv=view.findViewById(R.id.heightTv);
        sportsTv=view.findViewById(R.id.sportTv);
        progress_bar=view.findViewById(R.id.progress_bar);
        actionBtn=view.findViewById(R.id.actionBtn);

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (actionBtn.getText().toString().equals("Add as buddy")){

                    //Adding user as a buddy and changing action button status

                    progress_bar.setVisibility(View.VISIBLE);
                    Map map=new HashMap();
                    map.put("status","yes");
                    map.put("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Buddies").document(userId).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseFirestore.getInstance().collection("Users").document(userId).collection("Buddies").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progress_bar.setVisibility(View.GONE);
                                    actionBtn.setText("Remove from buddies");

                                }
                            });
                        }
                    });
                }
                else {

                    //removing user from buddies and changing action button status

                    progress_bar.setVisibility(View.VISIBLE);
                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Buddies").document(userId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseFirestore.getInstance().collection("Users").document(userId).collection("Buddies").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progress_bar.setVisibility(View.GONE);
                                    actionBtn.setText("Add as buddy");

                                }
                            });
                        }
                    });
                }
            }
        });

        loadData();
        return view;
    }


    //Fetching the data of specific user from firestore to show its details to current user
    private void loadData() {
        FirebaseFirestore.getInstance().collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nameTv.setText(documentSnapshot.getString("name") );
                emailTv.setText(documentSnapshot.getString("email"));
                dobTv.setText(documentSnapshot.getString("age")+" years old");
                genderTv.setText(documentSnapshot.getString("gender"));
                heightTv.setText(documentSnapshot.getString("height")+" cm");
                sportsTv.setText(documentSnapshot.getString("sports"));

                //Checking if the user is alredy buddy or not and then showing "Add as buddy" or "Remove from buddies" button respectively


                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Buddies").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                actionBtn.setEnabled(true);
                                actionBtn.setText("Remove from buddies");
                            }else {
                                actionBtn.setEnabled(true);
                                actionBtn.setText("Add as buddy");
                            }
                            progress_bar.setVisibility(View.GONE);
                        }
                    }
                });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress_bar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}