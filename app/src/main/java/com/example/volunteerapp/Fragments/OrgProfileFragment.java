package com.example.volunteerapp.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.volunteerapp.Activities.MainActivity;
import com.example.volunteerapp.R;
import com.example.volunteerapp.CustomTools.TagsInputEditText;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class OrgProfileFragment extends Fragment {
    private Button logoutButton;
    private TextView fullnameTextView, joiningdateTextView, mobileTextView, locationTextView, emailTextView, editSkills, editBio, editContact, editOrgType;
    private EditText bioEditText, mobile2EditText, email2EditText, orgTypeEditText;
    private ImageView profileImageView;
    private ProgressBar profileProgressBar;
    private TextInputLayout tagsLayout;
    private TagsInputEditText tagsEditText;
    private Uri imagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_profile, container, false);

        // Initialize all the XML elements
        logoutButton = view.findViewById(R.id.org_profile_log_out);
        fullnameTextView = view.findViewById(R.id.org_profile_name);
        emailTextView = view.findViewById(R.id.org_profile_email);
        joiningdateTextView = view.findViewById(R.id.org_profile_joining_date);
        bioEditText = view.findViewById(R.id.org_profile_bio);
        mobileTextView = view.findViewById(R.id.org_profile_mobile);
        profileImageView = view.findViewById(R.id.org_profile_image);
        locationTextView = view.findViewById(R.id.org_profile_location);
        orgTypeEditText = view.findViewById(R.id.org_profile_type);
        profileProgressBar = view.findViewById(R.id.org_profile_ProgressBar);
        editBio = view.findViewById(R.id.edit_org_bio);
        editContact = view.findViewById(R.id.edit_org_contact);
        mobile2EditText = view.findViewById(R.id.org_profile_mobile2);
        email2EditText = view.findViewById(R.id.ord_profile_email2);

        // Hide the progressbar and content initially
        profileProgressBar.setVisibility(View.GONE);
        view.findViewById(R.id.vol_profile_content).setVisibility(View.GONE);

        //initialising tags layout
        tagsLayout = view.findViewById(R.id.tagsLayout);
        tagsEditText = view.findViewById(R.id.tagsET);
        editSkills = view.findViewById(R.id.edit_org_skills);

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
                        bioEditText.setText(bio);
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
                        view.findViewById(R.id.vol_profile_content).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //error
                }
            });
        }
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use ImagePicker library to pick an image
                ImagePicker.Companion.with(OrgProfileFragment.this).cropSquare().start();
            }
        });


        editSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable or disable the TextInputLayout and its child based on the button press
                boolean isEnabled = !tagsLayout.isEnabled();
                tagsLayout.setEnabled(isEnabled);
                tagsEditText.setEnabled(isEnabled);

                // Update the data in Firebase when the "Edit" TextView is pressed
                if (!isEnabled) {
                    // Save the edited tags to Firebase
                    String editedTags = tagsEditText.getText().toString();
                    tagsLayout.setHelperText(null);
                    tagsLayout.setHint(null);
                    updateTagsInFirebase(editedTags);
                }
                else {
                    tagsLayout.setHelperText("Enter each tag space-separated");
                    tagsLayout.setHint("Edit your expertise");
                }
            }
        });

        editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_edit_contact, null);
                builder.setView(dialogView);

                // Initialize views in the dialog
                EditText mobile2EditTextDialog = dialogView.findViewById(R.id.mobile2EditTextDialog);
                EditText email2EditTextDialog = dialogView.findViewById(R.id.email2EditTextDialog);
                Button saveButton = dialogView.findViewById(R.id.saveButton);

                // Set initial values
                mobile2EditTextDialog.setText(mobile2EditText.getText().toString());
                email2EditTextDialog.setText(email2EditText.getText().toString());

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                // Handle the save button click
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get the edited values from the dialog
                        String editedMobile2 = mobile2EditTextDialog.getText().toString().trim();
                        String editedEmail2 = email2EditTextDialog.getText().toString().trim();

                        // Validate the edited values
                        if (TextUtils.isEmpty(editedMobile2) || editedMobile2.length() != 10) {
                            Toast.makeText(requireContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(editedEmail2) || !Patterns.EMAIL_ADDRESS.matcher(editedEmail2).matches()) {
                            Toast.makeText(requireContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
                        } else {
                            // Update the UI and Firebase with the edited values
                            mobile2EditText.setText(editedMobile2);
                            email2EditText.setText(editedEmail2);
                            updateContactinFirebase(editedMobile2, editedEmail2);

                            // Dismiss the dialog
                            dialog.dismiss();
                        }
                    }
                });
            }
        });


        editBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable or disable the bio field based on the button press
                boolean isEnabled = !bioEditText.isEnabled();
                bioEditText.setEnabled(isEnabled);

                // Update the data in Firebase when the "Save" button is pressed
                if (!isEnabled) {
                    // Save the edited bio to Firebase
                    String editedBio = bioEditText.getText().toString();
                    bioEditText.setTextColor(getResources().getColor(R.color.black2_0));
                    bioEditText.setHint(null);
                    updateBioInFirebase(editedBio);
                }
                else {
                    bioEditText.setHint("edit your bio");
                    bioEditText.setTextColor(getResources().getColor(R.color.lavender));
                }
            }
        });
        return view;
    }

    private void updateContactinFirebase(String mobile2,String email2){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String Uid = currentUser.getUid();
            // Reference to the user's data in Firebase Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(Uid);
            // Update the contacts field in Firebase
            userRef.child("mobile_2").setValue(mobile2);
            userRef.child("email_2").setValue(email2);
        }
    }

    private void updateTagsInFirebase(String editedTags) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String Uid = currentUser.getUid();
            // Reference to the user's data in Firebase Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(Uid);
            // Update the "tags" field in Firebase
            userRef.child("tags").setValue(editedTags);
        }
    }

    private void updateBioInFirebase(String editedBio) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String Uid = currentUser.getUid();
            // Reference to the user's data in Firebase Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(Uid);
            // Update the "bio" field in Firebase
            userRef.child("bio").setValue(editedBio);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            // Get the selected image URI from ImagePicker
            imagePath = data.getData();
            // Handle the selected image (e.g., display in ImageView and upload)
            getImageInImageView();
        }
    }

    private void getImageInImageView() {
        Bitmap bitmap = null;
        if (getContext() != null && imagePath != null) {
            try {
                // Decode the image with reduced dimensions and quality
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2; // Adjust the inSampleSize as needed
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                InputStream inputStream = getContext().getContentResolver().openInputStream(imagePath);
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Upload the compressed image
            uploadImage();
        }
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        // Compress the image before uploading
        Bitmap compressedBitmap = compressImage();

        // Convert the compressed bitmap to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageData = baos.toByteArray();

        // Upload the compressed image data
        FirebaseStorage.getInstance().getReference("profile_img/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .putBytes(imageData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(uriTask -> {
                            if (uriTask.isSuccessful()) {
                                updateProfilePicFirebase(uriTask.getResult().toString());
                            }
                        });
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                });
    }

    private Bitmap compressImage() {
        // Decode the image with options to scale down
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap compressedBitmap = null;

        if (getContext() != null && imagePath != null) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(imagePath);
                compressedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return compressedBitmap;
    }


    private void updateProfilePicFirebase(String url) {
        FirebaseDatabase.getInstance().getReference("Registered Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/image_url").setValue(url);
    }


}
