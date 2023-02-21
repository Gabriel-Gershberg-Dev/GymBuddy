package com.example.gymbuddy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymbuddy.R;
import com.example.gymbuddy.adapters.ReceivedRequestAdapter;
import com.example.gymbuddy.adapters.WorkoutsAdapter;
import com.example.gymbuddy.models.WorkoutModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ReceivedRequestsFragment extends Fragment {


    public ReceivedRequestsFragment() {
        // Required empty public constructor
    }

    List<WorkoutModel> list=new ArrayList<>();
    RecyclerView rv;
    ReceivedRequestAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_received_requests, container, false);

        rv=view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        getRequests();
        return view;
    }

    //Getting the list of all workouts from firestore where other buddies have invited me for workout
    // and sending the list to adatpter to show all requests in recyclerview

    private void getRequests() {
        FirebaseFirestore.getInstance().collection("Workouts").whereEqualTo("status","pending").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                list.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    if (documentSnapshot.getString("to").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        list.add(documentSnapshot.toObject(WorkoutModel.class));
                    }
                }
                adapter=new ReceivedRequestAdapter(list,getActivity());
                rv.setAdapter(adapter);

            }
        });
    }
}