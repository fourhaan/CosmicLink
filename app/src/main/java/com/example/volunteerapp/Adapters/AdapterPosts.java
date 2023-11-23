package com.example.volunteerapp.Adapters;

import android.content.Context;
import android.content.Intent;
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

import com.example.volunteerapp.Activities.CustomViews.OrgProfileView;
import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
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
        String Address = postList.get(position).getAddress();
        String pInterested = postList.get(position).getpInterested();//Contains total number of Interested Volunteers.
        long workhours = postList.get(position).getWorkhours();
        String dateofevent = postList.get(position).getDate();

        //Convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        //set data
        holder.displayName.setText(uName);
        holder.postTime.setText(pTime);
        holder.title.setText(pTitle);
        holder.description.setText(pDescription);
        holder.interested.setText(pInterested+"+" + " Interests");
        holder.addressBtn.setText("Location : "+Address);
        holder.date.setText("Start of Mission : "+dateofevent);
        holder.hours.setText("Total Mission Work Hours : "+workhours+" hours");

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
        holder.moreBtn.setVisibility(View.GONE);

        holder.interestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pInterested = Integer.parseInt(postList.get(holder.getAdapterPosition()).getpInterested());
                mProcessInterested = true;
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String postide = postList.get(holder.getAdapterPosition()).getpId();
                isParticipating(postide, new ParticipatingCallback() {
                    @Override
                    public void onResult(boolean isParticipating) {
                        if (!isParticipating) {
                            interestedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (mProcessInterested) {
                                        if (snapshot.child(postide).hasChild(myUid)) {
                                            // Already liked so remove like
                                            postsRef.child(postide).child("pInterested").setValue("" + (pInterested - 1));
                                            interestedRef.child(postide).child(myUid).removeValue();
                                            bookmarkRef.child(userUid).child(postide).removeValue(); // Fix here
                                            mProcessInterested = false;
                                        } else {
                                            // Like it if not liked already
                                            postsRef.child(postide).child("pInterested").setValue("" + (pInterested + 1));
                                            interestedRef.child(postide).child(myUid).setValue("Showed Interest");
                                            bookmarkRef.child(userUid).child(postide).setValue("true"); // Fix here
                                            mProcessInterested = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Already Participating", Toast.LENGTH_SHORT).show();
                            holder.interestedBtn.setText("Participating");
                        }
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
        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrgProfileView.class);
                intent.putExtra("userId", postList.get(position).getUid());
                context.startActivity(intent);
            }
        });
    }

    private void setInterested(MyHolder holder, String postKey) {
        interestedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(myUid)){
                    //User has liked the post
                    holder.interestedBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_thumb_up_blue,0,0,0);
                    holder.interestedBtn.setText("Showed Interest");
                }
                else {
                    //User has not liked the post
                    holder.interestedBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_thumb_up_24,0,0,0);
                    holder.interestedBtn.setText("I am Interested");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface ParticipatingCallback {
        void onResult(boolean isParticipating);
    }

    // Modify the isParticipating method
    private void isParticipating(String postId, ParticipatingCallback callback) {
        DatabaseReference participatingRef = FirebaseDatabase.getInstance().getReference("Participating").child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        participatingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the snapshot has any children
                boolean isParticipating = snapshot.exists();
                callback.onResult(isParticipating);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                callback.onResult(false); // Assume not participating in case of an error
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
        Button interestedBtn,notinterestedBtn,shareBtn,tag1,tag2,tag3,addressBtn,date,hours;
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
            addressBtn = itemView.findViewById(R.id.address_show);
            date = itemView.findViewById(R.id.date);
            hours = itemView.findViewById(R.id.hours);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            tag3 = itemView.findViewById(R.id.tag3);
        }
    }



}