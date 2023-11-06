package com.example.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get instance
        authProfile = FirebaseAuth.getInstance();

        //this gets us into fullscreen mode without nav bar.
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //On pressing Login button it opens LoginActivity
        Button buttonlogin = (Button) findViewById(R.id.login_button);
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        //On pressing Register button it opens Choice_Activity
        Button buttonregister = (Button) findViewById(R.id.register_button);
        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this,ChoiceActivity.class);
                 startActivity(intent);
            }
        });


    }
    //test

    //check if user is already logged in or not
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!=null){
            //start landing page
            startActivity(new Intent(MainActivity.this, VolunteerLandingPageActivity.class));
            finish();
        }
    }




}

