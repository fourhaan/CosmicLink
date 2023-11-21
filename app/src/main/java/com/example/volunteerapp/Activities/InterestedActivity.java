package com.example.volunteerapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.volunteerapp.Adapters.AdapterInterestedVolunteers;
import com.example.volunteerapp.Chat.Model.Users;
import com.example.volunteerapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InterestedActivity extends AppCompatActivity {

    String pId;
    List<String> interestedVolunteerUids;
    List<Users> interestedVolunteers;
    RecyclerView recyclerView;
    AdapterInterestedVolunteers adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested);

        pId = getIntent().getStringExtra("pId");

        interestedVolunteerUids = new ArrayList<>();
        interestedVolunteers = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view_interested);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdapterInterestedVolunteers(this, interestedVolunteers,pId);
        recyclerView.setAdapter(adapter);

        getInterestedVolunteers();
    }

    private void getInterestedVolunteers() {
        DatabaseReference interestedRef = FirebaseDatabase.getInstance().getReference().child("Interested").child(pId);

        interestedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                interestedVolunteerUids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String volunteerUid = snapshot.getKey();
                    interestedVolunteerUids.add(volunteerUid);
                }

                displayInterestedVolunteers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }

    private void displayInterestedVolunteers() {
        interestedVolunteers.clear();
        for (String volunteerUid : interestedVolunteerUids) {
            fetchAndDisplayUserProfile(volunteerUid);
        }
    }

    private void fetchAndDisplayUserProfile(String volunteerUid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(volunteerUid);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users userProfile = dataSnapshot.getValue(Users.class);
                interestedVolunteers.add(userProfile);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }
}
