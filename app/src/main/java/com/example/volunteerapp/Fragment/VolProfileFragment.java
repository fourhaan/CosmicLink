package com.example.volunteerapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.volunteerapp.MainActivity;
import com.example.volunteerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class VolProfileFragment extends Fragment {
    private Button logoutButton;
    private TextView fullnameTextView, usernameTextView, joiningdateTextView, bioTextView, mobileTextView, locationTextView, emailTextView;
    private ImageView profileImageView;
    private ProgressBar profileProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vol_profile, container, false);

        // Initialize all the XML elements
        logoutButton = view.findViewById(R.id.vol_profile_log_out);
        fullnameTextView = view.findViewById(R.id.vol_profile_name);
        usernameTextView = view.findViewById(R.id.vol_profile_username);
        emailTextView = view.findViewById(R.id.vol_profile_email);
        joiningdateTextView = view.findViewById(R.id.vol_profile_joining_date);
        bioTextView = view.findViewById(R.id.vol_profile_bio);
        mobileTextView = view.findViewById(R.id.vol_profile_mobile);
        locationTextView = view.findViewById(R.id.vol_profile_location);
        profileImageView = view.findViewById(R.id.vol_profile_image);
        profileProgressBar = view.findViewById(R.id.vol_profile_ProgressBar);

        // Hide the progressbar and content initially
        profileProgressBar.setVisibility(View.GONE);
        view.findViewById(R.id.vol_profile_content).setVisibility(View.GONE);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the logout action here
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                // Navigate to the MainActivity
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        // Retrieve the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Reference to the user's data in Firebase Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(userId);

            // Show the progress bar while loading
            profileProgressBar.setVisibility(View.VISIBLE);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullname").getValue(String.class);
                        String userName = snapshot.child("username").getValue(String.class);
                        String profileImageUrl = snapshot.child("image_url").getValue(String.class);
                        String bio = snapshot.child("bio").getValue(String.class);
                        String joiningDate = snapshot.child("joining_date").getValue(String.class);
                        String mobileNo = snapshot.child("mobile").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String city = snapshot.child("city").getValue(String.class);
                        String state = snapshot.child("state").getValue(String.class);
                        String location = city + ", " + state;

                        // Set the retrieved data to the TextViews
                        fullnameTextView.setText(fullName);
                        usernameTextView.setText("@"+userName);
                        bioTextView.setText(bio);
                        joiningdateTextView.setText(joiningDate);
                        mobileTextView.setText(mobileNo);
                        emailTextView.setText(email);
                        locationTextView.setText(location);

                        // Load and display the profile picture using Picasso
                        //doing little bit of compression by minimizing size to 200x200
                        if (profileImageUrl != null) {
                            Picasso.get()
                                    .load(profileImageUrl)
                                    .resize(200, 200)  // Set the desired image size
                                    .centerCrop()      // Crop the image to fit the ImageView
                                    .into(profileImageView);
                        }

                        // Hide the progress bar when data is ready
                        profileProgressBar.setVisibility(View.GONE);

                        // Show the content view
                        view.findViewById(R.id.vol_profile_content).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //error
                }
            });
        }

        return view;
    }
}
