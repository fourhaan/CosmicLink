package com.example.volunteerapp.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunteerapp.Chat.Model.Users;
import com.example.volunteerapp.Adapters.SearchAdapter;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView searchRecyclerView;
    SearchAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageButton qrCodeScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        DatabaseReference reference = database.getReference().child("Organisations");

        usersArrayList = new ArrayList<>();

        searchRecyclerView = findViewById(R.id.recyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(SearchActivity.this, usersArrayList);
        searchRecyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when the user submits the query (e.g., press search button)
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as the user types (real-time search)
                performSearch(newText);
                return true;
            }
        });

        qrCodeScan = findViewById(R.id.qr_code_scan_button);
        qrCodeScan.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, qrCodeScanner.class);
            //            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear(); // Clear the list before adding updated data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                    users.setFullname("Click to view Profile");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish this activity to prevent returning here after logging in
        }
    }

    private void performSearch(String query) {
        DatabaseReference reference = database.getReference().child("Organisations");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                for (DataSnapshot organizationSnapshot : snapshot.getChildren()) {
                    String organizationName = organizationSnapshot.getKey();

                    // Check if the organization name contains the search query
                    if (organizationName != null && organizationName.toLowerCase().contains(query.toLowerCase())) {
                        Log.d("Search", "Organization Name: " + organizationName);

                        // Extract organization details
                        String email = organizationSnapshot.child("email").getValue(String.class);
//                        String fullname = organizationSnapshot.child("fullname").getValue(String.class);
                        String fullname = "Click to view Profile";
                        String image_url = organizationSnapshot.child("image_url").getValue(String.class);
                        String userId = organizationSnapshot.child("userId").getValue(String.class);
                        String username = organizationSnapshot.child("username").getValue(String.class);

                        // Create a Users object with the organization details
                        Users users = new Users();
                        users.setFullname(fullname);
                        users.setEmail(email);
                        users.setImage_url(image_url);
                        users.setUserId(userId);
                        users.setUsername(username);

                        usersArrayList.add(users);
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
