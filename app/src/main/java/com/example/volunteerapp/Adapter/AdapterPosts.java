package com.example.volunteerapp.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //Get data
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();

        //Convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        //set data
        holder.displayName.setText(uName);
        holder.postTime.setText(pTime);
        holder.title.setText(pTitle);
        holder.description.setText(pDescription);

        //Set postImage
        //If there is no image
        if(pImage.equals("no_image")){
            //To hide imageview
            holder.postImg.setVisibility(View.GONE);
        }
        else {
            try {
                Picasso.get().load(pImage).into(holder.postImg);
            } catch (Exception e) {

            }
        }

        //Handle click buttons
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });
        holder.interestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Interested", Toast.LENGTH_SHORT).show();
            }
        });
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
        }
    }
}
