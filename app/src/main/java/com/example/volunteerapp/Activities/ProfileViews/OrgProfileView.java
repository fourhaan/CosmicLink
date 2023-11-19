package com.example.volunteerapp.Activities.ProfileViews;

import android.net.Uri;
import android.os.Bundle;

import com.example.volunteerapp.Activities.SearchActivity;
import com.example.volunteerapp.CustomViews.TagsInputEditText;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.volunteerapp.databinding.ActivityOrgProfileViewBinding;

import com.example.volunteerapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OrgProfileView extends AppCompatActivity {

    String orgUid;
    DatabaseReference orgRef;
    private TextView fullnameTextView, joiningdateTextView, mobileTextView, locationTextView, emailTextView;
    private EditText bioEditText, mobile2EditText, email2EditText, orgTypeEditText;
    private ImageView profileImageView;
    private ProgressBar profileProgressBar;
    private TextInputLayout tagsLayout;
    private TagsInputEditText tagsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_profile_view);

        orgUid = getIntent().getStringExtra("userId");

        orgRef = FirebaseDatabase.getInstance().getReference("Registered Users/"+orgUid);

        // Initialize all the XML elements
        fullnameTextView = findViewById(R.id.org_profile_name);
        emailTextView = findViewById(R.id.org_profile_email);
        joiningdateTextView = findViewById(R.id.org_profile_joining_date);
        bioEditText = findViewById(R.id.org_profile_bio);
        mobileTextView = findViewById(R.id.org_profile_mobile);
        profileImageView = findViewById(R.id.org_profile_image);
        locationTextView = findViewById(R.id.org_profile_location);
        orgTypeEditText = findViewById(R.id.org_profile_type);
        profileProgressBar = findViewById(R.id.org_profile_ProgressBar);
        mobile2EditText = findViewById(R.id.org_profile_mobile2);
        email2EditText = findViewById(R.id.ord_profile_email2);

        // Hide the progressbar and content initially
        profileProgressBar.setVisibility(View.GONE);
        findViewById(R.id.vol_profile_content).setVisibility(View.GONE);

        //initialising tags layout
        tagsLayout = findViewById(R.id.tagsLayout);
        tagsEditText = findViewById(R.id.tagsET);

        // Show the progress bar while loading
        profileProgressBar.setVisibility(View.VISIBLE);

        orgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullname").getValue(String.class);
                    String profileImageUrl = snapshot.child("image_url").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);
                    String joiningDate = snapshot.child("joining_date").getValue(String.class);
                    String mobileNo = snapshot.child("mobile").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);
                    String orgType = snapshot.child("org_type").getValue(String.class);
                    String state = snapshot.child("state").getValue(String.class);
                    String location = city + ", " + state;
                    String tags = snapshot.child("tags").getValue(String.class);
                    String mobileNo2 = snapshot.child("mobile_2").getValue(String.class);
                    String email2 = snapshot.child("email_2").getValue(String.class);

                    // Set the retrieved data to the TextViews
                    fullnameTextView.setText(fullName);
                    bioEditText.setText("We come under :\n"+orgType+"\n\n"+bio);
                    joiningdateTextView.setText(joiningDate);
                    mobileTextView.setText("+91-"+mobileNo);
                    emailTextView.setText(email);
                    locationTextView.setText(location);
                    orgTypeEditText.setText(orgType);
                    if(mobileNo2==null){
                        mobile2EditText.setText("");
                    }
                    else{
                        mobile2EditText.setText("+91-"+mobileNo2);
                    }
                    email2EditText.setText(email2);


                    //set tags to tagsEditText
                    tagsEditText.setText(tags);

                    // Load and display the profile picture using Picasso
                    // doing little bit of compression by minimizing size to 200x200
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
                    findViewById(R.id.vol_profile_content).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //error
            }
        });

    }
}