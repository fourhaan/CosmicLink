package com.example.volunteerapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Adapters.AdapterMyPosts;
import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrgHomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private List<modelPost> postList;
    private AdapterMyPosts adapterMyPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.orgPostsRecyclerView);
        postList = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String orgId = user.getUid();
            loadPosts(orgId);
        }

        return view;
    }

    private void loadPosts(String organizationId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts");

        // Query to retrieve posts for the specific organization
        postsRef.orderByChild("uid").equalTo(organizationId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        postList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            modelPost modelPost = ds.getValue(modelPost.class);
                            postList.add(modelPost);
                        }
                        // Setup RecyclerView and Adapter here
                        setupRecyclerView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error if needed
                    }
                });
    }

    private void setupRecyclerView() {
        // Assuming you have a RecyclerView with id orgPostsRecyclerView in your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterMyPosts = new AdapterMyPosts(getActivity(), postList);
        recyclerView.setAdapter(adapterMyPosts);
        adapterMyPosts.notifyDataSetChanged();
    }
}
