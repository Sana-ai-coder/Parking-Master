package com.projects.parkingapplication;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// This is MapsActivity (Backend) - second screen activity of the App
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Location permission request code is 123 in Android
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    // Google Maps Nearby search API URL for parking places
    // Replace LAT and LNG chars with actual latitude and longitude values
    // To change area of search change value of radius in the URL
    private final String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=LAT,LNG&radius=2500&types=parking&sensor=false&key=";
    private RequestQueue mRequestQueue;
    private MapView mapView;
    private GoogleMap googleMap;
    private GeoApiContext geoApiContext;
    private FusedLocationProviderClient fusedLocationProviderClient; // this holds the current phone GPS location
    private final List<Marker> parkingMarkers = new ArrayList<>();
    private LatLng customLocation = null;

    // this starts the second Activity - MapsActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set map view from layout
        setContentView(R.layout.activity_maps);

        // get intent data - we need custom location, if set in MainActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("customLatLng")) {
            // Custom location is enabled in MainActivity, so use it
            customLocation = intent.getParcelableExtra("customLatLng");
            System.out.println("Location from Main -- " + customLocation); // Logging custom location input form user
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Get the user's last known location from FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // create new REST call RequestQueue
        mRequestQueue = Volley.newRequestQueue(this);
        if(geoApiContext == null) {
            // this holds Google API results
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    // this method is called when Google Map is ready
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        // check if Location permission are granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setupMap();
        } else {
            // make lcoation request to user
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // this function sets up Google Map on second screen
    private void setupMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions here if not granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Enable My Location button and re-center functionality
        googleMap.setMyLocationEnabled(true);

        // get UI elements from layouts
        ImageButton findParkingButton = findViewById(R.id.findParkingButton);
        ImageButton zoomInButton = findViewById(R.id.zoomInButton);
        ImageButton zoomOutButton = findViewById(R.id.zoomOutButton);

        // zoom in functionality
        zoomInButton.setOnClickListener((l) -> {
            // Zoom in when zoom in button is clicked
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        });

        // zoom out button functionality
        zoomOutButton.setOnClickListener((l) -> {
            // Zoom out when zoom out button is clicked
            googleMap.animateCamera(CameraUpdateFactory.zoomOut());
        });

        final LatLng[] userLocation = {null};
        // if we have custom location from Main Activity then use that location
        // otherwise get user's current location
        if(customLocation != null) {
            userLocation[0] = customLocation;

            // Add a marker for the input location
            googleMap.addMarker(new MarkerOptions()
                    .position(customLocation)
                    .title("Input Location"));
            System.out.println("Input location set! lat = " + customLocation.latitude + " , long = " + customLocation.longitude);

            // Move the camera to the input location and zoom in
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(customLocation, 15); // Adjust zoom level as needed
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(customLocation));
            googleMap.animateCamera(cameraUpdate);
            findParkingButton.setClickable(true);
        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    userLocation[0] = new LatLng(location.getLatitude(), location.getLongitude());
                    // Add a marker for the user's location
                    googleMap.addMarker(new MarkerOptions()
                            .position(userLocation[0])
                            .title("Your Location"));
                    System.out.println("User location set! lat = " + userLocation[0].latitude + " , long = " + userLocation[0].longitude);

                    // Move the camera to the user's location and zoom in
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation[0], 15); // Adjust zoom level as needed
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation[0]));
                    googleMap.animateCamera(cameraUpdate);
                    findParkingButton.setClickable(true); // enable find parking places button (P icon)
                }
            });
        }

        // adds functionality to find parking button
        findParkingButton.setOnClickListener((View view) -> {
            System.out.println("Making parking places request!");
            JsonObjectRequest request = generateFindParkingRequest(userLocation, parkingMarkers);
            mRequestQueue.add(request);
        });

        // add marker on Google Map where user points
        googleMap.setOnMarkerClickListener((Marker marker) -> {
            if (parkingMarkers.contains(marker)) {
                // Remove the previously clicked marker, if any
                googleMap.clear();
                LatLng clickedMarkerLatLng = marker.getPosition();
                String title = marker.getTitle();
                parkingMarkers.clear();
                googleMap.addMarker(new MarkerOptions().position(userLocation[0])
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                // Add a new marker at the clicked location
                googleMap.addMarker(new MarkerOptions().position(clickedMarkerLatLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                // Calculate directions from the clicked location to the user's location
                calculateDirections(clickedMarkerLatLng, userLocation[0]);
                return true;
            }
            return false;
        });
    }

    // generateFindParkingRequest calls Google's nearby search API to find parking places near given location
    private JsonObjectRequest generateFindParkingRequest(LatLng[] userLocation, List<Marker> parkingMarkers) {
        // add user's current location lat, long
        String url = baseUrl.replace("LAT", String.valueOf(userLocation[0].latitude));
        url = url.replace("LNG", String.valueOf(userLocation[0].longitude));
        url += getString(R.string.google_maps_key);

        return new JsonObjectRequest(Request.Method.GET, url, null,
            (JSONObject response) -> {
                // Handle the JSON response here
                JSONArray results;
                try {
                    results = response.getJSONArray("results");
                    System.out.println("Places response " + response);
                    // Iterate through the results and add markers for each parking spot
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject place = results.getJSONObject(i);
                        JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                        double lat = location.getDouble("lat");
                        double lng = location.getDouble("lng");

                        // Add a marker for the parking spot
                        BitmapDescriptor descriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                        Marker currentMarker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(place.getString("name"))
                                .icon(descriptor));

                        parkingMarkers.add(currentMarker);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            },(VolleyError error) -> System.out.println("Error : " + error.networkResponse));
    }

    // calculateDirections defines method to calculate possible routes from source to destination
    private void calculateDirections(LatLng destination, LatLng origin) {
        Log.d(TAG, "calculateDirections: calculating directions from " + origin);

        // get destination lat lng
        com.google.maps.model.LatLng destinationLatLng = new com.google.maps.model.LatLng(
                destination.latitude,
                destination.longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        directions.alternatives(true); // to show all possible routes
        directions.origin(new com.google.maps.model.LatLng(
                origin.latitude,
                origin.longitude
        ));
        Log.d(TAG, "calculateDirections: destination: " + destination);

        directions.destination(destinationLatLng).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                if(googleMap == null) {
                    System.out.println("Map is null here...");
                }
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "Directions onFailure: " + e);
            }
        });
    }

    // addPolylinesToMap defines method to draw path for DirectionsResult (routes from source to destination)
    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "run: result routes: " + result.routes.length);

            boolean hasFirstPath = false;
            for(DirectionsRoute route: result.routes) {
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                List<LatLng> newDecodedPath = new ArrayList<>();

                // This loops through all the LatLng coordinates of ONE polyline.
                for(com.google.maps.model.LatLng latLng: decodedPath){
                    newDecodedPath.add(new LatLng(
                            latLng.lat,
                            latLng.lng
                    ));
                }
                if(googleMap == null) {
                    System.out.println("Map is null !!!");
                }
                Polyline polyline = googleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                if(!hasFirstPath) {
                    polyline.setColor(Color.BLUE);
                    polyline.setWidth(30);
                    hasFirstPath = true;
                } else {
                    polyline.setColor(Color.GRAY);
                    polyline.setWidth(15);
                }
                polyline.setClickable(true);
            }
        });
    }

    // onRequestPermissionsResult defines method to request Location permission from User
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // load map once user grants permissions
                mapView.getMapAsync(this);
            } else {
                // Handling permission denial
                // Navigating back to the main activity here
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent); // Start Main activity again
                finish(); // Optional: Finish the current activity to prevent returning to it when back is pressed

            }
        }
    }

    // Default methods from AppCompatActivity related to Memory consumption and system crashes

    @Override
    protected void onResume() {
        System.out.println("App Resumed!!!");
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        System.out.println("App Paused!!!");
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        System.out.println("Destroying . . .");
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        System.out.println("Low Memory!!!");
        super.onLowMemory();
        mapView.onLowMemory();
    }
}