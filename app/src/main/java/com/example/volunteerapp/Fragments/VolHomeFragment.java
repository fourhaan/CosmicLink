package com.example.volunteerapp.Fragments;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.volunteerapp.Adapters.AdapterPosts;
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
import java.util.Collections;
import java.util.List;

public class VolHomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<modelPost> postList;
    AdapterPosts adapterPosts;
    private TextView addressTv;
    private String volFullAddress;
    private double latitude,longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vol_home, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.postsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        addressTv = view.findViewById(R.id.vol_address);
        loadAddressFromFirebase();

        //Show newest post first.
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //Set the layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();
        loadPosts();
        return view;
    }

    private void loadAddressFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            // Reference to the database path "Registered Users/Uid/"
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(uid);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        volFullAddress = snapshot.child("address").getValue(String.class);
                        // Update the TextView with the address
                        addressTv.setText("Current Address : "+ volFullAddress);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error if needed
                }
            });
        }
    }

    private void loadPosts(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //We will get all data from this reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    modelPost modelPost = ds.getValue(com.example.volunteerapp.Models.modelPost.class);
                    latitude = ds.child("latitude").getValue(double.class);
                    longitude = ds.child("longitude").getValue(double.class);
                    postList.add(modelPost);
                    sortPostsByDistance();
                    adapterPosts = new AdapterPosts(getActivity(),postList);
                    //Set adapter to recycler view
                    recyclerView.setAdapter(adapterPosts);
                    adapterPosts.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //If there is an error
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortPostsByDistance() {
        // Get the volunteer's location (latitude and longitude)
        double volLatitude = latitude;
        double volLongitude = longitude;

        // Create a Location object for the volunteer
        Location volunteerLocation = new Location("Volunteer");
        volunteerLocation.setLatitude(volLatitude);
        volunteerLocation.setLongitude(volLongitude);

        // Sort the postList based on distance to the volunteer
        Collections.sort(postList, (post1, post2) -> {
            double post1Latitude = post1.getLatitude();
            double post1Longitude = post1.getLongitude();

            double post2Latitude = post2.getLatitude();
            double post2Longitude = post2.getLongitude();

            // Create Location objects for each post
            Location post1Location = new Location("Post1");
            post1Location.setLatitude(post1Latitude);
            post1Location.setLongitude(post1Longitude);

            Location post2Location = new Location("Post2");
            post2Location.setLatitude(post2Latitude);
            post2Location.setLongitude(post2Longitude);

            // Compare distances to the volunteer
            float distanceToPost1 = volunteerLocation.distanceTo(post1Location);
            float distanceToPost2 = volunteerLocation.distanceTo(post2Location);

            return Float.compare(distanceToPost1, distanceToPost2);
        });
    }


}