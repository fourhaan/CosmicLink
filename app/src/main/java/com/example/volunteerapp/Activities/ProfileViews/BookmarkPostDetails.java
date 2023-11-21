package com.example.volunteerapp.Activities.ProfileViews;

import android.icu.text.CaseMap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Adapters.AdapterPosts;
import com.example.volunteerapp.Adapters.SearchAdapter;
import com.example.volunteerapp.Chat.Model.Users;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BookmarkPostDetails extends AppCompatActivity {
    String postUid;
    DatabaseReference postRef;
    FirebaseAuth auth;
    RecyclerView searchRecyclerView;
    SearchAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView picture,postImg,profileImageView;
    TextView displayName,postTime,title,description,hours,interested;
    ImageButton moreBtn;
    Button interestedBtn,notinterestedBtn,shareBtn,tag1,tag2,tag3,addressBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmarked_post_view);
        postUid = getIntent().getStringExtra("pId");
        postRef = FirebaseDatabase.getInstance().getReference("Posts/"+postUid);

        //Initialize the xml
        displayName = findViewById(R.id.displayName);
        postTime = findViewById(R.id.postTime);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        hours = findViewById(R.id.hours);
        profileImageView = findViewById(R.id.picture);
        postImg = findViewById(R.id.postImg);
        interested = findViewById(R.id.interested);
        tag1 = findViewById(R.id.tag1);
        tag2 = findViewById(R.id.tag2);
        tag3 = findViewById(R.id.tag3);
        addressBtn = findViewById(R.id.address_show);
        postImg.setVisibility(View.GONE);

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String uName = snapshot.child("uName").getValue(String.class);
                    String pdescription = snapshot.child("pDescr").getValue(String.class);
                    String pimg = snapshot.child("pImage").getValue(String.class);
                    String ptitle = snapshot.child("pTitle").getValue(String.class);
                    String pTags = snapshot.child("pTags").getValue(String.class);
                    String pinterested = snapshot.child("pInterested").getValue(String.class);
                    String pTime = snapshot.child("pTime").getValue(String.class);
                    String pid = snapshot.child("pId").getValue(String.class);
                    String uDp = snapshot.child("uDp").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String[] tagsArray = pTags.split(" ");
                    tag1.setText(tagsArray[0]);
                    tag2.setText(tagsArray[1]);
                    tag3.setText(tagsArray[2]);

                    //Convert timestamp to dd/mm/yyyy hh:mm am/pm
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTime));
                    String Time = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

                    // Set the retrieved data to the TextViews
                    displayName.setText(uName);
                    title.setText(ptitle);
                    description.setText(pdescription);
                    addressBtn.setText(address);
                    interested.setText(pinterested+"+"+" Interests");
                    postTime.setText(Time);

                    Picasso.get().load(uDp).into(profileImageView);


                    if (pimg != null) {
                        if(pimg=="no_image") {
                        }
                        else {
                            postImg.setVisibility(View.VISIBLE);
                            Picasso.get()
                                    .load(pimg)
                                    .resize(200, 200)  // Set the desired image size
                                    .centerCrop()      // Crop the image to fit the ImageView
                                    .into(postImg);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
