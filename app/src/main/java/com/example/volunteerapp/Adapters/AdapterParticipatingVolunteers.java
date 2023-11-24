package com.example.volunteerapp.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Activities.OrgTaskActivity;
import com.example.volunteerapp.Chat.Activity.chatwindo;
import com.example.volunteerapp.Chat.Model.Users;
import com.example.volunteerapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterParticipatingVolunteers extends RecyclerView.Adapter<AdapterParticipatingVolunteers.ViewHolder> {
    private Context context;
    private List<Users> participatingVolunteers;
    private String pId;

    public AdapterParticipatingVolunteers(Context context, List<Users> participatingVolunteers, String pId) {
        this.context = context;
        this.participatingVolunteers = participatingVolunteers;
        this.pId = pId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_for_org_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users volunteer = participatingVolunteers.get(position);

        holder.Name.setText(volunteer.getFullname());
        holder.userName.setText("@"+volunteer.getUsername());
        String uid = participatingVolunteers.get(position).getUserId();
        String img_url = participatingVolunteers.get(position).getImage_url();
        Picasso.get().load(img_url).centerCrop().resize(100,100).into(holder.profile);

        holder.addTask.setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), OrgTaskActivity.class);
            intent.putExtra("uId",uid);
            intent.putExtra("pId",pId);
            context.startActivity(intent);
        });

        holder.profile.setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), chatwindo.class);
            intent.putExtra("uid",uid);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return participatingVolunteers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name, userName;
        CircleImageView profile;
        Button addTask;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.fullname);
            userName = itemView.findViewById(R.id.username);
            profile = itemView.findViewById(R.id.userimg);
            addTask = itemView.findViewById(R.id.view_tasks);
        }
    }
}


