package com.example.volunteerapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.volunteerapp.Chat.Activity.ChatActivity;
import com.example.volunteerapp.Fragments.OrgTrackFragment;
import com.example.volunteerapp.Fragments.OrgHomeFragment;
import com.example.volunteerapp.Fragments.OrgQrFragment;
import com.example.volunteerapp.Fragments.OrgProfileFragment;

import com.example.volunteerapp.R;
import com.example.volunteerapp.databinding.ActivityOrganisationLandingPageBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class OrganisationLandingPageActivity extends AppCompatActivity {

    ActivityOrganisationLandingPageBinding binding;
    private ImageView chatClick;
    private FloatingActionButton addPost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using view binding here
        binding = ActivityOrganisationLandingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to hide the bottom system nav bar.
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //chat button on top of framelayout
        chatClick = binding.chatOrg;

        chatClick.setOnClickListener(v -> {
            Intent intent = new Intent(OrganisationLandingPageActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        addPost = binding.floatingActionButtonOrg;

        addPost.setOnClickListener(v -> {
            Intent intent = new Intent(OrganisationLandingPageActivity.this, AddPostActivity.class);
            startActivity(intent);
        });

        // Starting at Home fragment that is the feed
        replaceFragment(new OrgHomeFragment());
        binding.bottomNavigationViewOrg.setBackground(null);

        binding.bottomNavigationViewOrg.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new OrgHomeFragment());
            } else if (itemId == R.id.bookmark) {
                replaceFragment(new OrgTrackFragment());
            } else if (itemId == R.id.QR) {
                replaceFragment(new OrgQrFragment());
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
