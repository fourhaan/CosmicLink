package com.example.volunteerapp.Activities.CustomViews;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.volunteerapp.CustomTools.TagsInputEditText;
import com.example.volunteerapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class VolunteerProfileView extends AppCompatActivity {

    private String volUid;
    private DatabaseReference volRef;
    private TextView vol_profile_name,vol_profile_username,vol_profile_joining_date,vol_profile_hours,vol_profile_mobile,vol_profile_email,vol_profile_location,vol_hours;
    private EditText vol_profile_bio;
    private TextInputLayout tagsLayout;
    private TagsInputEditText tagsET;
    private ImageView vol_profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_profile_view);

        volUid = getIntent().getStringExtra("userId");
        volRef = FirebaseDatabase.getInstance().getReference("Registered Users/" + volUid);

        //Initializing XML
        vol_profile_name = findViewById(R.id.vol_profile_name);
        vol_profile_username = findViewById(R.id.vol_profile_username);
        vol_profile_joining_date = findViewById(R.id.vol_profile_joining_date);
        vol_profile_bio = findViewById(R.id.vol_profile_bio);
        vol_profile_hours = findViewById(R.id.vol_profile_hours);
        vol_profile_mobile = findViewById(R.id.vol_profile_mobile);
        vol_profile_email = findViewById(R.id.vol_profile_email);
        vol_profile_location = findViewById(R.id.vol_profile_location);
        vol_profile_image = findViewById(R.id.vol_profile_image);
        vol_hours = findViewById(R.id.vol_profile_hours);
        // initializing tags layout
        tagsLayout = findViewById(R.id.tagsLayout);
        tagsET = findViewById(R.id.tagsET);


        volRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullname").getValue(String.class);
                    String profileImageUrl = snapshot.child("image_url").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);
                    String joiningDate = snapshot.child("joining_date").getValue(String.class);
                    String mobileNo = snapshot.child("mobile").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String state = snapshot.child("state").getValue(String.class);
                    String city = snapshot.child("city").getValue(String.class);
                    String usertype = snapshot.child("usertype").getValue(String.class);
                    String location = city + ", " + state;
                    String tags = snapshot.child("tags").getValue(String.class);
                    int workhours = snapshot.child("workhours").getValue(Integer.class);
                    String workhoursString = String.valueOf(workhours);
                    vol_hours.setText("Total Working Hours = "+workhoursString+" hours");

                    // Set the retrieved data to the TextViews
                    vol_profile_name.setText(fullName);
                    vol_profile_bio.setText("I come under:\n "+usertype+"\n\n"+bio);
                    vol_profile_joining_date.setText(joiningDate);
                    vol_profile_mobile.setText("+91 "+mobileNo);
                    vol_profile_location.setText(location);
                    vol_profile_email.setText(email);
                    vol_profile_username.setText("@"+username);
                    // set tags to tagsEditText
                    tagsET.setText(tags);

                    if (profileImageUrl != null) {
                        Picasso.get()
                                .load(profileImageUrl)
                                .resize(200, 200)  // Set the desired image size
                                .centerCrop()      // Crop the image to fit the ImageView
                                .into(vol_profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
