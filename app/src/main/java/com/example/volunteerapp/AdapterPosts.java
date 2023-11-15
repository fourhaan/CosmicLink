package com.example.volunteerapp;

import android.content.Context;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Models.modelPost;

import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    Context context;
    List<modelPost> postList;

    public AdapterPosts(Context context, List<modelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate layout row_posts.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //Get data

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //View holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //This is the views from row_posts.xml
        ImageView picture,postImg;
        TextView displayName,postTime,title,description,interested;
        ImageButton moreBtn;
        Button interestedBtn,commentBtn,shareBtn;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.picture);
            postImg = itemView.findViewById(R.id.postImg);
            displayName = itemView.findViewById(R.id.displayName);
            postTime = itemView.findViewById(R.id.postTime);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            interested = itemView.findViewById(R.id.interested);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            interestedBtn = itemView.findViewById(R.id.interestedBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
        }
    }
}
