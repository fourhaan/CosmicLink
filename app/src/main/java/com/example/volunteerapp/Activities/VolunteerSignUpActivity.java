package com.example.volunteerapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.volunteerapp.R;
import com.example.volunteerapp.Models.volunteerDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VolunteerSignUpActivity extends AppCompatActivity {

    private EditText editTextfullName, editTextemail, editTextdob, editTextmobileNo, editTextregPwd, editTextconfirmPwd, editTextuserName;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private CheckBox confirmDetails;
    private static final String TAG = "volunteerSignUpActivity";
    private DatePickerDialog date_picker;
    private Spinner stateSpinner,citySpinner;
    private ArrayAdapter stateAdapter,cityAdapter;
    private String selectedState,selectedCity,joiningDate;
    private TextView regionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_sign_up);

        //initialising all editTexts
        editTextfullName = findViewById(R.id.editText_register_full_name);
        editTextemail = findViewById(R.id.editText_register_email);
        editTextdob = findViewById(R.id.editText_register_dob);
        editTextmobileNo = findViewById(R.id.editText_register_mobile);
        editTextregPwd = findViewById(R.id.editText_register_password);
        editTextconfirmPwd = findViewById(R.id.editText_confirm_password);
        editTextuserName = findViewById(R.id.editText_register_user_name);

        //Gender RadioButton
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck(); //this will remove any checks as the activity starts

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


        //setting up the date-picker clickable on edittext
        editTextdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date-picker dialog it creates a new dialog like view where we can pick date
                DatePickerDialog date_picker = new DatePickerDialog(VolunteerSignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //for compatibility months are 0-11 so +1 for our use
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                        if (!isDateBeforeCurrent(selectedDate)) {
                            editTextdob.setText(selectedDate);
                        } else {
                            // Show an error message if the selected date is before the current date
                            Toast.makeText(VolunteerSignUpActivity.this, "Please select a date not after the current date", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, year, month, day);
                date_picker.show();
            }
        });

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
                hideKeyboardFrom(editTextdob);
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

        //setting up the register button
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                //all the details are taken and stored in variables using getText().toString()
                String TextfullName = editTextfullName.getText().toString();
                String Textemail = editTextemail.getText().toString();
                String Textdob = editTextdob.getText().toString();
                String TextmobileNo = editTextmobileNo.getText().toString();
                String Textpwd = editTextregPwd.getText().toString();
                String TextconfirmPwd = editTextconfirmPwd.getText().toString();
                String TextuserName = editTextuserName.getText().toString();
                String Textgender; //cant be initialised directly w/o checking null exception

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
                    Toast.makeText(VolunteerSignUpActivity.this,"Please enter the full name",Toast.LENGTH_SHORT).show();
                    editTextfullName.setError("Full name is required");
                    editTextfullName.requestFocus();
                }
                else if(TextUtils.isEmpty(TextuserName)) {
                    editTextuserName.setError("Username is required");
                    editTextuserName.requestFocus();
                }
                else if (TextuserName.length() < 4) {
                    editTextuserName.setError("Username should be at least 4 characters");
                    editTextuserName.requestFocus();
                }
                else if(TextUtils.isEmpty(Textdob)){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please enter the dob",Toast.LENGTH_SHORT).show();
                    editTextdob.setError("dob is required");
                    editTextdob.requestFocus();
                }
                else if(TextUtils.isEmpty(Textemail)){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please enter the email",Toast.LENGTH_SHORT).show();
                    editTextemail.setError("email is required");
                    editTextemail.requestFocus();
                }
                else if(TextUtils.isEmpty(TextmobileNo)){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please enter the mobile No.",Toast.LENGTH_SHORT).show();
                    editTextmobileNo.setError("mobile number is required");
                    editTextmobileNo.requestFocus();
                }
                else if(TextUtils.isEmpty(Textpwd)){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please enter the password",Toast.LENGTH_SHORT).show();
                    editTextregPwd.setError("password is required");
                    editTextregPwd.requestFocus();
                }
                else if(TextUtils.isEmpty(TextconfirmPwd)){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please re-enter the password",Toast.LENGTH_SHORT).show();
                    editTextconfirmPwd.setError("confirm the password");
                    editTextconfirmPwd.requestFocus();
                }
                // this checks radio group if empty and generates error
                else if(radioGroupRegisterGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please select your gender",Toast.LENGTH_SHORT).show();
                    radioButtonRegisterGenderSelected.setError("Full name is required");
                    radioGroupRegisterGender.requestFocus();
                }

                // this checks for confirm details checkbox
                else if (!confirmDetails.isChecked()) {
                    Toast.makeText(VolunteerSignUpActivity.this, "Please confirm the details", Toast.LENGTH_SHORT).show();
                    confirmDetails.setError("You must click confirm details");
                    confirmDetails.requestFocus();
                }

                // checks for mobile no. length to be equal to 10
                else if(TextmobileNo.length() != 10){
                    Toast.makeText(VolunteerSignUpActivity.this,"Incorrect mobile no.",Toast.LENGTH_SHORT).show();
                    editTextmobileNo.setError("Mobile No. should be 10 digits");
                    editTextmobileNo.requestFocus();
                }

                else if(Textpwd.length() <6){
                    Toast.makeText(VolunteerSignUpActivity.this,"Enter password of length 6 or more digits.",Toast.LENGTH_SHORT).show();
                    editTextregPwd.setError("password should be least 6 digits");
                    editTextregPwd.requestFocus();
                }

                //checks if email address matches the common pattern of email addresses.
                else if(!Patterns.EMAIL_ADDRESS.matcher(Textemail).matches()){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please re-enter your email",
                            Toast.LENGTH_SHORT).show();
                    editTextemail.setError("Valid email is required");
                    editTextemail.requestFocus();
                }

                //checks if the mobile number follows the patter as indian mobile numbers
                else if(!mobileNoMatcher.find()){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please re-enter your mobile no.",
                            Toast.LENGTH_SHORT).show();
                    editTextmobileNo.setError("Mobile No. is not valid");
                    editTextmobileNo.requestFocus();
                }

                //checks if confirmation password is same as registered password
                else if(!Textpwd.equals((TextconfirmPwd))){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please re-enter the password",Toast.LENGTH_SHORT).show();
                    editTextconfirmPwd.setError("Password is incorrect");
                    editTextconfirmPwd.requestFocus();
                    //clear the entered passwords if failed like normal signups
                    editTextregPwd.clearComposingText();
                    editTextconfirmPwd.clearComposingText();
                }
                else if(selectedCity.equals("Select Your City")){
                    Toast.makeText(VolunteerSignUpActivity.this,"Please select your city",Toast.LENGTH_SHORT).show();
                    regionTextView.setError("Select your region");
                    regionTextView.requestFocus();
                }

                //this means all the data is correct and errors handled.
                else{
                    Textgender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE); //starts loading animation in centre
                    registerUser(TextfullName, Textemail, Textdob, Textgender, TextmobileNo, Textpwd, TextuserName);
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
                editTextuserName.setText("");
                editTextdob.setText("");
                stateSpinner.setSelection(0);
                citySpinner.setSelection(0);
                radioGroupRegisterGender.clearCheck();

                // After all the fields are cleared, call setRefreshing(false) to stop the loading indicator.
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    private boolean isDateBeforeCurrent(String selectedDate) {
        // Convert the selected date and current date to milliseconds
        long selectedDateMillis = convertDateToMillis(selectedDate);
        long currentDateMillis = System.currentTimeMillis();

        // Check if the selected date is before the current date
        return selectedDateMillis > currentDateMillis;
    }

    private long convertDateToMillis(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date parsedDate = sdf.parse(date);
            return parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void hideKeyboardFrom(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void registerUser(String TextfullName, String Textemail, String Textdob, String Textgender, String TextmobileNo, String Textpwd, String TextuserName) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference userNamereference = FirebaseDatabase.getInstance().getReference("Usernames");
        String bio = "";
        String imgUrl = "https://firebasestorage.googleapis.com/v0/b/volunteerapp-f6acb.appspot.com/o/placeholder.png?alt=media&token=6cac78e0-6d59-44cd-a844-d4bb8ca1727d";

        auth.createUserWithEmailAndPassword(Textemail, Textpwd).addOnCompleteListener(VolunteerSignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser regUser = auth.getCurrentUser();
                    String userType = "volunteer";

                    // Check if the username is unique
                    userNamereference.child(TextuserName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Username exists
                                editTextuserName.setError("Username already taken");
                                editTextuserName.requestFocus();
                                // deleting the user if it fails
                                regUser.delete();
                            } else {
                                String userId = regUser.getUid();
                                // Unique username
                                volunteerDetails writeUserDetails = new volunteerDetails(TextfullName,Textemail, TextuserName, Textgender, Textdob, TextmobileNo, userType, selectedState, selectedCity, joiningDate, bio, imgUrl,userId);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
                                reference.child(regUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Registration successful
                                            // Write the username to the "Usernames" reference
//                                            userNamereference.child(TextuserName).setValue(regUser.getUid());
                                            userNamereference.child(TextuserName).setValue(regUser.getEmail());


                                            regUser.sendEmailVerification();
                                            Toast.makeText(VolunteerSignUpActivity.this, "Registration was successful. Please verify your email address.", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(VolunteerSignUpActivity.this, VolunteerLandingPageActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Registration unsuccessful
                                            Toast.makeText(VolunteerSignUpActivity.this, "Registration was unsuccessful. Please try again.", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database errors if necessary
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    // Handle specific exceptions as you did in your code
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        editTextregPwd.setError("Weak Password. Please use a mix of alphabets and numbers");
                        editTextregPwd.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextemail.setError("Your email is invalid or already in use. Please re-enter the email");
                        editTextemail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        editTextemail.setError("Your email is already registered. Please use another email or login");
                        editTextemail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(VolunteerSignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


}