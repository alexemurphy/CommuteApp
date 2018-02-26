package commute.commuteapp;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Class for saving the route
 */

public class SaveTripActivity extends AppCompatActivity {
    Trip tripToSave;

    /**
     * Constructor
     * @param inputTrip : The route to be input
     */
    public SaveTripActivity(Trip inputTrip){
        tripToSave = inputTrip;
    }

    public void saveTrip(){
        //TODO Save the data from Trip class
    }

    public ArrayList<String> getJourneyList(){
        //TODO Get a list of the journeys from the save
        return new ArrayList<String>();
    }

    public ArrayList<String> getRouteList(String journey){
        //TODO Get a list of the routes from a specific journey
        return new ArrayList<String>();
    }

    public void setNewJourneyName(String name){
        //TODO Set a new journey with the name given
    }

    public void setNewRouteName(String name){
        //TODO Set a new trip with the name given
    }



}
