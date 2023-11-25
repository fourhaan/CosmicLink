package com.example.volunteerapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.volunteerapp.Adapters.AdapterInterestedVolunteers;
import com.example.volunteerapp.Adapters.AdapterParticipatingVolunteers;
import com.example.volunteerapp.Chat.Model.Users;
import com.example.volunteerapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrgParticipatingActivity extends AppCompatActivity {

    String pId;
    List<String> participatingVolunteerUids;
    List<Users> participatingVolunteers;
    RecyclerView recyclerView;
    AdapterParticipatingVolunteers adapter;
    Button common_task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participating);

        pId = getIntent().getStringExtra("pId");

        participatingVolunteerUids = new ArrayList<>();
        participatingVolunteers = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view_interested);
        common_task = findViewById(R.id.add_common_task);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdapterParticipatingVolunteers(this, participatingVolunteers,pId);
        recyclerView.setAdapter(adapter);

        common_task.setOnClickListener(v -> {
            Intent intent = new Intent(OrgParticipatingActivity.this,CommonTaskActivity.class);
            intent.putExtra("pId",pId);
            startActivity(intent);
        });

        getInterestedVolunteers();
    }

    private void getInterestedVolunteers() {
        DatabaseReference interestedRef = FirebaseDatabase.getInstance().getReference().child("Participating").child(pId);

        interestedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                participatingVolunteerUids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String volunteerUid = snapshot.getKey();
                    participatingVolunteerUids.add(volunteerUid);
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
        participatingVolunteers.clear();
        for (String volunteerUid : participatingVolunteerUids) {
            fetchAndDisplayUserProfile(volunteerUid);
        }
    }

    private void fetchAndDisplayUserProfile(String volunteerUid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(volunteerUid);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users userProfile = dataSnapshot.getValue(Users.class);
                participatingVolunteers.add(userProfile);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }
}
