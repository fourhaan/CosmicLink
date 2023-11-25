package com.example.volunteerapp.Activities.CustomViews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.example.volunteerapp.Adapters.AdapterPosts;
import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class viewOrgPosts extends AppCompatActivity {

    private String orgUid;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<modelPost> postList;
    AdapterPosts adapterPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_org_posts);

        orgUid = getIntent().getStringExtra("orgUid");

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.postsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();
        loadPosts();
    }

    private void loadPosts() {
        DatabaseReference orgPostsRef = FirebaseDatabase.getInstance().getReference("Posts");

        orgPostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot pidSnapshot : snapshot.getChildren()) {
                    // Check if orgUid data exists within the post
                    if (pidSnapshot.child("uid").getValue(String.class).equals(orgUid)) {
                        modelPost modelPost = pidSnapshot.getValue(com.example.volunteerapp.Models.modelPost.class);
                        postList.add(modelPost);
                    }
                }

                // Reverse the list to show the latest posts first
                Collections.reverse(postList);

                // Set adapter to recycler view
                adapterPosts = new AdapterPosts(viewOrgPosts.this, postList);
                recyclerView.setAdapter(adapterPosts);
                adapterPosts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                Toast.makeText(viewOrgPosts.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




}



