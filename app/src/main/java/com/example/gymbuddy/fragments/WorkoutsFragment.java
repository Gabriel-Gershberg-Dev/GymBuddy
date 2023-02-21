package com.example.gymbuddy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gymbuddy.R;
import com.example.gymbuddy.adapters.WorkoutsAdapter;
import com.example.gymbuddy.models.WorkoutModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkoutsFragment extends Fragment {
    RecyclerView rv;
    TextView requestsTv;
    List<WorkoutModel> list=new ArrayList<>();
    WorkoutsAdapter adapter;
    NavController navController;
    String uid;
    public WorkoutsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_workouts, container, false);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        rv=view.findViewById(R.id.rv);
        requestsTv=view.findViewById(R.id.requestsTv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        navController= Navigation.findNavController(getActivity(),R.id.notesNavHostFragment);

        requestsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           navController.navigate(R.id.receivedRequestsFragment);
            }
        });

        getWorkouts();
        
        
        return view;
    }

    //Getting all workouts from firestore which are approved and showing in recycler view
    private void getWorkouts() {
        FirebaseFirestore.getInstance().collection("Workouts").whereEqualTo("status","approved").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                list.clear();
              for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                  if (documentSnapshot.getString("from").equals(uid)||documentSnapshot.getString("to").equals(uid)){
                      list.add(documentSnapshot.toObject(WorkoutModel.class));
                  }
              }
              adapter=new WorkoutsAdapter(list,getActivity());
              rv.setAdapter(adapter);

            }
        });
    }
}