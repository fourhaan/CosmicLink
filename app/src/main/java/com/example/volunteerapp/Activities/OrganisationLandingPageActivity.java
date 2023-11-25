package com.example.volunteerapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.volunteerapp.Chat.Activity.ChatActivity;
import com.example.volunteerapp.Fragments.OrgTrackFragment;
import com.example.volunteerapp.Fragments.OrgHomeFragment;
import com.example.volunteerapp.Fragments.OrgQrFragment;
import com.example.volunteerapp.Fragments.OrgProfileFragment;

import com.example.volunteerapp.R;
import com.example.volunteerapp.databinding.ActivityOrganisationLandingPageBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;


public class OrganisationLandingPageActivity extends AppCompatActivity {

    ActivityOrganisationLandingPageBinding binding;
    private ImageView logoutClick;
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
        logoutClick = binding.logoutVol;
        logoutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(OrganisationLandingPageActivity.this);
                builder.setTitle("Logout Confirmation");
                builder.setMessage("Do you really want to logout?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Yes," perform the logout action
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();

                        // Navigate to the MainActivity
                        Intent intent = new Intent(OrganisationLandingPageActivity.this, MainActivity.class);
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
