package com.example.volunteerapp.Adapters;

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

import com.example.volunteerapp.CustomViews.TagsInputEditText;
import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    Context context;
    List<modelPost> postList;

    String myUid;

    private DatabaseReference interestedRef; //for interested database node
    private DatabaseReference postsRef;//reference for post
    private DatabaseReference bookmarkRef;
    boolean mProcessInterested = false;

    public AdapterPosts(Context context, List<modelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        interestedRef = FirebaseDatabase.getInstance().getReference().child("Interested");
        bookmarkRef = FirebaseDatabase.getInstance().getReference().child("Bookmark");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

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
        String pTags = postList.get(position).getpTags();
        String pInterested = postList.get(position).getpTags();//Contains total number of Interested Volunteers.

        //Convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        //set data
        holder.displayName.setText(uName);
        holder.postTime.setText(pTime);
        holder.title.setText(pTitle);
        holder.description.setText(pDescription);
        holder.interested.setText(pInterested + " Are Interested");

        //Set interested for each post
        setInterested(holder,pId);

        //adding the three tags to 3 buttons
        String[] tagsArray = pTags.split(" ");
        holder.tag1.setText(tagsArray[0]);
        holder.tag2.setText(tagsArray[1]);
        holder.tag3.setText(tagsArray[2]);

        //Set postImage
        //If there is no image
        if(pImage.equals("no_image")){
            //To hide imageview
            holder.postImg.setVisibility(View.GONE);
            Picasso.get().load(uDp).into(holder.picture);
        }
        else {
            try {
                Picasso.get().load(pImage).into(holder.postImg);
                Picasso.get().load(uDp).into(holder.picture);
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


                //Toast.makeText(context, "Interested", Toast.LENGTH_SHORT).show();
                int pInterested = Integer.parseInt(postList.get(position).getpInterested());
                mProcessInterested = true;
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                //getting id of post clicked
                String postide = postList.get(position).getpId();
                bookmarkRef.child(userUid).child(postide).setValue(true);
                interestedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessInterested){
                            if(snapshot.child(postide).hasChild(myUid)){
                                //Already liked so remove like
                                postsRef.child(postide).child("pInterested").setValue(""+(pInterested-1));
                                interestedRef.child(postide).child(myUid).removeValue();
                                mProcessInterested = false;
                            }
                            else {
                                //Like it if not liked already
                                postsRef.child(postide).child("pInterested").setValue(""+(pInterested+1));
                                interestedRef.child(postide).child(myUid).setValue("Showed Interest");
                                mProcessInterested = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.notinterestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "Not Interested", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setInterested(MyHolder holder, String postKey) {
        interestedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(myUid)){
                    //User has liked the post
                    holder.interestedBtn.setText("Showed Interest");
                }
                else {
                    //User has not liked the post
                    holder.interestedBtn.setText("I am Interested");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        Button interestedBtn,notinterestedBtn,shareBtn,tag1,tag2,tag3;
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
            notinterestedBtn = itemView.findViewById(R.id.notinterestedBtn);
//            commentBtn = itemView.findViewById(R.id.commentBtn);
//            shareBtn = itemView.findViewById(R.id.shareBtn);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            tag3 = itemView.findViewById(R.id.tag3);
        }
    }
}
