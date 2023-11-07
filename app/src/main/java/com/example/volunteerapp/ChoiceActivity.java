package com.example.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ChoiceActivity extends AppCompatActivity {

    private ImageButton volunteerSignUp,organisationSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        //opens volunteer signup route
        volunteerSignUp = (ImageButton) findViewById(R.id.volunteer_button);
        volunteerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this,volunteerSignUpActivity.class);
                startActivity(intent);
            }
        });

        //opens organisation signup route
        organisationSignUp = (ImageButton) findViewById(R.id.organisation_button);
        organisationSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this,organisationSignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}