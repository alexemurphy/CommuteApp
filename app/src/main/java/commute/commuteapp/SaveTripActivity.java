package commute.commuteapp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Class for saving the route
 */

public class SaveTripActivity extends AppCompatActivity {
    Trip tripToSave;
    SQLiteHelper SQLiteHelper = new SQLiteHelper(this);

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


        if(tripToSave.getIndex() > 0) {
            SQLiteHelper.addTrip(tripToSave);
        }


    }

    /**
     * Get an array of all journey names
     * [[ID, Origin, Destination]]
     */
    public ArrayList<ArrayList<String>> getJourneyList(){
        //TODO Broken for empty table

        ArrayList<Journey> allJourneys;
        ArrayList<ArrayList<String>> journeyNames = new ArrayList<>();

        try {

            allJourneys = SQLiteHelper.getAllJourneys();

            int allJourneysSize = allJourneys.size();
            int ID;
            String origin;
            String destination;


            for(int i = 0; i < allJourneysSize; i++){
                ID = allJourneys.get(i).getID();
                origin = allJourneys.get(i).getOrigin();
                destination = allJourneys.get(i).getDestination();
                ArrayList<String> currentJourneyInfo = new ArrayList<>();
                currentJourneyInfo.add(0, Integer.toString(ID));
                currentJourneyInfo.add(1, origin);
                currentJourneyInfo.add(2, destination);

                journeyNames.add(currentJourneyInfo);

            }

        }
        catch(NullPointerException e){

            ArrayList<String> empty  = new ArrayList<>();
            empty.add(0, "-1");
            empty.add(1, "");
            empty.add(2, "");
            journeyNames.add(empty);

        }

        return journeyNames;
    }

    /**
     * Get an array of all Route names from the specific ID
     * [[ID, String]]
     */
    public ArrayList<ArrayList<String>> getRouteList(String ID){


        ArrayList<ArrayList<String>> routeNames = new ArrayList<>();

        try {


            ArrayList<Route> allRoutes = SQLiteHelper.getAllRoutesInJourney(Integer.parseInt(ID));
            int allRoutesSize = allRoutes.size();
            int routeID;
            String routeName;


            for (int i = 0; i < allRoutesSize; i++) {
                routeID = allRoutes.get(i).getID();
                routeName = allRoutes.get(i).getName();
                ArrayList<String> currentJourneyInfo = new ArrayList<>();
                currentJourneyInfo.add(0, Integer.toString(routeID));
                currentJourneyInfo.add(1, routeName);
                routeNames.add(currentJourneyInfo);
            }
        }
        catch(NullPointerException e){

            ArrayList<String> empty = new ArrayList<>();
            empty.add(0, "-1");
            empty.add(1,"");
            routeNames.add(empty);

        }

        return routeNames;

    }

    /** //TODO REMOVE THIS
     * Store the name of a new journey
     *
     * @param origin : The origin of the journey
     * @param destination : The destination of the journey
     */
    public void setNewJourney(String origin, String destination){
        //TODO Set a new journey with the name given
    }

    /** //TODO REMOVE THIS
     * Store the name of a new route
     *
     * @param name : The name of the route
     * @param transportMethod : The method of transport of the route
     */
    public void setNewRoute(String name, String transportMethod){
        //TODO Set a new trip with the name given
        Log.d("setNewRoute", "Name: " + name + " Transport Method: " + transportMethod);
    }

}
