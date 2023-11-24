package com.example.volunteerapp.Fragments;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Arrays;
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
    private Button sortingbtn;

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

        sortingbtn = view.findViewById(R.id.sorting_button);
        sortingbtn.setOnClickListener(v -> {
            showSortingDialog();
        });

        sortingbtn.setText("Sort By : Location");

        //Show newest post first.
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //Set the layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();
        loadPosts(0);
        return view;
    }

    private void showSortingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort By")
                .setItems(R.array.sorting_methods, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Location (Default)
                            sortingbtn.setText("Sort By : Location");
                            loadPosts(1);
                            break;
                        case 1:
                            // Date of Event
                            sortingbtn.setText("Sort By : Date");
                            loadPosts(2);
                            break;
                        case 2:
                            // Work Hours
                            sortingbtn.setText("Sort By : Work Hours");
                            loadPosts(3);
                            break;
                        case 3:
                            // Tags (Match User's Tags)
                            sortingbtn.setText("Sort By : Tags");
                            loadPosts(4);
                            break;
                        case 4:
                            // Latest posts
                            sortingbtn.setText("Sort By : Latest");
                            loadPosts(5);
                            break;
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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
                    } else if(!snapshot.child("address").exists()){
                        longitude = 0.0;
                        latitude = 0.0;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error if needed
                }
            });
        }
    }

    private void loadPosts(int choice) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        // We will get all data from this reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    modelPost modelPost = ds.getValue(com.example.volunteerapp.Models.modelPost.class);

                    // Check if the current post's pId is present in the Participating reference
                    if (isParticipating(modelPost.getpId())) {
                        postList.add(modelPost);
                    }
                }
                if(choice == 1) {
                    sortPostsByDistance();
                } else if (choice == 2){
                    sortPostsByDate();
                } else if (choice == 3) {
                    sortPostsByWorkHours();
                } else if (choice ==4){
                    sortPostsByTags();
                } else if(choice == 5) {
                    sortPostsByTime();
                } else {
                    //error
                }

                adapterPosts = new AdapterPosts(getActivity(), postList);
                // Set adapter to recycler view
                recyclerView.setAdapter(adapterPosts);
                adapterPosts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // If there is an error
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to check if the volunteer is already participating in the post
    private boolean isParticipating(String postId) {
        DatabaseReference participatingRef = FirebaseDatabase.getInstance().getReference("Participating")
                .child(postId)
                .child(firebaseAuth.getCurrentUser().getUid());

        return participatingRef != null;
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

            return Float.compare(distanceToPost2, distanceToPost1);
        });
    }

    private void sortPostsByDate() {
        // Sort the postList based on date of the event
        Collections.sort(postList, (post1, post2) -> {
            String date1 = post1.getDate();
            String date2 = post2.getDate();

            return date2.compareTo(date1);
        });

        adapterPosts.notifyDataSetChanged();
    }

    private void sortPostsByWorkHours() {
        // Sort the postList based on work hours
        Collections.sort(postList, (post1, post2) -> {
            int workHours1 = post1.getWorkhours();
            int workHours2 = post2.getWorkhours();

            return Integer.compare(workHours1, workHours2);
        });

        adapterPosts.notifyDataSetChanged();
    }

    private void sortPostsByTags() {
        String userTagsString = getTag();
        List<String> userTags = Arrays.asList(userTagsString.split("\\s+"));

        Collections.sort(postList, (post1, post2) -> {
            String tagsString1 = post1.getpTags();
            String tagsString2 = post2.getpTags();

            // Check for null values
            if (tagsString1 == null) {
                tagsString1 = "";
            }
            if (tagsString2 == null) {
                tagsString2 = "";
            }

            List<String> tags1 = Arrays.asList(tagsString1.split("\\s+"));
            List<String> tags2 = Arrays.asList(tagsString2.split("\\s+"));

            // Calculate the number of common tags with the user's tags
            long commonTagsCount1 = userTags.stream().filter(tags1::contains).count();
            long commonTagsCount2 = userTags.stream().filter(tags2::contains).count();

            return Long.compare(commonTagsCount1, commonTagsCount2);
        });

        adapterPosts.notifyDataSetChanged();



    }

    private void sortPostsByTime() {
        // Sort the postList based on the time of the event
        Collections.sort(postList, (post1, post2) -> {
            String time1 = post1.getpTime();
            String time2 = post2.getpTime();

            return time1.compareTo(time2);
        });

        adapterPosts.notifyDataSetChanged();
    }




}