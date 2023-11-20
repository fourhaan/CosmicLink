package com.example.volunteerapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.volunteerapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivityForVol extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private boolean isPermissionGranted = false;
    private GoogleMap mGoogleMap;
    private MapView mapView;
    private FloatingActionButton fab;
    private FusedLocationProviderClient mLocationClient;
    private final int GPS_REQUEST_CODE = 9001;
    private EditText locSearch;
    private ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        checkMyPermission();
        mapView = findViewById(R.id.mapView);
        fab = findViewById(R.id.fab_loc);
        locSearch = findViewById(R.id.search_item);
        searchIcon = findViewById(R.id.search_loc_img);

        mLocationClient = new FusedLocationProviderClient(this);

        fab.setOnClickListener(v -> {
            if (isPermissionGranted) {
                getCurrLoc();
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isPermissionGranted) {
            if (isGPSenabled) {
                mapView.getMapAsync(this);
                mapView.onCreate(savedInstanceState);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("GPS Permission")
                        .setMessage("GPS is required for this app to work, Please enable GPS")
                        .setPositiveButton("Yes", (dialog, i) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, GPS_REQUEST_CODE);
                        })
                        .setCancelable(false)
                        .show();
            }
        }

        searchIcon.setOnClickListener(this::geoLocate);

    }

    private void geoLocate(View view) {
        String locationName = locSearch.getText().toString();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                String fullAddress = address.getAddressLine(0);
                gotoLocation(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(fullAddress)
                        .draggable(true);

                mGoogleMap.addMarker(markerOptions);

                // Show a dialog to confirm the address
                new android.os.Handler().postDelayed(() -> showConfirmationDialog(fullAddress, latitude, longitude), 2000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCurrLoc() {
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Use Geocoder to get the address from latitude and longitude
                Geocoder geocoder = new Geocoder(LocationActivityForVol.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);

                        gotoLocation(latitude, longitude);
                        String fullAddress = address.getAddressLine(0);

                        // Add a draggable marker
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title(fullAddress)
                                .draggable(true);

                        Marker marker = mGoogleMap.addMarker(markerOptions);
                        marker.showInfoWindow();  // Show the info window

                        new android.os.Handler().postDelayed(() -> showConfirmationDialog(fullAddress, latitude, longitude), 2000);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showConfirmationDialog(String fullAddress, double latitude, double longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Address");
        builder.setMessage("Is this your correct address?\n\n" + fullAddress);
        builder.setPositiveButton("Yes", (dialog, which) -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                String uid = user.getUid();

                // Reference to the database path "Registered Users/Uid/"
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users").child(uid);

                // Save the address details
                userRef.child("address").setValue(fullAddress);
                userRef.child("latitude").setValue(latitude);
                userRef.child("Longitude").setValue(longitude);
            }
            // Proceed with the intent
            Intent intent = new Intent(LocationActivityForVol.this, VolunteerLandingPageActivity.class);
            startActivity(intent);

            Toast.makeText(this, "Address confirmed", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // Do nothing or add any additional handling
            Toast.makeText(this, "Address not confirmed", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void gotoLocation(double latitude, double longitude) {
        LatLng LatLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 18);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(LocationActivityForVol.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // Set up the marker drag listener
        mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                // Handle drag start event if needed
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // Handle drag event if needed
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Handle drag end event
                LatLng position = marker.getPosition();
                double latitude = position.latitude;
                double longitude = position.longitude;

                // Use Geocoder to get the address from latitude and longitude
                Geocoder geocoder = new Geocoder(LocationActivityForVol.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);

                        // Show a dialog to confirm the updated address
                        showConfirmationDialog(address.getAddressLine(0), latitude, longitude);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
