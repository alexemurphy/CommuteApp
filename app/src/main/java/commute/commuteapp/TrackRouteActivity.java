package commute.commuteapp;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Toast;


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

    //Trip
    Trip trackedTrip = new Trip();
    boolean measureRoute;

    //Trip save instance
    SaveTripClass saveTrip;

    //Journeys and routes list
    ArrayList<ArrayList<String>> journeys;
    ArrayList<ArrayList<String>> routes;
    private String spinnerResult = "car";



    //-----------------------------------Setup-----------------------------------\\

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
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
                    trackedTrip.setStartTime();

                    //Create a handler to run the add route method every 5 seconds
                    final Handler handler = new Handler();
                    final int delay = 5000; //milliseconds

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Log.d("Handler: ", "Add To Trip");
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
                trackedTrip.setElapsedTime();

                //Instantiate a save trip
                saveTrip = new SaveTripClass(trackedTrip, getApplicationContext());
                saveTrip.saveTrip();
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
        trackedTrip.addNode(deviceLocation);
        Log.d("addToRoute", "deviceLocation = " + deviceLocation.latitude + " , " + deviceLocation.longitude);

        //Create Polyline from the current location to the past location
        //If there is at least 1 other node in the route
        if(trackedTrip.getNumberOfNodes() > 1) {
            //Get the last node
            int count = trackedTrip.getNumberOfNodes();
            LatLng prevNode = trackedTrip.getLatitudeAndLongitude(count-2);
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

        //Setup new journey button
        final Button newJourney = findViewById(R.id.newJourney);
        newJourney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            showJourneyInputBox();
            }
        });

        //Setup set journey button
        final Button setJourney = findViewById(R.id.setJourney);
        setJourney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Set the routes compared to the journey
                String ID = getIDFromName(journeys, ((Spinner)findViewById(R.id.journeyDropdown)).getSelectedItem().toString());
                setRoutes(ID);
            }
        });

        //Setup new journey button
        final Button newRoute = findViewById(R.id.newRoute);
        newRoute.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showRouteInputBox();
            }
        });

        //Setup save button
        final Button save = findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Get all of the data values from the GUI
                if(getAllValues()) {
                    //If gathering was successful, continue
                    saveTrip.setTripToSave(trackedTrip);

                    //Save trip
                    saveTrip.saveTrip();

                    //Return to main menu
                    Intent intent = new Intent(TrackRouteActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void saveButton(View view){
        //Get all of the data values from the GUI
        getAllValues();

        //Save trip
        saveTrip.saveTrip();

        //Return to main menu
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    /**
     * Gets the ID from the String List using the name presented to the user
     *
     * @param mainList : The list that stores the IDs and the names
     * @param target : The String of the name
     * @return  : The ID or null if the name cannot be found
     */
    private String getIDFromName(ArrayList<ArrayList<String>> mainList, String target){
        for(int i = 0; i < mainList.size(); i++){
            if(mainList.get(i).get(1).equals(target)){
                return mainList.get(i).get(1);
            }
        }
        return null;
    }

    /**
     * Show a popup for entering a new journey name //TODO FIX THIS TO ADD JOURNEYS
     */
    private void showJourneyInputBox(){
        //Setup the alert view
        setContentView(R.layout.activity_newjourney);

        //Buttons
        //Cancel Button
        final Button cancel = findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveMenuSetup();
            }
        });

        //Save Button
        final Button save = findViewById(R.id.btnSaveJourney);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String origin = ((EditText) findViewById(R.id.originName)).getText().toString();
                String dest = ((EditText) findViewById(R.id.destinationName)).getText().toString();

                if(origin.equals("") || dest.equals("")){
                    throwWarningMessage("Origin or Destination Name Empty");
                }
                else {
                    saveTrip.setNewJourney(origin, dest);
                    saveMenuSetup();
                }
            }
        });
    }

    /**
     * Show a popup for entering a new route name //TODO FIX THIS TO ADD ROUTES
     */
    private void showRouteInputBox(){
        //Setup the alert view
        setContentView(R.layout.activity_newroute);

        //Dropdown
        //Get all of the transport methods
        ArrayList<String> transportMethods = new ArrayList<>();
        transportMethods.add("Car");
        transportMethods.add("Bus");
        transportMethods.add("Bike");
        transportMethods.add("Train");
        transportMethods.add("Foot");
        transportMethods.add("Plane");
        transportMethods.add("Boat");

        //Add all elements to the list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, transportMethods);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        ((Spinner) findViewById(R.id.spinner_transport_method)).setAdapter(adapter);

        //Buttons
        //Cancel Button
        final Button cancel = findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveMenuSetup();
            }
        });

        //Save Button
        final Button save = findViewById(R.id.btnCreateRoute);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String routeName = ((EditText) findViewById(R.id.routeName)).getText().toString();
                if(routeName.equals("") || saveTrip.checkRouteNameExists(routeName)){
                    throwWarningMessage("Invalid Route Name Provided!");
                }
                else {
                    saveTrip.setNewRoute(routeName, ((Spinner) findViewById(R.id.spinner_transport_method)).getSelectedItem().toString());
                    saveMenuSetup();
                }
            }
        });

    }

    /**
     * Gets all of the values from the saveTripMenu
     *
     * @return : True if successful, false otherwise
     */
    private boolean getAllValues(){
        //TODO Fix this
        //Journey ID
        String ID;
        try {
            ID = getIDFromName(journeys, ((Spinner) findViewById(R.id.journeyDropdown)).getSelectedItem().toString());
            trackedTrip.setJourneyID(new Integer(ID)); //TODO THIS NEEDS TO GO -AM
        }
        catch(Exception e){
            throwWarningMessage("No Journey Name Set");
            return false;
        }
        try {
            ID = getIDFromName(routes, ((Spinner) findViewById(R.id.routeDropdown)).getSelectedItem().toString());
            //Route ID
            trackedTrip.setRouteID(new Integer(ID));
        }
        catch(Exception e){
            throwWarningMessage("No Route Name Set");
            return false;
        }
        return true;
    }

    /**
     * Throws a warning message to the user
     *
     * @param msg : The string to be thrown
     */
    private void throwWarningMessage(String msg){
        Context c = getApplicationContext();
        Toast t = Toast.makeText(c, msg, Toast.LENGTH_SHORT);
        t.show();
    }

    /**
     * Fill the journey dropdown menu with every journey in the database
     */
    private void setJourneys(){
        journeys = saveTrip.getJourneyList();

        //Get all of the strings of the journey
        ArrayList<String> journeyStrings = new ArrayList<String>();
        for(int i = 0; i < journeys.size(); i++){
            journeyStrings.add(journeys.get(i).get(1));
        }

        //Add all elements to the list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, journeyStrings);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        ((Spinner) findViewById(R.id.journeyDropdown)).setAdapter(adapter);
    }

    /**
     * Set the route dropdown menu with every route in the specified journey
     *
     * @param ID : The ID of the journey
     */
    private void setRoutes(String ID){
        routes = saveTrip.getRouteList(ID);

        //Get all of the strings of the journey
        ArrayList<String> routeStrings = new ArrayList<String>();
        for(int i = 0; i < routes.size(); i++){
            routeStrings.add(routes.get(i).get(1));
        }
        //Add all elements to the list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, routeStrings);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        ((Spinner) findViewById(R.id.routeDropdown)).setAdapter(adapter);
    }

}
