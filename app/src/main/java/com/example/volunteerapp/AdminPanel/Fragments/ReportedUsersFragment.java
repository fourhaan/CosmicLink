package com.example.volunteerapp.AdminPanel.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.volunteerapp.AdminPanel.Adapters.ReportedUsersAdapter;
import com.example.volunteerapp.AdminPanel.Models.ReportedPost;
import com.example.volunteerapp.AdminPanel.Models.ReportedUser;
import com.example.volunteerapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ReportedUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReportedUsersAdapter reportedUsersAdapter;
    private List<ReportedUser> reportedUsersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reported_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewReportedUsers);
        reportedUsersList = new ArrayList<>();
        reportedUsersAdapter = new ReportedUsersAdapter(getContext(), reportedUsersList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(reportedUsersAdapter);

        // Call method to fetch and display reported users
        fetchReportedUsers();

        return view;
    }

    private void fetchReportedUsers() {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Reported Users");

        reportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportedUsersList.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String reportedId = userSnapshot.getKey();

                    for (DataSnapshot byUserSnapshot : userSnapshot.getChildren()) {
                        String reportedById = byUserSnapshot.getKey();

                        ReportedUser reportedUser = new ReportedUser();
                        reportedUser.setTimestamp(byUserSnapshot.getValue(long.class));
                        reportedUser.setReportedByUserId(reportedById);
                        reportedUser.setReportedUserId(reportedId);

                        reportedUsersList.add(reportedUser);
                    }
                }

                reportedUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }
}
