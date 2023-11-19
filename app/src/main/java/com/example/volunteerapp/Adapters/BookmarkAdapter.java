package com.example.volunteerapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.MyHolder>{
    Context context;
    List<modelPost> bookmarkedList;
    private DatabaseReference bookmarkRef;
    public BookmarkAdapter(Context context, List<modelPost> bookmarkedList) {
        this.context = context;
        this.bookmarkedList = bookmarkedList;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmark").child(userId);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate layout row_bookmark.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_bookmark,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.MyHolder holder, int position) {
        String pId = bookmarkedList.get(position).getpId();
        String pTitle = bookmarkedList.get(position).getpTitle();
        String pImage = bookmarkedList.get(position).getpImage();
        String pTags = bookmarkedList.get(position).getpTags();

        //To set data

        holder.title.setText(pTitle);

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

    }

    @Override
    public int getItemCount() {
        return bookmarkedList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView postImg;
        TextView title;
        Button bookmarkedButton;


        public MyHolder(@NonNull View itemView) {

            super(itemView);
            postImg = itemView.findViewById(R.id.bookmarkedPic);
            title = itemView.findViewById(R.id.bookmarkedText);
            bookmarkedButton = itemView.findViewById(R.id.bookmarkedBtn);

        }
    }

}