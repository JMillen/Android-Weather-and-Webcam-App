package com.example.assignmentthree;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.assignmentthree.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    FusedLocationProviderClient fusedLocationProviderClient;

    double lat;
    double lon;
    LatLng placeLatLng;
    String placeName;
    LatLng placeWeatherLatLng;
    private RequestQueue queue;
    // Arraylist from camera titles
    ArrayList<String> cameraTitle = new ArrayList<>();
    // Define ArrayList for camera ID's
    ArrayList<Integer> cameraIDs = new ArrayList<Integer>();
    // Define adapter
    CameraAdapter adapter;


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
        Places.initialize(getApplicationContext(), "Use-API-key-here");

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
                // Clear the existing camera data
                cameraTitle.clear();
                cameraIDs.clear();
                adapter.notifyDataSetChanged();

                mMap.clear();
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
                placeLatLng = place.getLatLng();
                placeName = place.getName();
                placeWeatherLatLng = new LatLng(placeLatLng.latitude, placeLatLng.longitude + 0.05);
                int icon = getResources().getIdentifier("img_mm_marker_orange", "drawable", getPackageName());
                getWeatherPin(); //for weather
                getCameraPin();  //for camera
                Marker placeLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(placeLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(icon))
                        .title(placeName));
                placeLocationMarker.setTag(3);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 11));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Status status) {
                Log.i(STATUS_BAR_SERVICE, "An error occurred: " + status);
            }
        });

        ListView listViewCameras = findViewById(R.id.lv_cameras);
        adapter = new CameraAdapter(this, cameraTitle);

        // Set the adapter to the listView
        listViewCameras.setAdapter(adapter);

        listViewCameras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Get the camera ID from the JsonArray based on the position
                try{
                    int cameraID = cameraIDs.get(position);

                    // Start the CameraPreviewActivity with the camera ID
                    Intent intent = new Intent(MapsActivity.this, CameraDetailsActivity.class);
                    intent.putExtra("camera_id", cameraID);
                    startActivity(intent);
                } catch (Exception e){
                    Log.d("error", e.toString());
                }

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastKnownLocation();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Get the tag from the marker
                Integer cameraID = (Integer) marker.getTag();

                // Checks if weather marker, user marker or place marker was clicked
                if(cameraID != null && (cameraID.equals(1) || cameraID.equals(2) || cameraID.equals(3))){
                    marker.showInfoWindow();
                } else {
                    // Check that the camera ID isn't null
                    if (cameraID != null) {
                        // Start the CameraDetailsActivity with the camera ID
                        Intent intent = new Intent(MapsActivity.this, CameraDetailsActivity.class);
                        intent.putExtra("camera_id", cameraID);
                        startActivity(intent);
                    }
                }
                return true;
            }
        });
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
                    int icon = getResources().getIdentifier("img_mm_marker_orange", "drawable", getPackageName());
                    Marker userLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .title("User Location"));
                    userLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(icon));
                    userLocationMarker.setTag(2);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                } else {
                    Toast.makeText(MapsActivity.this, "Error getting location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getWeatherPin(){
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=Use-API-key-here";
        // Request a object response from the provided URL.
        Log.d("WeatherDebug", "URL: " + url); // Log the URL
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("WeatherDebug", "API response: " + response.toString()); // Log the JSON response

                    //Get a JSONArray from response object
                    JSONArray jsonArray = response.getJSONArray("weather");
                    //Get the object that is at index 0 in our JSONArray
                    JSONObject weatherArray = jsonArray.getJSONObject(0);
                    //Get the main description to be used
                    String mainDescription = weatherArray.getString("main").toLowerCase(Locale.ROOT);

                    String iconName = "img_mm_weather_" + mainDescription;
                    Log.d("WeatherDebug", "Icon name: " + iconName); // Log the icon name


                    int icon = getResources().getIdentifier(iconName, "drawable", getPackageName());
                    Log.d("WeatherDebug", "Icon resource ID: " + icon); // Log the icon resource ID


                    //Add a marker to the map with data
                    Marker weatherMarker = mMap.addMarker(new MarkerOptions()
                            .position(placeWeatherLatLng)
                            .title(weatherArray.getString("main"))
                            .snippet(weatherArray.getString("description"))
                            .icon(BitmapDescriptorFactory.fromResource(icon)));
                    weatherMarker.setTag(1);
                } catch (JSONException e) {
                    Log.d("error", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volleyError","Error :" + error.getMessage());
            }
        });
        queue.add(objectRequest);
    }

    private void getCameraPin(){
        String url = "https://api.windy.com/webcams/api/v3/webcams?lang=en&limit=5&offset=0&nearby=" + lat + "%2C" + lon +"%2C10&include=location";

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray webcams = response.getJSONArray("webcams");

                    int icon = getResources().getIdentifier("img_mm_camera_teal", "drawable", getPackageName());

                    for(int i = 0; i < webcams.length(); i++){
                        JSONObject webcam = webcams.getJSONObject(i);
                        JSONObject location = webcam.getJSONObject("location");
                        String title = webcam.getString("title");
                        double webcamLat = location.getDouble("latitude");
                        double webcamLon = location.getDouble("longitude");
                        int cameraID = webcam.getInt("webcamId");

                        // Add camera title to arrayList
                        cameraTitle.add(title);
                        // Add camera ID to arrayList
                        cameraIDs.add(cameraID);

                        // Add a marker for the camera
                        LatLng cameraLatLng = new LatLng(webcamLat, webcamLon);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(cameraLatLng)
                                .title(title));
                        marker.setTag(cameraID);
                        marker.setIcon(BitmapDescriptorFactory.fromResource(icon));
                    }

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                } catch(JSONException e) {
                    Log.d("error", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volleyError", "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-windy-api-key", "Use-API-key-here");
                return headers;
            }
        };
        queue.add(objectRequest);
    }

}
