package com.example.volunteerapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Activities.LoginActivity;
import com.example.volunteerapp.Activities.qrCodeScanner;
import com.example.volunteerapp.Adapters.SearchAdapter;
import com.example.volunteerapp.Chat.Model.Users;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BfollowFragment extends Fragment {

    private FirebaseAuth auth;
    private RecyclerView searchRecyclerView;
    private SearchAdapter adapter;
    private FirebaseDatabase database;
    private ArrayList<Users> usersArrayList;
    private ImageButton qrCodeScan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bfollow, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        DatabaseReference organizationsRef = database.getReference().child("Organisations");

        usersArrayList = new ArrayList<>();

        searchRecyclerView = view.findViewById(R.id.recyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SearchAdapter(getActivity(), usersArrayList);
        searchRecyclerView.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        organizationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot organizationSnapshot : snapshot.getChildren()) {
                    String organizationName = organizationSnapshot.getKey();
                    DatabaseReference followersRef = database.getReference().child("Organisations")
                            .child(organizationName)
                            .child("followers");

                    followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot followerSnapshot) {
                            for (DataSnapshot userSnapshot : followerSnapshot.getChildren()) {
                                String userID = userSnapshot.getKey();
                                if (userID != null && userID.equals(auth.getCurrentUser().getUid())) {
                                    // If the user is a follower, add the organization details to the list
                                    Users users = organizationSnapshot.getValue(Users.class);
                                    if (users != null) {
                                        usersArrayList.add(users);
                                        users.setFullname("Click to view Profile");
                                    }
                                    break; // No need to check further, as the user is already found in followers
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }

        return view;
    }

    private void performSearch(String query) {
        DatabaseReference organizationsRef = database.getReference().child("Organisations");

        organizationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot organizationSnapshot : snapshot.getChildren()) {
                    String organizationName = organizationSnapshot.getKey();
                    DatabaseReference followersRef = database.getReference().child("Organisations")
                            .child(organizationName)
                            .child("followers");

                    followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot followerSnapshot) {
                            for (DataSnapshot userSnapshot : followerSnapshot.getChildren()) {
                                String userID = userSnapshot.getKey();
                                if (userID != null && userID.equals(auth.getCurrentUser().getUid())) {
                                    // If the user is a follower, check if the organization name contains the search query
                                    if (organizationName != null && organizationName.toLowerCase().contains(query.toLowerCase())) {
                                        Users users = organizationSnapshot.getValue(Users.class);
                                        if (users != null) {
                                            usersArrayList.add(users);
                                            users.setFullname("Click to view Profile");
                                        }
                                    }
                                    break; // No need to check further, as the user is already found in followers
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

}
