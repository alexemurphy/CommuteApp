package commute.commuteapp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Class for saving the route
 */

public class SaveTripActivity extends AppCompatActivity {
    Route routeToSave;

    /**
     * Constructor
     * @param inputRoute : The route to be input
     */
    public SaveTripActivity(Route inputRoute){
        routeToSave = inputRoute;
    }

    public void saveTrip(){
        //TODO Save the data from Route class
    }

    public ArrayList<String> getJourneyList(){
        //TODO Get a list of the journeys from the save
        return new ArrayList<String>();
    }

    public ArrayList<String> getRouteList(String journey){
        //TODO Get a list of the routes from a specific journey
        return new ArrayList<String>();
    }



}
