package com.example.volunteerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Activities.CustomViews.BookmarkPostDetails;
import com.example.volunteerapp.Activities.VolTaskActivity;
import com.example.volunteerapp.Chat.Activity.chatwindo;
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
    private boolean isBookmarkButtonEnabled = true;
    //private DatabaseReference bookmarkRef;
    public BookmarkAdapter(Context context, List<modelPost> bookmarkedList) {
        this.context = context;
        this.bookmarkedList = bookmarkedList;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        //bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmark").child(userId);
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
        modelPost post = bookmarkedList.get(position);

        String pId = bookmarkedList.get(position).getpId();
        String pTitle = bookmarkedList.get(position).getpTitle();
        String pImage = bookmarkedList.get(position).getpImage();
        String pTags = bookmarkedList.get(position).getpTags();
        String uId = bookmarkedList.get(position).getUid();

        //To set data

        holder.title.setText(pTitle);

        //Set postImage
        //If there is no image
        if(pImage.equals("no_image")){
            holder.postImg.setImageResource(R.drawable.image_holder);
        }
        else {
            try {
                Picasso.get().load(pImage).centerCrop().resize(100,100).into(holder.postImg);
            } catch (Exception e) {

            }
        }

        // Set OnClickListener to open the profile page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String myuId = user.getUid();
            @Override
            public void onClick(View view) {
                // Handle the click event, open the profile page
                if(isBookmarkButtonEnabled) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookmarkPostDetails.class);
                    intent.putExtra("pId", post.getpId());
                    context.startActivity(intent);
                }
                else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, VolTaskActivity.class);
                    intent.putExtra("pId", post.getpId());
                    intent.putExtra("uId",myuId);
                    intent.putExtra("workhours",post.getWorkhours());
                    context.startActivity(intent);
                }
            }
        });

        if (isBookmarkButtonEnabled) {
            holder.bookmarkedButton.setVisibility(View.VISIBLE);
            holder.chatbutton.setVisibility(View.GONE);
            holder.status.setText("Click to View Post Info ");
        } else {
            holder.bookmarkedButton.setVisibility(View.GONE);
            holder.chatbutton.setVisibility(View.VISIBLE);
            holder.status.setText("Click to View Tasks ");
        }

        holder.chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context.getApplicationContext(), chatwindo.class);
                intent.putExtra("uid",uId);
                context.startActivity(intent);
            }
        });

        holder.bookmarkedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    // Remove the post from the "Bookmark" reference for the current user
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = user.getUid();
                    DatabaseReference bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmark").child(userId).child(pId);
                    bookmarkRef.removeValue();

                    // Remove the post from the "Interested" reference for the current user
                    DatabaseReference interestedRef = FirebaseDatabase.getInstance().getReference().child("Interested").child(pId).child(userId);
                    interestedRef.removeValue();

                    // Retrieve the current value of pInterests
                    int currentInterests = Integer.parseInt(post.getpInterested());

                    // If the current value is greater than 0, decrement it by 1
                    if (currentInterests > 0) {
                        currentInterests--;
                    }

                    // Update the pInterests in the Firebase Realtime Database
                    DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(pId);
                    postsRef.child("pInterested").setValue(String.valueOf(currentInterests));

                    // Remove the post from the bookmarkedList
                    bookmarkedList.remove(position);

                    // Notify the adapter that the data set has changed
                    notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return bookmarkedList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView postImg;
        TextView title,status;
        ImageButton bookmarkedButton,chatbutton;


        public MyHolder(@NonNull View itemView) {

            super(itemView);
            postImg = itemView.findViewById(R.id.bookmarkedPic);
            title = itemView.findViewById(R.id.bookmarkedText);
            bookmarkedButton = itemView.findViewById(R.id.bookmarkedBtn);
            status = itemView.findViewById(R.id.below_Text);
            chatbutton = itemView.findViewById(R.id.chatBtn);
        }
    }

    public void setBookmarkButtonEnabled(boolean enabled) {
        isBookmarkButtonEnabled = enabled;
        notifyDataSetChanged();
    }


}