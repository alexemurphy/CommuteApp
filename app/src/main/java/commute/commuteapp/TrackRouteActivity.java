package commute.commuteapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


public class TrackRouteActivity extends AppCompatActivity implements OnMapReadyCallback{

    //Location Permissions
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean mLocationPermissionsGranted = false;

    //The Map
    private GoogleMap mMap;

    private LatLng deviceLocation;

    //Route
    Route trackedRoute = new Route();
    boolean measureRoute;

    //Trip save instance
    SaveTripActivity saveTrip;


    //-----------------------------------Setup-----------------------------------\\

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Turn route measuring off to start with
        measureRoute = false;

        //Show the activity map GUI
        setContentView(R.layout.activity_trackmap);

        //Check location Permissions
        getLocationPermission();

        //Initialise the buttons
        init();
    }

    /**
     * Setup the buttons
     */
    private void init(){
        //Create a button to navigate to the map
        final Button start = findViewById(R.id.startRoute);
        start.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view){
                if(measureRoute){
                    measureRoute = false;
                    start.setText(R.string.resumeText);
                }
                else {
                    //Turn route measuring on
                    measureRoute = true;
                    trackedRoute.setStartTime();

                    //Create a handler to run the add route method every 5 seconds
                    final Handler handler = new Handler();
                    final int delay = 5000; //milliseconds

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Log.d("Handler: ", "Add To Route");
                            if(measureRoute) {
                                addToRoute();
                            }
                            handler.postDelayed(this, delay);
                        }
                    }, delay);

                    start.setText("Pause");

                }
            }
        });

        //Create a button to navigate to the map
        Button stop = findViewById(R.id.stopRoute);
        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //TODO Do other stuff before moving back to main menu - e.g. save the route
                //Stop route tracking
                measureRoute = false;

                //Stop the timer
                trackedRoute.setElapsedTime();

                //Instantiate a save trip
                saveTrip = new SaveTripActivity(trackedRoute);

                //Load the save menu
                saveMenuSetup();
            }
        });
    }

    /**
     * Initialise the map
     */
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(TrackRouteActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //If location permissions granted
        if (mLocationPermissionsGranted) {
            //Get the devices location, and focus the map onto it
            getDeviceLocation();

            //Show the current location on the map if permissions are granted (auto generated code)
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);

            //Allow the zoom controls
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    /**
     * Add the current location to the route and generate a polyline if appropriate
     */
    private synchronized void addToRoute(){
        //Update the device's current location
        getDeviceLocation();
        //TODO change the time to be meaningful
        trackedRoute.addNode(deviceLocation, 0);
        Log.d("addToRoute", "deviceLocation = " + deviceLocation.latitude + " , " + deviceLocation.longitude);

        //Create Polyline from the current location to the past location
        //If there is at least 1 other node in the route
        if(trackedRoute.getNumberOfNodes() > 1) {
            //Get the last node
            int count = trackedRoute.getNumberOfNodes();
            LatLng prevNode = trackedRoute.getLatitudeAndLongitude(count-2);
            //Create the Polyline
            addPolyline(new LatLng(deviceLocation.latitude, deviceLocation.longitude), new LatLng(prevNode.latitude, prevNode.longitude));
        }
    }

    //-----------------------------------Map Functions-----------------------------------\\

    /**
     * Moves camera to a specific location
     *
     * @param latitudeAndLongitude : The latitude and longitude of the location
     * @param zoom : The zoom of the camera
     */
    private void moveCamera(LatLng latitudeAndLongitude, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeAndLongitude, zoom));
    }

    /**
     * Add a polyline to the map
     * @param latitudeAndLongitudeStart : The start latitude and longitude of the coordinate
     * @param latitudeAndLongitudeEnd : The end latitude and longitude of the coordinate
     */
    private void addPolyline(LatLng latitudeAndLongitudeStart, LatLng latitudeAndLongitudeEnd){
        mMap.addPolyline(new PolylineOptions().add(latitudeAndLongitudeStart, latitudeAndLongitudeEnd).width(5).color(Color.BLUE));

    }

    /**
     * Get the device's current location, display it on the map and move the camera to the location
     */
    private void getDeviceLocation(){
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            //If location permissions have been granted
            if(mLocationPermissionsGranted){
                //Check for the last known location
                Task location = mFusedLocationProviderClient.getLastLocation();

                //Constantly update the location of the device
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        //If the location exists, plot it on the map
                        if (task.isSuccessful() && task.getResult() != null) {
                            //Location Found
                            Location currentLocation = (Location) task.getResult();

                            //Set the currentLocation variable
                            deviceLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            //Move the camera to the current position using the current zoom level
                            moveCamera(deviceLocation, mMap.getCameraPosition().zoom);
                        }
                    }
                });
            }
        }
        catch(SecurityException ignored){
        }

    }

    //-----------------------------------Permissions-----------------------------------\\

    /**
     * Check whether the permissions for location has been granted.
     * If not, request the permissions from the user
     */
    private void getLocationPermission(){
        String[] permissions= {FINE_LOCATION, COARSE_LOCATION};

        //Fine location permission
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //Coarse location permission
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            }
            else{
                //Request user for permission
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            //Request user for permission
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int ignored : grantResults) {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;

                    //Initialize map
                    initMap();

                }
            }
        }
    }

//-----------------------------------Save Trip-----------------------------------\\
    /**
     * Setup the save menu to save the current route
     */
    private void saveMenuSetup(){
        //Display the save screen
        setContentView(R.layout.activity_savetrip);

        //Setup Journey View
        setJourneys();

        //Setup set journey button
        final Button setJourney = findViewById(R.id.setJourney);
        setJourney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Set the routes compared to the journey
                setRoutes(((Spinner)findViewById(R.id.journeyDropdown)).getSelectedItem().toString());
            }
        });

        //Setup save button
        final Button save = findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Get all of the data values from the GUI
                getAllValues();

                //Save trip
                saveTrip.saveTrip();

                //Return to main menu
                Intent intent = new Intent(TrackRouteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Gets all of the values from the saveTripMenu
     */
    private void getAllValues(){
        trackedRoute.setRouteName(((EditText)findViewById(R.id.tripSave)).getText().toString());
        trackedRoute.setTransportMethod(((EditText)findViewById(R.id.transportMethodInput)).getText().toString());
        trackedRoute.setJourneyName(((Spinner)findViewById(R.id.journeyDropdown)).getSelectedItem().toString());
        trackedRoute.setRouteName(((Spinner)findViewById(R.id.routeDropdown)).getSelectedItem().toString());
    }

    /**
     * Fill the journey dropdown menu with every journey in the database
     */
    private void setJourneys(){
        ArrayList<String> journeys= saveTrip.getJourneyList();

        //Add all elements to the list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, journeys);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.journeyDropdown)).setAdapter(adapter);
    }

    /**
     * Set the route dropdown menu with every route in the specified journey
     *
     * @param journeyName : The name of the journey
     */
    private void setRoutes(String journeyName){
        ArrayList<String> routes = saveTrip.getRouteList(journeyName);
        //Add all elements to the list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, routes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.routeDropdown)).setAdapter(adapter);
    }

}
