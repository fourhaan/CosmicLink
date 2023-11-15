package com.example.volunteerapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //get instance
//        authProfile = FirebaseAuth.getInstance();

//        //this gets us into fullscreen mode without nav bar.
//        this.getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //On pressing Login button it opens LoginActivity
        Button buttonlogin = (Button) findViewById(R.id.login_button);
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        //On pressing Register button it opens Choice_Activity
        Button buttonregister = (Button) findViewById(R.id.register_button);
        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this,ChoiceActivity.class);
                 startActivity(intent);
            }
        });
    }

//    // Check if user is already logged in or not
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = authProfile.getCurrentUser();
//        if (currentUser != null) {
//            // Get the UID of the logged-in user
//            String uid = currentUser.getUid();
//
//            // Retrieve the user's data from Firebase Realtime Database
//            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(uid);
//            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        String userType = snapshot.child("usertype").getValue(String.class);
//                        // Check the user type
//                        if ("volunteer".equals(userType)) {
//                            // Redirect to the Volunteer Landing Page
//                            Intent intent = new Intent(MainActivity.this, VolunteerLandingPageActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//                        } else if ("organisation".equals(userType)) {
//                            // Redirect to the Organization Landing Page (change to the appropriate activity name)
//                            Intent intent = new Intent(MainActivity.this, OrganisationLandingPageActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            // Handle unknown user type
//                            Toast.makeText(MainActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        // Handle missing user data
//                        Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle database error
//                    Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }



