package com.example.assignmentthree;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.assignmentthree.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    FusedLocationProviderClient fusedLocationProviderClient;

    double lat;
    double lon;
    LatLng placeLatLng;
    private RequestQueue queue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissionGranted();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialise the places with API key
        Places.initialize(getApplicationContext(), "AIzaSyA5pUxD_2Xi1s-bga4itPVaq-VblEHmxg8");

        //Create a new placesClient
        PlacesClient placesClient = Places.createClient(this);

        //Get our autocompletefragment search view
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        //Set the country of our autocomplete to only display places in New Zealand
        autocompleteFragment.setCountries("NZ");

        //Make sure our autocomplete returns only names and lat/lng
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        //Instantiate the RequestQueue
        queue = Volley.newRequestQueue(this);


        //Listener to see if a location has been clicked.
        //Once location has been clicked, calls methods to add pins and move the camera to the selected location
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
                placeLatLng = place.getLatLng();
                //getWeatherPin(); //for weather .. to implement
                //getCameraPin();  //for camera  .. to implement
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 11));
            }

            @Override
            public void onError(Status status) {
                Log.i(STATUS_BAR_SERVICE, "An error occurred: " + status);
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastKnownLocation();
    }


    /**
     * Method that checks if location permissions have been granted.
     */
    public void checkPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Permission not granted, so asking for permission
            askForPermission();
        } else {
            //Permission was granted
            permissionGranted();
        }
    }

    /**
     * Method that launches a locationPermissionRequest
     */
    public void askForPermission() {
        ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            boolean coarseGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

            if (fineGranted) {
                // fine permission granted
                permissionGranted();
            } else if (coarseGranted) {
                // coarse permission granted
                checkPermissionGranted();
            } else {
                // No permissions have been granted
                permissionNotGranted();
            }
        });

        // Passing request for permissions
        activityResultLauncher.launch(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });
    }


    /**
     * Method that uses toast to display if permission was granted
     */
    public void permissionGranted() {
        Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
        getLastKnownLocation();
    }

    /**
     * Method that uses toast to display if permission was not granted
     */
    public void permissionNotGranted() {
        Toast.makeText(this, "NOT GRANTED", Toast.LENGTH_SHORT).show();
    }


    /**
     * Method that gets the users last location and adds a marker to the map
     */
    public void getLastKnownLocation() {
        // Get fusedLocation provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Forced permission check because android studio doesn't realise we've already checked
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("User Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                } else {
                    Toast.makeText(MapsActivity.this, "Error getting location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method that takes last location and returns Latitude and Longitude
     * of users location
     * @param location
     */
    public LatLng getLatLngFromLocation(Location location) {
        if (location != null) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            return new LatLng(lat, lon);
        } else {
            Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
            return null;
        }
    }










}