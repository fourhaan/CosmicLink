package com.example.volunteerapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Chat.Model.Users;
import com.example.volunteerapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterInterestedVolunteers extends RecyclerView.Adapter<AdapterInterestedVolunteers.ViewHolder> {
    private Context context;
    private List<Users> interestedVolunteers;

    public AdapterInterestedVolunteers(Context context, List<Users> interestedVolunteers) {
        this.context = context;
        this.interestedVolunteers = interestedVolunteers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_for_org, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users volunteer = interestedVolunteers.get(position);

        holder.Name.setText(volunteer.getFullname());
        holder.userName.setText("@"+volunteer.getUsername());
        String img_url = interestedVolunteers.get(position).getImage_url();
        Picasso.get().load(img_url).into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return interestedVolunteers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name, userName;
        CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.fullname);
            userName = itemView.findViewById(R.id.username);
            profile = itemView.findViewById(R.id.userimg);
        }
    }
}

