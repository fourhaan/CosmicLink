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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        // We will get all data from this reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelPost modelPost = ds.getValue(com.example.volunteerapp.Models.modelPost.class);
                    postList.add(modelPost);
                }
                adapterPosts = new AdapterPosts(viewOrgPosts.this, postList);
                // Set adapter to recycler view
                recyclerView.setAdapter(adapterPosts);
                adapterPosts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // If there is an error
                Toast.makeText(viewOrgPosts.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}



