package com.example.volunteerapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.volunteerapp.CustomTools.TagsInputEditText;
import com.example.volunteerapp.Models.modelPost;
import com.example.volunteerapp.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPostActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userDb;
    private EditText titleEt , descriptionEt , workhoursEt;
    private ImageView imageIv;
    private Button uploadButton;
    private Uri imagePath;
    private String name, email,uid, dp, editedTags,date;
    private ProgressDialog pd;
    private TextInputLayout tagsLayout;
    private TagsInputEditText tagsEditText;
    private TextView address;;
    private EditText editTextdate;
    private double latitude;
    private String fullAddress;
    private double longitude;
    private int workhours = 0;
    private static final int GPS_REQUEST_CODE = 9001;
    private static final int LOCATION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        email = currentUser.getEmail();
        uid = currentUser.getUid();

        //progress dialog
        pd = new ProgressDialog(this);

        userDb = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = userDb.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    name = "" + ds.child("fullname").getValue();
                    email = "" + ds.child("email").getValue();
                    dp = "" + ds.child("image_url").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //initialise the views
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        workhoursEt = findViewById(R.id.workhours);
        uploadButton = findViewById(R.id.upload_post);
        imageIv = findViewById(R.id.imageIv);
        address = findViewById(R.id.address_Et);
        editTextdate = findViewById(R.id.editText_date);

        address.setOnClickListener(v -> {
            if (checkLocationAndGPSPermission()) {
                Intent intent = new Intent(AddPostActivity.this, LocationActivityForPost.class);
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
            }
        });

        //initialising tags layout
        tagsLayout = findViewById(R.id.tagsLayout);
        tagsEditText = findViewById(R.id.tagsET);


        //onCLick for upload button
        uploadButton.setOnClickListener(v -> {
            String title = titleEt.getText().toString().trim();
            String description = descriptionEt.getText().toString().trim();
            workhours = Integer.parseInt(workhoursEt.getText().toString());
            editedTags = tagsEditText.getText().toString();
            String[] tagsArray = editedTags.split("\\s+"); // Split the string based on whitespace

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(AddPostActivity.this, "Enter the title", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(description)) {
                Toast.makeText(AddPostActivity.this, "Enter the description", Toast.LENGTH_SHORT).show();
                return;
            } else if (workhours == 0) {
                Toast.makeText(AddPostActivity.this, "Work Hour cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(fullAddress)) {
                Toast.makeText(AddPostActivity.this, "Add an Address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if there are exactly 3 tags
            else if (tagsArray.length != 3) {
                Toast.makeText(AddPostActivity.this, "Pls enter 3 tags only", Toast.LENGTH_SHORT).show();
                return;
            } else {
                uploadData(title, description, imagePath);
            }

        });

        //onClick for imageView
        imageIv.setOnClickListener(v -> {
            ImagePicker.Companion.with(AddPostActivity.this).cropSquare().start();
        });

        editTextdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //date-picker dialog it creates a new dialog like view where we can pick date
                DatePickerDialog date_picker = new DatePickerDialog(AddPostActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //for compatibility months are 0-11 so +1 for our use
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                        if (!isDateBeforeCurrent(selectedDate)) {
                            editTextdate.setText(selectedDate);
                        } else {
                            // Show an error message if the selected date is before the current date
                            Toast.makeText(AddPostActivity.this, "Please select a date not before the current date", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, year, month, day);
                date_picker.show();
            }
        });

    }
    private boolean isDateBeforeCurrent(String selectedDate) {
        // Convert the selected date and current date to milliseconds
        long selectedDateMillis = convertDateToMillis(selectedDate);
        long currentDateMillis = System.currentTimeMillis();

        // Check if the selected date is before the current date
        return selectedDateMillis < currentDateMillis;
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

    private void uploadData(String title, String description,Uri image_Path ) {
        String interested="0";
        pd.setMessage("Publishing post...");
        pd.show();
        date = editTextdate.getText().toString().trim();

        //for post-image , post-id , post-publish-time
        String timeStamp = String.valueOf(System.currentTimeMillis());

        if(image_Path!=null){
            // Compress the image before uploading
            Bitmap compressedBitmap = compressImage();

            // Convert the compressed bitmap to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageData = baos.toByteArray();

            //post with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("Posts/"+"post_"+timeStamp);
            ref.putBytes(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            //image is uploaded
                            Task<Uri> uriTask = task.getResult().getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            String downloadUrl = uriTask.getResult().toString();
                            if (uriTask.isSuccessful()){
                                //url is received then proceed with adding remaining fields
                                modelPost postDetails = new modelPost(timeStamp, title,workhours, description,interested, downloadUrl, timeStamp, uid, email, dp, name, editedTags,date,fullAddress,latitude,longitude);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child(timeStamp).setValue(postDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //added it in database
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this,"Post published",Toast.LENGTH_SHORT).show();
                                                //reset the views
                                                titleEt.setText("");
                                                descriptionEt.setText("");
                                                tagsEditText.setText("");
                                                editTextdate.setText("");
                                                address.setText("");
                                                imageIv.setImageResource(android.R.color.transparent);
                                                imagePath = null;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed adding it to database
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed to upload image
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            String downloadUrl = "no_image";
            modelPost postDetails = new modelPost(timeStamp, title,workhours, description,interested, downloadUrl, timeStamp, uid, email, dp, name, editedTags,date,fullAddress,latitude,longitude);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timeStamp).setValue(postDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //added it in database
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this,"Post published",Toast.LENGTH_SHORT).show();
                            //reset the views
                            titleEt.setText("");
                            workhoursEt.setText("");
                            descriptionEt.setText("");
                            tagsEditText.setText("");
                            address.setText("");
                            editTextdate.setText("");
                            imagePath = null;
                            imageIv.setImageDrawable(getDrawable(R.drawable.image_holder));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding it to database
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
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

        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the result data here
            if (data != null) {
                fullAddress = data.getStringExtra("fullAddress");
                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);

                if (fullAddress != null) {
                    address.setText("Address: " + fullAddress);
                }
            }
        }
    }


    private void getImageInImageView() {
        Bitmap bitmap = null;
        if (this != null && imagePath != null) {
            try {
                // Decode the image with reduced dimensions and quality
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2; // Adjust the inSampleSize as needed
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                InputStream inputStream = getContentResolver().openInputStream(imagePath);
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                imageIv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap compressImage() {
        // Decode the image with options to scale down
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap compressedBitmap = null;

        if (this != null && imagePath != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imagePath);
                compressedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return compressedBitmap;
    }

    private boolean checkLocationAndGPSPermission() {
        boolean isLocationPermissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!isLocationPermissionGranted) {
            // Location permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return false;
        }

        // Check if GPS is enabled
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            // GPS is not enabled, show settings to enable it
            showGPSEnabledDialog();
            return false;
        }

        return true;
    }

    private void showGPSEnabledDialog() {
        // Your existing code to show GPS enable dialog
        // ...

        // For example:
        new AlertDialog.Builder(this)
                .setTitle("GPS Permission")
                .setMessage("GPS is required for this app to work. Please enable GPS.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, GPS_REQUEST_CODE);
                })
                .setCancelable(false)
                .show();
    }
}