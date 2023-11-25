package com.example.volunteerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Activities.InterestedActivity;
import com.example.volunteerapp.Activities.OrgParticipatingActivity;
import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterOrgTrackPosts extends RecyclerView.Adapter<AdapterOrgTrackPosts.ViewHolder> {
    private Context context;
    private List<modelPost> orgPostList;

    public AdapterOrgTrackPosts(Context context, List<modelPost> orgPostList) {
        this.context = context;
        this.orgPostList = orgPostList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_org_posts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        modelPost post = orgPostList.get(position);

        holder.orgPostTitle.setText(post.getpTitle());
        holder.orgPostDate.setText(post.getDate());
        holder.orgPostInterests.setText(post.getpInterested() + " Interests");
        String pImage = orgPostList.get(position).getpImage();

        if(pImage.equals("no_image")){
            holder.postImg.setImageResource(R.drawable.image_holder);
        }
        else {
            try {
                Picasso.get().load(pImage).centerCrop().resize(100,100).into(holder.postImg);
            } catch (Exception e) {

            }
        }

        holder.addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event, open the profile page
                Context context = view.getContext();
                Intent intent = new Intent(context, InterestedActivity.class);
                intent.putExtra("pId", post.getpId());
                context.startActivity(intent);
            }
        });
        holder.checkActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, OrgParticipatingActivity.class);
                intent.putExtra("pId", post.getpId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orgPostList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orgPostTitle, orgPostDate, orgPostInterests;
        CircleImageView postImg;
        ImageButton addUser, checkActivity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orgPostTitle = itemView.findViewById(R.id.orgPostTitle);
            orgPostDate = itemView.findViewById(R.id.orgPostDate);
            orgPostInterests = itemView.findViewById(R.id.orgPostInterests);
            postImg = itemView.findViewById(R.id.orgPostImg);
            addUser = itemView.findViewById(R.id.add_vol);
            checkActivity = itemView.findViewById(R.id.check_activity);
        }

    }
}
