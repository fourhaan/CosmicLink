package com.example.volunteerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class organisationSignUpActivity extends AppCompatActivity {

    private EditText editTextfullName, editTextemail, editTextmobileNo, editTextregPwd, editTextconfirmPwd;
    private ProgressBar progressBar;
    private CheckBox confirmDetails;
    private static final String TAG = "organisationSignUpActivity";
    private Spinner stateSpinner,citySpinner,orgTypeSpinner;
    private ArrayAdapter stateAdapter,cityAdapter,orgTypeAdapter;
    private String selectedState,selectedCity,joiningDate,selectedOrgType;
    private TextView regionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisation_sign_up);

        //initialising all editTexts
        editTextfullName = findViewById(R.id.editText_register_org_name);
        editTextemail = findViewById(R.id.editText_register_email);
        editTextmobileNo = findViewById(R.id.editText_register_mobile);
        editTextregPwd = findViewById(R.id.editText_register_password);
        editTextconfirmPwd = findViewById(R.id.editText_confirm_password);

        //progress bar
        progressBar = findViewById(R.id.progressBar);

        //check box details are confirmed
        confirmDetails = findViewById(R.id.checkBox_confirm_details);

        //initialise region textview
        regionTextView = findViewById(R.id.textView_state_spinner);

        //to get joiningDate of user.
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        joiningDate = dateFormat.format(cal.getTime());

        //initialising state spinner
        stateSpinner = findViewById(R.id.state_spinner);
        //populating ArrayAdapter using array of strings and a spinner layout that we will define
        stateAdapter = ArrayAdapter.createFromResource(this, R.array.array_states,R.layout.spinner_layout);
        //specify the layout to use when dropdown menu is opened
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //connecting stateSpinner and stateAdapter to populate it
        stateSpinner.setAdapter(stateAdapter);
        //using nested spinners for cities i.e when stateSpinner is selected
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //initialise citySpinner
                citySpinner = findViewById(R.id.city_spinner);
                //getting the values from stateSpinner
                selectedState = stateSpinner.getSelectedItem().toString();
                //hiding keyboard just for ui appeal
                hideKeyboardFrom(editTextfullName);
                hideKeyboardFrom(editTextemail);
                hideKeyboardFrom(editTextmobileNo);
                hideKeyboardFrom(editTextregPwd);
                hideKeyboardFrom(editTextconfirmPwd);

                int parentID = parent.getId();
                if(parentID == R.id.state_spinner){
                    switch(selectedState){
                        //using switch case accordingly to select the major city associated with the state
                        case "Select Your State": cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),R.array.array_default_cities,R.layout.spinner_layout);
                            break;
                        case "Andaman and Nicobar Islands":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_andaman_and_nicobar_cities, R.layout.spinner_layout);
                            break;
                        case "Andhra Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_andhra_pradesh_cities, R.layout.spinner_layout);
                            break;
                        case "Arunachal Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_arunachal_pradesh_cities, R.layout.spinner_layout);
                            break;
                        case "Assam":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_assam_cities, R.layout.spinner_layout);
                            break;
                        case "Bihar":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_bihar_cities, R.layout.spinner_layout);
                            break;
                        case "Chandigarh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_chandigarh_cities, R.layout.spinner_layout);
                            break;
                        case "Chhattisgarh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_chhattisgarh_cities, R.layout.spinner_layout);
                            break;
                        case "Dadra and Nagar Haveli":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_dadra_and_nagar_haveli_cities, R.layout.spinner_layout);
                            break;
                        case "Daman and Diu":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_daman_and_diu_cities, R.layout.spinner_layout);
                            break;
                        case "Delhi":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_delhi_cities, R.layout.spinner_layout);
                            break;
                        case "Goa":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_goa_cities, R.layout.spinner_layout);
                            break;
                        case "Gujarat":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_gujarat_cities, R.layout.spinner_layout);
                            break;
                        case "Haryana":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_haryana_cities, R.layout.spinner_layout);
                            break;
                        case "Himachal Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_himachal_pradesh_cities, R.layout.spinner_layout);
                            break;
                        case "Jammu and Kashmir":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_jammu_and_kashmir_cities, R.layout.spinner_layout);
                            break;
                        case "Jharkhand":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_jharkhand_cities, R.layout.spinner_layout);
                            break;
                        case "Karnataka":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_karnataka_cities, R.layout.spinner_layout);
                            break;
                        case "Kerala":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_kerala_cities, R.layout.spinner_layout);
                            break;
                        case "Ladakh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_ladakh_cities, R.layout.spinner_layout);
                            break;
                        case "Lakshadweep":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_lakshadweep_cities, R.layout.spinner_layout);
                            break;
                        case "Madhya Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_madhya_pradesh_cities, R.layout.spinner_layout);
                            break;
                        case "Maharashtra":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_maharashtra_cities, R.layout.spinner_layout);
                            break;
                        case "Manipur":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_manipur_cities, R.layout.spinner_layout);
                            break;
                        case "Meghalaya":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_meghalaya_cities, R.layout.spinner_layout);
                            break;
                        case "Mizoram":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_mizoram_cities, R.layout.spinner_layout);
                            break;
                        case "Nagaland":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_nagaland_cities, R.layout.spinner_layout);
                            break;
                        case "Odisha":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_odisha_cities, R.layout.spinner_layout);
                            break;
                        case "Puducherry":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_puducherry_cities, R.layout.spinner_layout);
                            break;
                        case "Punjab":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_punjab_cities, R.layout.spinner_layout);
                            break;
                        case "Rajasthan":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_rajasthan_cities, R.layout.spinner_layout);
                            break;
                        case "Sikkim":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_sikkim_cities, R.layout.spinner_layout);
                            break;
                        case "Tamil Nadu":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_tamil_nadu_cities, R.layout.spinner_layout);
                            break;
                        case "Telangana":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_telangana_cities, R.layout.spinner_layout);
                            break;
                        case "Tripura":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_tripura_cities, R.layout.spinner_layout);
                            break;
                        case "Uttarakhand":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_uttarakhand_cities, R.layout.spinner_layout);
                            break;
                        case "Uttar Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_uttar_pradesh_cities, R.layout.spinner_layout);
                            break;
                        case "West Bengal":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_west_bengal_cities, R.layout.spinner_layout);
                            break;
                        default: break;
                    }
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(cityAdapter);
                    //if it selected then we take value
                    citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCity = citySpinner.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //initialising and implementing org type spinner
        orgTypeSpinner = findViewById(R.id.spinner_organisation_type);
        orgTypeAdapter = ArrayAdapter.createFromResource(this, R.array.array_org_types,R.layout.spinner_layout);
        orgTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orgTypeSpinner.setAdapter(orgTypeAdapter);
        orgTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOrgType = orgTypeSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //setting up the register button
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //all the details are taken and stored in variables using getText().toString()
                String TextfullName = editTextfullName.getText().toString();
                String Textemail = editTextemail.getText().toString();
                String TextmobileNo = editTextmobileNo.getText().toString();
                String Textpwd = editTextregPwd.getText().toString();
                String TextconfirmPwd = editTextconfirmPwd.getText().toString();

                //using java.util.regex for regular expressions
                //[6-9] means first digit is 6-9
                //rest 9 digits are 0-9
                String indianMobileNoRegex = "[6-9][0-9]{9}";

                //mobileNoMatcher matches the regular expression with mobile number.
                Matcher mobileNoMatcher;
                Pattern mobileNoPattern = Pattern.compile(indianMobileNoRegex);
                mobileNoMatcher = mobileNoPattern.matcher(TextmobileNo);


                //the following is error handling if fields are empty
                if(TextUtils.isEmpty(TextfullName)){
                    Toast.makeText(organisationSignUpActivity.this,"Please enter the full name",Toast.LENGTH_SHORT).show();
                    editTextfullName.setError("Full name is required");
                    editTextfullName.requestFocus();
                }
                else if(TextUtils.isEmpty(Textemail)){
                    Toast.makeText(organisationSignUpActivity.this,"Please enter the email",Toast.LENGTH_SHORT).show();
                    editTextemail.setError("email is required");
                    editTextemail.requestFocus();
                }
                else if(TextUtils.isEmpty(TextmobileNo)){
                    Toast.makeText(organisationSignUpActivity.this,"Please enter the mobile No.",Toast.LENGTH_SHORT).show();
                    editTextmobileNo.setError("mobile number is required");
                    editTextmobileNo.requestFocus();
                }
                else if(TextUtils.isEmpty(Textpwd)){
                    Toast.makeText(organisationSignUpActivity.this,"Please enter the password",Toast.LENGTH_SHORT).show();
                    editTextregPwd.setError("password is required");
                    editTextregPwd.requestFocus();
                }
                else if(TextUtils.isEmpty(TextconfirmPwd)){
                    Toast.makeText(organisationSignUpActivity.this,"Please re-enter the password",Toast.LENGTH_SHORT).show();
                    editTextconfirmPwd.setError("confirm the password");
                    editTextconfirmPwd.requestFocus();
                }
                // this checks for confirm details checkbox
                else if (!confirmDetails.isChecked()) {
                    Toast.makeText(organisationSignUpActivity.this, "Please confirm the details", Toast.LENGTH_SHORT).show();
                    confirmDetails.setError("You must click confirm details");
                    confirmDetails.requestFocus();
                }

                // checks for mobile no. length to be equal to 10
                else if(TextmobileNo.length() != 10){
                    Toast.makeText(organisationSignUpActivity.this,"Incorrect mobile no.",Toast.LENGTH_SHORT).show();
                    editTextmobileNo.setError("Mobile No. should be 10 digits");
                    editTextmobileNo.requestFocus();
                }

                else if(Textpwd.length() <6){
                    Toast.makeText(organisationSignUpActivity.this,"Enter password of length 6 or more digits.",Toast.LENGTH_SHORT).show();
                    editTextregPwd.setError("password should be least 6 digits");
                    editTextregPwd.requestFocus();
                }

                //checks if email address matches the common pattern of email addresses.
                else if(!Patterns.EMAIL_ADDRESS.matcher(Textemail).matches()){
                    Toast.makeText(organisationSignUpActivity.this,"Please re-enter your email",
                            Toast.LENGTH_SHORT).show();
                    editTextemail.setError("Valid email is required");
                    editTextemail.requestFocus();
                }

                //checks if the mobile number follows the patter as indian mobile numbers
                else if(!mobileNoMatcher.find()){
                    Toast.makeText(organisationSignUpActivity.this,"Please re-enter your mobile no.",
                            Toast.LENGTH_SHORT).show();
                    editTextmobileNo.setError("Mobile No. is not valid");
                    editTextmobileNo.requestFocus();
                }

                //checks if confirmation password is same as registered password
                else if(!Textpwd.equals((TextconfirmPwd))){
                    Toast.makeText(organisationSignUpActivity.this,"Please re-enter the password",Toast.LENGTH_SHORT).show();
                    editTextconfirmPwd.setError("Password is incorrect");
                    editTextconfirmPwd.requestFocus();
                    //clear the entered passwords if failed like normal signups
                    editTextregPwd.clearComposingText();
                    editTextconfirmPwd.clearComposingText();
                }
                else if(selectedCity.equals("Select Your City")){
                    Toast.makeText(organisationSignUpActivity.this,"Please select your city",Toast.LENGTH_SHORT).show();
                    regionTextView.setError("Select your region");
                    regionTextView.requestFocus();
                }

                //this means all the data is correct and errors handled.
                else{
                    progressBar.setVisibility(View.VISIBLE); //starts loading animation in centre
                    registerUser(TextfullName, Textemail, TextmobileNo, Textpwd);
                }

            }
        });

        //on refreshing the scrollview we clear all the fields
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                editTextfullName.setText("");
                editTextemail.setText("");
                editTextmobileNo.setText("");
                editTextregPwd.setText("");
                editTextconfirmPwd.setText("");
                stateSpinner.setSelection(0);
                citySpinner.setSelection(0);
                orgTypeSpinner.setSelection(0);

                // After all the fields cleared the fields, call setRefreshing(false) to stop the loading indicator.
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void hideKeyboardFrom(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    //registering a user to firebase database
    private void registerUser(String TextfullName,String Textemail,String TextmobileNo,String Textpwd){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String bio = "";
        String imgUrl = "https://firebasestorage.googleapis.com/v0/b/volunteerapp-f6acb.appspot.com/o/placeholder.png?alt=media&token=6cac78e0-6d59-44cd-a844-d4bb8ca1727d";
        auth.createUserWithEmailAndPassword(Textemail,Textpwd).addOnCompleteListener(organisationSignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            //first onComplete method is to create data and get uid
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser regUser = auth.getCurrentUser();
                    String userType = "organisation"; //set userType to organisation

                    //next 2 lines are used to get data and get uid
                    OrganisationDetails writeUserDetails = new OrganisationDetails(TextfullName,TextmobileNo,userType,selectedState,selectedCity,joiningDate,selectedOrgType,bio,imgUrl);
                    //it creates a data node called registered volunteers under which the user data is stored.
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
                    reference.child(regUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //to confirm the user is registered use a confirmation email
                            //send confirm email
                            if (task.isSuccessful()) {
                                regUser.sendEmailVerification();
                                Toast.makeText(organisationSignUpActivity.this, "Registration was successful. Please verify your email address.", Toast.LENGTH_LONG).show();

                                //Open profile page after successful registration
                                Intent intent = new Intent(organisationSignUpActivity.this, OrganisationLandingPageActivity.class);
                                //this make sures that if we registered then we cant go back to previous activities using back button
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //closes the registration activity
                            } else {
                                Toast.makeText(organisationSignUpActivity.this, "Registration was unsuccessful. PLease try again.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else {
                    //try is to used to make a code-block for testing errors
                    try {
                        //throw is to used to create custom error depending exception type
                        //throw here would get exception from any exception in task which is task not successful
                        throw task.getException();
                        //example of exception : FirebaseAuthWeakPasswordException checks for weak password
                        //catch literally catches the error for us and we can define toasts acc. to it
                    } catch(FirebaseAuthWeakPasswordException e){
                        editTextregPwd.setError("Weak Password. Please use a mix of alphabets and numbers");
                        editTextregPwd.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextemail.setError("Your email is invalid or already in use. Please re-enter the email");
                        editTextemail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e){
                        editTextemail.setError("Your email is already registered. Please use another email or login");
                        editTextemail.requestFocus();
                    } //this will handle all the renaming exception and help us debug later on while loggin the exception
                    catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(organisationSignUpActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }



}