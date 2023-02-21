package com.example.gymbuddy.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymbuddy.R;
import com.example.gymbuddy.models.WorkoutModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ReceivedRequestAdapter extends RecyclerView.Adapter<ReceivedRequestAdapter.RequestsVH> {


    List<WorkoutModel> list;
    Activity context;
    NavController navController;

    public ReceivedRequestAdapter(List<WorkoutModel> list, Activity context) {
        this.list = list;
        this.context = context;
        navController= Navigation.findNavController(context,R.id.notesNavHostFragment);
    }

    @NonNull
    @Override
    public RequestsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.requests_layout,null);
        return new RequestsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsVH holder, @SuppressLint("RecyclerView") int position) {
        holder.nameTv.setText(list.get(position).getTitle());
        holder.dateTv.setText(list.get(position).getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("workoutId",list.get(position).getId());
                navController.navigate(R.id.workoutDetailFragment,bundle);
            }
        });

        holder.approveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("Workouts").document(list.get(position).getId()).update("status","approved");

                FirebaseFirestore.getInstance().collection("Workouts").whereEqualTo("status","pending").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        list.clear();
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                            if (documentSnapshot.getString("to").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                list.add(documentSnapshot.toObject(WorkoutModel.class));
                            }
                        }
                        ReceivedRequestAdapter.this.notifyDataSetChanged();
                        Toast.makeText(context, "Request approved successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RequestsVH extends RecyclerView.ViewHolder{
        TextView nameTv,dateTv,approveTv;
        public RequestsVH(@NonNull View itemView) {
            super(itemView);
            nameTv=itemView.findViewById(R.id.nameTv);
            dateTv=itemView.findViewById(R.id.dateTv);
            approveTv=itemView.findViewById(R.id.approveTv);
        }
    }
}
