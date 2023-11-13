package com.example.volunteerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Hide the action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set the app to fullscreen mode
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // Initialize Firebase Authentication
        authProfile = FirebaseAuth.getInstance();

        // Define the delay for the splash screen
        int splashScreenDelay = 3000;

        // Create a handler to delay the splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserAndRedirect();
            }
        }, splashScreenDelay);
    }

    private void checkUserAndRedirect() {
        FirebaseUser currentUser = authProfile.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userType = snapshot.child("usertype").getValue(String.class);
                        Intent intent;
                        if ("volunteer".equals(userType)) {
                            intent = new Intent(SplashScreen.this, VolunteerLandingPageActivity.class);
                        } else if ("organisation".equals(userType)) {
                            intent = new Intent(SplashScreen.this, OrganisationLandingPageActivity.class);
                        } else {
                            // Handle unknown user type
                            // You can add a default action or show an error message here
                            intent = new Intent(SplashScreen.this, MainActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        // Handle missing user data
                        // You can add a default action or show an error message here
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    }
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    // You can add a default action or show an error message here
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            });
        } else {
            // User is not logged in, go to MainActivity
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }
    }
}
