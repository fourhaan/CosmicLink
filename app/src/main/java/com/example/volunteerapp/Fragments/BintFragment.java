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


public class BintFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<modelPost> bookmarkedLists;
    FirebaseDatabase database;
    BookmarkAdapter bookmarkAdapter;


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

        bookmarkedLists = new ArrayList<>();
        loadBookmark();
        return view;

    }

    private void loadBookmark() {
        DatabaseReference bookmarkRef = FirebaseDatabase.getInstance().getReference("Bookmark").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        bookmarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookmarkedLists.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String postId = ds.getKey(); // Get the post ID from the Bookmark/uid reference

                    if (postId != null) {
                        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);

                        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    modelPost bookmarkedList = dataSnapshot.getValue(modelPost.class);
                                    bookmarkedLists.add(bookmarkedList);

                                    // Move this line outside of the loop to prevent multiple adapter instances
                                    if (bookmarkAdapter == null) {
                                        bookmarkAdapter = new BookmarkAdapter(getActivity(), bookmarkedLists);
                                        recyclerView.setAdapter(bookmarkAdapter);
                                    } else {
                                        bookmarkAdapter.notifyDataSetChanged();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    private void performSearch(String query) {
        DatabaseReference reference = database.getReference().child("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookmarkedLists.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    String pTitle = postSnapshot.child("pTitle").getValue(String.class);

                    // Check if the post title contains the search query
                    if (pTitle != null && pTitle.toLowerCase().contains(query.toLowerCase())) {
                        Log.d("Search", "Post ID: " + postId);
                        modelPost searchList = postSnapshot.child(postId).getValue(modelPost.class);
                        bookmarkedLists.add(searchList);
                        if (bookmarkAdapter == null) {
                            bookmarkAdapter = new BookmarkAdapter(getActivity(), bookmarkedLists);
                            recyclerView.setAdapter(bookmarkAdapter);
                        } else {
                            bookmarkAdapter.notifyDataSetChanged();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }



}