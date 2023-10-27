package com.example.assignmentthree;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CameraDetailsActivity extends AppCompatActivity {

    // Initialize Variables
    private ImageView previewImageView;
    private TextView titleTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView cityTextView;
    private TextView regionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_details);

        // Initialize the views
        previewImageView = findViewById(R.id.iv_cameraPreview);
        titleTextView = findViewById(R.id.tv_title);
        latitudeTextView = findViewById(R.id.tv_latitude);
        longitudeTextView = findViewById(R.id.tv_longitude);
        cityTextView = findViewById(R.id.tv_City);
        regionTextView = findViewById(R.id.tv_Region);

        // Get the camera ID handed over
        int cameraID = getIntent().getIntExtra("camera_id", -1);

        // Checks camera ID isn't null before requesting data
        if(cameraID != -1){
            getCameraDetails(cameraID);
            getCameraImage(cameraID);
        }
    }

    private void getCameraDetails(int cameraID){
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the API URL for ID specific camera
        String url = "https://api.windy.com/webcams/api/v3/webcams?lang=en&limit=1&offset=0&include=location&webcamIds=" + cameraID;
        Log.d("WeatherDebug", "URL: " + url); // Log the URL

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray webcams = response.getJSONArray("webcams");

                            if (webcams.length() > 0) {
                                JSONObject camera = webcams.getJSONObject(0);

                                // Extract camera details
                                JSONObject location = camera.getJSONObject("location");
                                String title = camera.getString("title");
                                double camLat = location.getDouble("latitude");
                                double camLon = location.getDouble("longitude");
                                String city = location.getString("city");
                                String region = location.getString("region");

                                // Set text to the TextViews
                                titleTextView.setText(title);
                                String latLabel = getString(R.string.title_lat) + " " + String.valueOf((camLat));
                                latitudeTextView.setText(latLabel);
                                String lonLabel = getString(R.string.title_lon) + " " + String.valueOf(camLon);
                                longitudeTextView.setText(lonLabel);
                                String cityLabel = getString(R.string.title_city) + " " + String.valueOf(city);
                                cityTextView.setText(cityLabel);
                                String regionLabel = getString(R.string.title_region) + " " + String.valueOf(region);
                                regionTextView.setText(regionLabel);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle Error
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-windy-api-key", "Use-API-key-here");
                return headers;
            }
        };
        // Add the request to the RequestQueue
        queue.add(objectRequest);
    }


    private void getCameraImage(int cameraID) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String imageUrl = "https://api.windy.com/webcams/api/v3/webcams?lang=en&limit=1&offset=0&include=images&webcamIds=" + cameraID;

        JsonObjectRequest imageRequest = new JsonObjectRequest(Request.Method.GET, imageUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray webcams = response.getJSONArray("webcams");

                            if (webcams.length() > 0) {
                                JSONObject camera = webcams.getJSONObject(0);

                                // Extract camera image URLs
                                JSONObject images = camera.getJSONObject("images");
                                JSONObject currentImage = images.getJSONObject("current");
                                String previewUrl = currentImage.getString("preview");

                                // Use Picasso to load the image into the ImageView
                                Picasso.get().load(previewUrl).into(previewImageView);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle Error
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-windy-api-key", "Use-API-key-here");
                return headers;
            }
        };
        // Add the request to the RequestQueue
        queue.add(imageRequest);
    }
}
