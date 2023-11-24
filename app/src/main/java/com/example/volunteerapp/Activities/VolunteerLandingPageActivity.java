package com.example.volunteerapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.example.volunteerapp.Chat.Activity.ChatActivity;
import com.example.volunteerapp.Fragments.VolBookmarkFragment;
import com.example.volunteerapp.Fragments.VolHomeFragment;
import com.example.volunteerapp.Fragments.VolpartFragment;
import com.example.volunteerapp.Fragments.VolProfileFragment;
import com.example.volunteerapp.R;
import com.example.volunteerapp.databinding.ActivityVolunteerLandingPageBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class VolunteerLandingPageActivity extends AppCompatActivity {

    private static final int GPS_REQUEST_CODE = 9001;
    private static final int LOCATION_REQUEST_CODE = 1001;
    ActivityVolunteerLandingPageBinding binding;
    private ImageView logoutClick,locationClick;
    private FloatingActionButton searchClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using view binding here
        binding = ActivityVolunteerLandingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to hide the bottom system nav bar.
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        searchClick = binding.floatingActionButtonVol;
        searchClick.setOnClickListener(v -> {
            Intent intent = new Intent(VolunteerLandingPageActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        logoutClick = binding.logoutVol;
        logoutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(VolunteerLandingPageActivity.this);
                builder.setTitle("Logout Confirmation");
                builder.setMessage("Do you really want to logout?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Yes," perform the logout action
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();

                        // Navigate to the MainActivity
                        Intent intent = new Intent(VolunteerLandingPageActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "No," do nothing or handle as needed
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                builder.show();
            }
        });


        locationClick = binding.locationVol;
        locationClick.setOnClickListener(v -> {
            if (checkLocationAndGPSPermission()) {
                Intent intent = new Intent(VolunteerLandingPageActivity.this, LocationActivityForVol.class);
                startActivity(intent);
            }
        });

        // Starting at Home fragment that is the feed
        replaceFragment(new VolHomeFragment());
        binding.bottomNavigationViewVol.setBackground(null);

        binding.bottomNavigationViewVol.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new VolHomeFragment());
            } else if (itemId == R.id.bookmark) {
                replaceFragment(new VolBookmarkFragment());
            } else if (itemId == R.id.track) {
                replaceFragment(new VolpartFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new VolProfileFragment());
            }
            return true;
        });
    }

    private boolean checkLocationAndGPSPermission() {
        boolean isLocationPermissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!isLocationPermissionGranted) {
            // Location permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return false;
        }

        // Check if GPS is enabled
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            // GPS is not enabled, show settings to enable it
            showGPSEnabledDialog();
            return false;
        }

        return true;
    }

    private void showGPSEnabledDialog() {
        // Your existing code to show GPS enable dialog
        // ...

        // For example:
        new AlertDialog.Builder(this)
                .setTitle("GPS Permission")
                .setMessage("GPS is required for this app to work. Please enable GPS.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, GPS_REQUEST_CODE);
                })
                .setCancelable(false)
                .show();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_vol, fragment);
        fragmentTransaction.commit();
    }

}