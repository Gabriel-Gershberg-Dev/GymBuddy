package com.example.gymbuddy.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymbuddy.R;
import com.example.gymbuddy.models.UserModel;

import java.util.List;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.SwipeVH>{

    Activity context;
    List<UserModel> list;
    NavController navController;

    public SwipeAdapter(Activity activity, List<UserModel> list) {
        this.context = activity;
        this.list = list;
        navController= Navigation.findNavController(context,R.id.notesNavHostFragment);
    }

    @NonNull
    @Override
    public SwipeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cards_layout,null);
        return new SwipeVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwipeVH holder, @SuppressLint("RecyclerView") int position) {
        holder.nameTv.setText(list.get(position).getName());
        holder.ageTv.setText(list.get(position).getAge()+" years old");
        if (list.get(position).getGender().equals("Male")){
          holder.mainIv.setImageResource(R.drawable.malegym);
        } else if (list.get(position).getGender().equals("FeMale")) {
            holder.mainIv.setImageResource(R.drawable.femalegym);

        }else {
            holder.mainIv.setImageResource(R.drawable.othergym);
        }

        holder.detailsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("userId",list.get(position).getUid());
                navController.navigate(R.id.showUserDetailsFragment,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SwipeVH extends RecyclerView.ViewHolder{
     TextView nameTv,ageTv;
     ImageView mainIv,detailsIv;
        public SwipeVH(@NonNull View itemView) {
            super(itemView);
            nameTv=itemView.findViewById(R.id.nameTv);
            ageTv=itemView.findViewById(R.id.ageTv);
            mainIv=itemView.findViewById(R.id.mainIv);
            detailsIv=itemView.findViewById(R.id.detaulsIv);
        }
    }
}
