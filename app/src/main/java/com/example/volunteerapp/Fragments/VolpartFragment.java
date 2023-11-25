package com.example.volunteerapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.volunteerapp.Adapters.AdapterPosts;
import com.example.volunteerapp.Adapters.BookmarkAdapter;
import com.example.volunteerapp.Chat.Model.Users;
import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class VolpartFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<modelPost> participatingLists;
    FirebaseDatabase database;
    BookmarkAdapter participatingAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_int, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.bookmarkRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //Show newest post first.
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        //Set the layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        participatingLists = new ArrayList<>();
        loadParticipating();

        return view;
    }

    private void loadParticipating() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference participatingRef = FirebaseDatabase.getInstance().getReference("Participating");

        participatingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                participatingLists.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    // ds.getKey() gives the pid
                    String postId = ds.getKey(); // Get the post ID from the Participating node

                    // Check if there are userIds under this pid
                    if (ds.hasChildren()) {
                        for (DataSnapshot userIdSnapshot : ds.getChildren()) {
                            String userId = userIdSnapshot.getKey();

                            // Compare userId with the current user's ID
                            if (userId != null && userId.equals(currentUserUid)) {
                                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);

                                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            modelPost participatingList = dataSnapshot.getValue(modelPost.class);
                                            participatingLists.add(participatingList);

                                            // Move this line outside of the loop to prevent multiple adapter instances
                                            if (participatingAdapter == null) {
                                                participatingAdapter = new BookmarkAdapter(getActivity(), participatingLists);
                                                participatingAdapter.setBookmarkButtonEnabled(false);
                                                recyclerView.setAdapter(participatingAdapter);
                                            } else {
                                                participatingAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle error
                                    }
                                });

                                // Break the inner loop as we found a match
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }



}