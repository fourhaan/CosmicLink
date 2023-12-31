package com.example.volunteerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Activities.CustomViews.BookmarkPostDetails;
import com.example.volunteerapp.Activities.CustomViews.VolunteerProfileView;
import com.example.volunteerapp.Chat.Model.Users;
import com.example.volunteerapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterInterestedVolunteers extends RecyclerView.Adapter<AdapterInterestedVolunteers.ViewHolder> {
    private Context context;
    private List<Users> interestedVolunteers;
    private String pId;

    public AdapterInterestedVolunteers(Context context, List<Users> interestedVolunteers, String pId) {
        this.context = context;
        this.interestedVolunteers = interestedVolunteers;
        this.pId = pId;
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
        Picasso.get().load(img_url).centerCrop().resize(100,100).into(holder.profile);

        holder.accept.setOnClickListener(v -> {
            DatabaseReference participatingRef = FirebaseDatabase.getInstance().getReference()
                    .child("Participating").child(pId).child(volunteer.getUserId());

            participatingRef.setValue("Working");
            // Remove the user from the interestedVolunteers list
            interestedVolunteers.remove(position);

            // Remove the volunteer from the "Interested" reference
            DatabaseReference interestedRef = FirebaseDatabase.getInstance().getReference()
                    .child("Interested").child(pId).child(volunteer.getUserId());
            interestedRef.removeValue();

            //Remove the bookmark from volunteer
            DatabaseReference BookmarkRef = FirebaseDatabase.getInstance().getReference()
                    .child("Bookmark").child(volunteer.getUserId()).child(pId);
            BookmarkRef.removeValue();

            // Notify the adapter that the data set has changed
            notifyDataSetChanged();
        });

        holder.reject.setOnClickListener(v -> {
            // Remove the user from the interestedVolunteers list
            interestedVolunteers.remove(position);

            // Remove the volunteer from the "Interested" reference
            DatabaseReference interestedRef = FirebaseDatabase.getInstance().getReference()
                    .child("Interested").child(pId).child(volunteer.getUserId());
            interestedRef.removeValue();

            // Remove the bookmark from volunteer
            DatabaseReference bookmarkRef = FirebaseDatabase.getInstance().getReference()
                    .child("Bookmark").child(volunteer.getUserId()).child(pId);
            bookmarkRef.removeValue();

            // Notify the adapter that the data set has changed
            notifyDataSetChanged();
        });

        // Set OnClickListener to open the profile page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event, open the profile page
                Context context = view.getContext();
                Intent intent = new Intent(context, VolunteerProfileView.class);
                intent.putExtra("userId", volunteer.getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return interestedVolunteers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name, userName;
        CircleImageView profile;
        ImageButton accept,reject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.fullname);
            userName = itemView.findViewById(R.id.username);
            profile = itemView.findViewById(R.id.userimg);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
        }
    }
}

