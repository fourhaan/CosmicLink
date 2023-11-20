package com.example.volunteerapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;

import com.example.volunteerapp.Chat.Activity.ChatActivity;
import com.example.volunteerapp.Fragments.VolBookmarkFragment;
import com.example.volunteerapp.Fragments.VolHomeFragment;
import com.example.volunteerapp.Fragments.VolMapFragment;
import com.example.volunteerapp.Fragments.VolProfileFragment;
import com.example.volunteerapp.R;
import com.example.volunteerapp.databinding.ActivityVolunteerLandingPageBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VolunteerLandingPageActivity extends AppCompatActivity {

    private static final int GPS_REQUEST_CODE = 9001;
    private static final int LOCATION_REQUEST_CODE = 1001;
    ActivityVolunteerLandingPageBinding binding;
    private ImageView chatClick,locationClick;
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

        chatClick = binding.chatVol;
        chatClick.setOnClickListener(v -> {
            Intent intent = new Intent(VolunteerLandingPageActivity.this, ChatActivity.class);
            startActivity(intent);
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
            } else if (itemId == R.id.map) {
                replaceFragment(new VolMapFragment());
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