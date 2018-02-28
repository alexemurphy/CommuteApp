package commute.commuteapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    //Error handling for incorrect version of play store
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    /**
     * The main method of the program
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the layout to be the main menu
        setContentView(R.layout.activity_main);


        //If the google play service is available
        if(checkGoogleServiceVersion()){
            init();
        }

    }

    /**
     * Initialise the buttons
     */
    private void init(){
        //Create a button to navigate to the map
        Button trackMap = findViewById(R.id.TrackMap);
        trackMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Change to map view
                goToMap();
            }
        });

        Button routeMap = findViewById(R.id.RouteMap);
        routeMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goToLoad();
            }
        });
    }

    public void goToLoad(){
        Intent intent = new Intent(this, LoaderActivity.class);
        startActivity(intent);
    }

    public void goToMap() {
        Intent intent = new Intent(this, TrackRouteActivity.class);
        startActivity(intent);
    }

    /**
     * Check that the correct version of Google Play Services is installed
     *
     * @return : True if the correct version is installed, false otherwise
     */
    public boolean checkGoogleServiceVersion(){
        //Check whether the google API is available
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        //If the play store is up to date - map requests can be made
        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        //The user can update the google play services
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            return false;
        }
        //Can't make map requests
        else{
            return false;
        }
    }
}
