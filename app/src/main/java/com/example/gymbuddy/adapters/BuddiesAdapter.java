package com.example.gymbuddy.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.gymbuddy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import kotlin.jvm.internal.Lambda;

public class BuddiesAdapter extends RecyclerView.Adapter<BuddiesAdapter.BuddiesVH> {


    List<String> list;
    Context context;
    NavController navController;


    public BuddiesAdapter(List<String> list, Activity activity) {
        this.list = list;
        this.context = activity;
        navController = Navigation.findNavController(activity, R.id.notesNavHostFragment);
    }

    @NonNull
    @Override
    public BuddiesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buddies_layout, null);
        return new BuddiesVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuddiesVH holder, @SuppressLint("RecyclerView") int position) {
        FirebaseFirestore.getInstance().collection("Users").document(list.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    holder.nameTv.setText(documentSnapshot.getString("name"));
                    Glide.with(context).load(documentSnapshot.getString("image")).into(holder.iv);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("userId", list.get(position));
                            navController.navigate(R.id.showUserDetailsFragment, bundle);

                        }
                    });

                    holder.emailTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            emailIntent.setType("message/rfc822");
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{documentSnapshot.getString("email")});
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Workout");
                            context.startActivity(Intent.createChooser(emailIntent, "Send mail using..."));


                        }
                    });

                    holder.requestTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("userId", list.get(position));
                            navController.navigate(R.id.requestWorkoutFragment, bundle);

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class BuddiesVH extends RecyclerView.ViewHolder {
        TextView nameTv, requestTv, emailTv;
        ImageView iv;

        public BuddiesVH(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            requestTv = itemView.findViewById(R.id.requestTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            iv = itemView.findViewById(R.id.iv);
        }
    }
}
