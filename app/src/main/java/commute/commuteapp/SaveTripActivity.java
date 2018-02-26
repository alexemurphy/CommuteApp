package commute.commuteapp;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Class for saving the route
 */

public class SaveTripActivity extends AppCompatActivity {
    Trip tripToSave;

    /**
     * Emptu Constructor
     */
    public SaveTripActivity(){}

    /**
     * Constructor
     * @param inputTrip : The route to be input
     */
    public SaveTripActivity(Trip inputTrip){
        tripToSave = inputTrip;
    }

    /**
     * Interact with the SQLiteHelper to save the trip
     */
    public void saveTrip(){
        //TODO Save the data from Trip class
    }

    /**
     * Get an array of all journey names
     * [[ID, String]]
     */
    public ArrayList<ArrayList<String>> getJourneyList(){
        //TODO Get a list of the journeys from the save
        ArrayList<ArrayList<String>> test =  new ArrayList<>();
        ArrayList<String> t1 =  new ArrayList<String>();

        t1.add("1");
        t1.add("Hello world");
        test.add(t1);
        return test;
    }

    /**
     * Get an array of all Route names from the specific ID
     * [[ID, String]]
     */
    public ArrayList<ArrayList<String>> getRouteList(String ID){
        //TODO Get a list of the routes from a specific journey
        return new ArrayList<ArrayList<String>>();
    }

    /**
     * Store the name of a new journey
     *
     * @param name : The name of the journey
     */
    public void setNewJourneyName(String name){
        //TODO Set a new journey with the name given
    }

    /**
     * Store the name of a new route
     *
     * @param name : The name of the route
     */
    public void setNewRouteName(String name){
        //TODO Set a new trip with the name given
    }

}
