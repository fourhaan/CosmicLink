package com.example.volunteerapp.AdminPanel.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.AdminPanel.Models.ReportedPost;
import com.example.volunteerapp.AdminPanel.Adapters.ReportedPostsAdapter;
import com.example.volunteerapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class ReportedPostsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReportedPostsAdapter reportedPostsAdapter;
    private List<ReportedPost> reportedPostsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reported_posts, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewReportedPosts);
        reportedPostsList = new ArrayList<>();
        reportedPostsAdapter = new ReportedPostsAdapter(getContext(), reportedPostsList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(reportedPostsAdapter);

        // Call method to fetch and display reported posts
        fetchReportedPosts();

        return view;
    }

    private void fetchReportedPosts() {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Reports");

        reportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportedPostsList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String pid = postSnapshot.getKey();

                    for (DataSnapshot userSnapshot : postSnapshot.getChildren()) {
                        String uid = userSnapshot.getKey();

                        ReportedPost reportedPost = userSnapshot.getValue(ReportedPost.class);
                        reportedPost.setPostId(pid);
                        reportedPost.setReportedById(uid);

                        // Set boolean values based on the structure in your Firebase database
                        if(userSnapshot.child("Harassment").getValue(Boolean.class)!=null)
                        reportedPost.setHarassment(userSnapshot.child("Harassment").getValue(Boolean.class));

                        if(userSnapshot.child("Inappropriate Content").getValue(Boolean.class)!=null)
                            reportedPost.setInappropriateContent(userSnapshot.child("Inappropriate Content").getValue(Boolean.class));

                        if(userSnapshot.child("Other").getValue(Boolean.class)!=null)
                            reportedPost.setOther(userSnapshot.child("Other").getValue(Boolean.class));

                        if(userSnapshot.child("Spam").getValue(Boolean.class)!=null)
                            reportedPost.setSpam(userSnapshot.child("Spam").getValue(Boolean.class));

                        reportedPostsList.add(reportedPost);
                    }
                }

                reportedPostsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }


}

