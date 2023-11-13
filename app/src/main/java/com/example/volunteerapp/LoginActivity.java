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

    private EditText editTextloginInput, editTextloginPwd;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private TextView signUpRedirect;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextloginInput = findViewById(R.id.editText_login_input);
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
                String textLoginInput = editTextloginInput.getText().toString().trim();
                String textPwd = editTextloginPwd.getText().toString();

                //Basic checks for fields that are filled in editTexts
                if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginActivity.this,"Please enter the password",Toast.LENGTH_SHORT).show();
                    editTextloginPwd.setError("password is required");
                    editTextloginPwd.requestFocus();
                }
                else if(TextUtils.isEmpty(textLoginInput)) {
                    Toast.makeText(LoginActivity.this, "Please enter the Email or username", Toast.LENGTH_SHORT).show();
                    editTextloginInput.setError("Email/username is required");
                    editTextloginInput.requestFocus();
                }
//                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
//                    Toast.makeText(LoginActivity.this,"Please re-enter your email",
//                            Toast.LENGTH_SHORT).show();
//                    editTextloginEmail.setError("Valid email is required");
//                    editTextloginEmail.requestFocus();
//                }
                //this means all the data entered is correct and errors handled.
                else {
                    progressBar.setVisibility(View.VISIBLE);  //starts loading animation in centre
                    loginUser(textLoginInput, textPwd);
                }

            }
        });

    }
    private void loginUser(String loginInput, String pwd) {
        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        DatabaseReference userNamesReference = FirebaseDatabase.getInstance().getReference("Usernames");

        // Check if the login input is an email address
        if (isEmail(loginInput)) {
            // Log in using email
            authProfile.signInWithEmailAndPassword(loginInput, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Email login successful, proceed with user data retrieval and validation
                        handleSuccessfulLogin(authProfile.getCurrentUser());
                    } else {
                        // Email login failed, try username login
                        tryUsernameLogin(loginInput, pwd);
                    }
                }
            });
        } else {
            // Not an email, try username login
            tryUsernameLogin(loginInput, pwd);
        }
    }

    private boolean isEmail(String input) {
        // Simple email validation by checking for the presence of '@' character
        return input.contains("@");
    }

    private void tryUsernameLogin(String username, String password) {
        // Look up the UID for the given username from your Usernames database
        DatabaseReference usernamesReference = FirebaseDatabase.getInstance().getReference("Usernames");
        usernamesReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String uid = dataSnapshot.getValue(String.class);

                    FirebaseAuth authProfile = FirebaseAuth.getInstance();
                    // Sign in using the retrieved UID
                    authProfile.signInWithEmailAndPassword(uid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Username login successful, proceed with user data retrieval and validation
                                handleSuccessfulLogin(authProfile.getCurrentUser());
                            } else {
                                // Handle username login failure
                                handleLoginFailure(task.getException());
                            }
                        }
                    });
                } else {
                    // Handle case where the provided username doesn't exist
                    handleLoginFailure(new Exception("Username not found."));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                handleLoginFailure(new Exception("Database error: " + databaseError.getMessage()));
            }
        });
    }

    private void handleSuccessfulLogin(FirebaseUser currentUser) {
        if (currentUser != null) {
            // Check if the user's email is verified
            if (currentUser.isEmailVerified()) {
                String uid = currentUser.getUid();

                // Retrieve the user's data from Firebase Realtime Database
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(uid);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String userType = snapshot.child("usertype").getValue(String.class);
                            // Check the user type and proceed accordingly
                            handleUserType(userType);
                        } else {
                            // Handle missing user data
                            handleLoginFailure(new Exception("User data not found."));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                        handleLoginFailure(new Exception("Database error: " + error.getMessage()));
                    }
                });
            } else {
                // Handle email not verified
                handleEmailNotVerified(currentUser);
            }
        } else {
            // Handle null user
            handleLoginFailure(new Exception("User is null."));
        }
    }

    private void handleUserType(String userType) {
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
            handleLoginFailure(new Exception("Unknown user type."));
        }
    }

    private void handleEmailNotVerified(FirebaseUser currentUser) {
        currentUser.sendEmailVerification();
        FirebaseAuth.getInstance().signOut();
        progressBar.setVisibility(View.GONE);
        showAlertDialog(); // Show an alert dialog to inform the user about email verification
    }

    private void handleLoginFailure(Exception exception) {
        // Handle login failure and display appropriate error message
        // This can be customized based on the specific use case
        Toast.makeText(LoginActivity.this, "Login was unsuccessful: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }
    private void showAlertDialog(){
        //structure of alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email. You cannot login without email verification");

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