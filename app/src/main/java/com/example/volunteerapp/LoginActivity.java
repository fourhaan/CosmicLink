package com.example.volunteerapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextloginEmail, editTextloginPwd;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private TextView signUpRedirect;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextloginEmail = findViewById(R.id.editText_login_email);
        editTextloginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.loginProgressBar);
        signUpRedirect = findViewById(R.id.signUpRedirectText);

        //when signup textview is pressed it opens choice-activity
        signUpRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ChoiceActivity.class));
            }
        });

        //get instance
        authProfile = FirebaseAuth.getInstance();

        //login process for user
        Button loginButton = findViewById(R.id.login_page_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextloginEmail.getText().toString();
                String textPwd = editTextloginPwd.getText().toString();

                //Basic checks for fields that are filled in editTexts
                if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginActivity.this,"Please enter the password",Toast.LENGTH_SHORT).show();
                    editTextloginPwd.setError("password is required");
                    editTextloginPwd.requestFocus();
                }
                else if(TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(LoginActivity.this, "Please enter the Email", Toast.LENGTH_SHORT).show();
                    editTextloginEmail.setError("Email is required");
                    editTextloginEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this,"Please re-enter your email",
                            Toast.LENGTH_SHORT).show();
                    editTextloginEmail.setError("Valid email is required");
                    editTextloginEmail.requestFocus();
                }
                //this means all the data entered is correct and errors handled.
                else {
                    progressBar.setVisibility(View.VISIBLE);  //starts loading animation in centre
                    loginUser(textEmail, textPwd);
                }

            }
        });

    }
    // Main login method using firebase Auth
    private void loginUser(String email , String pwd){
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    //get instance of current user
                    FirebaseUser currentUser = authProfile.getCurrentUser();
                    // check if user's email is verified or not
                    if(currentUser.isEmailVerified()) {
                        // Get the UID of the logged-in user
                        String uid = currentUser.getUid();

                        // Retrieve the user's data from Firebase Realtime Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(uid);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String userType = snapshot.child("usertype").getValue(String.class);
                                    // Check the user type
                                    if ("volunteer".equals(userType)) {
                                        // Redirect to the Volunteer Landing Page
                                        Intent intent = new Intent(LoginActivity.this, VolunteerLandingPageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else if ("organisation".equals(userType)) {
                                        // Redirect to the Organization Landing Page (change to the appropriate activity name)
                                        Intent intent = new Intent(LoginActivity.this, OrganisationLandingPageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Handle unknown user type
                                        Toast.makeText(LoginActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle missing user data
                                    Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle database error
                                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    //else send email verification , open email app and sign out
                    else {
                        currentUser.sendEmailVerification();
                        authProfile.signOut();
                        progressBar.setVisibility(View.GONE);
                        showAlertDialog(); //showing a alert dialog to inform user about email verification
                    }
                }
                else {
                    try{
                        throw task.getException();
                        //exception handling below
                        //So according to https://github.com/firebase/firebase-js-sdk/issues/7661
                        // new changes are made to firebase error handling for invalid credentials and user exception and credential exception not working
                        // it is clubbed together as - An internal error has occurred. [ INVALID_LOGIN_CREDENTIALS ] according to logs.
                    } catch(FirebaseAuthInvalidUserException e){
                        editTextloginEmail.setError("The user is not yet registered on the platform. Please try again.");
                        editTextloginEmail.requestFocus();
                    }
                    catch(FirebaseAuthInvalidCredentialsException e){
                        editTextloginPwd.setError("The password entered is incorrect.");
                        editTextloginPwd.requestFocus();
                    } catch(Exception e){ //takes care of remaining exception
                        Log.e(TAG, e.getMessage());
                    }
                    Toast.makeText(LoginActivity.this, "Login was unsuccessful", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    private void showAlertDialog(){
        //structure of alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email. You cannot login wihout email verification");

        //opening email app if button is clicked
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                //the email app is opened in new window not inside our app
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}