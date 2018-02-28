package commute.commuteapp;




import android.content.Context;
import android.util.Log;


import java.util.ArrayList;




/**
 * Class for saving the route
 */

public class SaveTripClass {
    Trip tripToSave;
    SQLiteHelper helper;
    Context c;

    /**
     * Empty Constructor
     */
    public SaveTripClass(){}

    /**
     * Constructor
     * @param inputTrip : The route to be input
     */
    public SaveTripClass(Trip inputTrip, Context c){

        tripToSave = inputTrip;
        this.c = c;
        helper = new SQLiteHelper(c);
    }

    public void setTripToSave(Trip inpTrip){
        tripToSave = inpTrip;
    }

    /**
     * Checks whether the route name has been used before
     *
     * @param routeName : The name of the route
     * @return : True if has been used, false if the name has not been used
     */
    public boolean checkRouteNameExists(String routeName){
        return false;
    }

    /**
     * Interact with the SQLiteHelper to save the trip
     */
    public void saveTrip(){


        if(tripToSave.getIndex() > 0) {
            helper.addTrip(tripToSave);
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

            allJourneys = helper.getAllJourneys();

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


            ArrayList<Route> allRoutes = helper.getAllRoutesInJourney(ID);
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

        Journey journey = new Journey();
        journey.setDestination(destination);
        journey.setOrigin(origin);

        helper.addJourney(journey);
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

    /**
     * Delete the journey via its ID
     *
     * @param journeyID
     */
    public void deleteJourney(String journeyID){
        //TODO Delete the journey by the ID
    }

    /**
     * Save Journey
     *
     * @param journey: The journey to be saved
     */
    public void saveJourney(ArrayList<String> journey){
        //TODO Save the journey
    }
}
