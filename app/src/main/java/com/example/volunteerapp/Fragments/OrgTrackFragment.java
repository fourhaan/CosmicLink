package com.example.volunteerapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Adapters.AdapterOrgTrackPosts;
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

public class OrgTrackFragment extends Fragment {
    RecyclerView recyclerView;
    List<modelPost> orgPostList;
    AdapterOrgTrackPosts adapterOrgTrackPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_track, container, false);

        recyclerView = view.findViewById(R.id.orgPostsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        orgPostList = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String orgId = user.getUid();
            loadOrgPosts(orgId);
        }

        return view;
    }

    private void loadOrgPosts(String organizationId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts");

        // Query to retrieve posts for the specific organization
        postsRef.orderByChild("uid").equalTo(organizationId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        orgPostList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            modelPost modelPost = ds.getValue(modelPost.class);
                            orgPostList.add(modelPost);
                        }

                        adapterOrgTrackPosts = new AdapterOrgTrackPosts(getActivity(), orgPostList);
                        recyclerView.setAdapter(adapterOrgTrackPosts);
                        adapterOrgTrackPosts.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error if needed
                    }
                });
    }
}
