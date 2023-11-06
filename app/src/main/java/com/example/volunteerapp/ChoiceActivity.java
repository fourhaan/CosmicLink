package com.example.volunteerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChoiceActivity extends AppCompatActivity {

    private Button volunteerSignUp;
    private Button organisationSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        //opens volunteer signup route
        volunteerSignUp = (Button) findViewById(R.id.volunteer_button);
        volunteerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this,volunteerSignUpActivity.class);
                startActivity(intent);
            }
        });

        //opens organisation signup route
        organisationSignUp = (Button) findViewById(R.id.organisation_button);
        organisationSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this,organisationSignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}