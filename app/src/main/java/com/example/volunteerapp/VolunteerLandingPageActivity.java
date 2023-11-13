package com.example.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.volunteerapp.Fragment.VolBookmarkFragment;
import com.example.volunteerapp.Fragment.VolHomeFragment;
import com.example.volunteerapp.Fragment.VolMapFragment;
import com.example.volunteerapp.Fragment.VolProfileFragment;
import com.example.volunteerapp.databinding.ActivityVolunteerLandingPageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class VolunteerLandingPageActivity extends AppCompatActivity {

    ActivityVolunteerLandingPageBinding binding;
    private ImageView chatClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using view binding here
        binding = ActivityVolunteerLandingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to hide the bottom system nav bar.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        chatClick = binding.chatVol;

        chatClick.setOnClickListener(v -> {
            Intent intent = new Intent(VolunteerLandingPageActivity.this, ChatActivity.class);
            startActivity(intent);
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_vol, fragment);
        fragmentTransaction.commit();
    }

}