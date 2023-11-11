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

import com.example.volunteerapp.Fragment.OrgBookmarkFragment;
import com.example.volunteerapp.Fragment.OrgHomeFragment;
import com.example.volunteerapp.Fragment.OrgMapFragment;
import com.example.volunteerapp.Fragment.OrgProfileFragment;

import com.example.volunteerapp.databinding.ActivityOrganisationLandingPageBinding;


public class OrganisationLandingPageActivity extends AppCompatActivity {

    ActivityOrganisationLandingPageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using view binding here
        binding = ActivityOrganisationLandingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to hide the bottom system nav bar.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Starting at Home fragment that is the feed
        replaceFragment(new OrgHomeFragment());
        binding.bottomNavigationViewOrg.setBackground(null);

        binding.bottomNavigationViewOrg.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new OrgHomeFragment());
            } else if (itemId == R.id.bookmark) {
                replaceFragment(new OrgBookmarkFragment());
            } else if (itemId == R.id.map) {
                replaceFragment(new OrgMapFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new OrgProfileFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_org, fragment);
        fragmentTransaction.commit();
    }

}
