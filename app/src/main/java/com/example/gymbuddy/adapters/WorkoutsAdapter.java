package com.example.gymbuddy.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymbuddy.R;
import com.example.gymbuddy.models.WorkoutModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutVH> {

    List<WorkoutModel> list;
    Activity context;
    NavController navController;

    public WorkoutsAdapter(List<WorkoutModel> list, Activity context) {
        this.list = list;
        this.context = context;
        navController= Navigation.findNavController(context,R.id.notesNavHostFragment);
    }

    @NonNull
    @Override
    public WorkoutVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_layout,null);
        return new WorkoutVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutVH holder, @SuppressLint("RecyclerView") int position) {
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

         holder.deleteIv.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FirebaseFirestore.getInstance().collection("Workouts").document(list.get(position).getId()).delete();
                 Toast.makeText(context, "Workout deleted successfully", Toast.LENGTH_SHORT).show();
                 list.remove(position);
                 WorkoutsAdapter.this.notifyDataSetChanged();
             }
         });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class WorkoutVH extends RecyclerView.ViewHolder{
        TextView nameTv,dateTv;
        ImageView deleteIv;

        public WorkoutVH(@NonNull View itemView) {
            super(itemView);
            nameTv=itemView.findViewById(R.id.nameTv);
            dateTv=itemView.findViewById(R.id.dateTv);
            deleteIv=itemView.findViewById(R.id.deleteIv);
        }
    }
}
