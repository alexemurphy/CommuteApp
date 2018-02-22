package commute.commuteapp;


import java.util.Calendar;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Date;

/**
 * The class that tracks the route and times between each
 */
public class Route {
    //The route with the latitude and longitude measurements
    private ArrayList<LatLng> routeMap= new ArrayList<LatLng>();
    //The time distances between each LatLng
    private ArrayList<Integer> timeMap = new ArrayList<Integer>();

    //The number of nodes in the route
    int index;

    //Timing
    long startTime;
    long elapsedTime;



    //Basic Information
    String JourneyName;
    String routeName;
    String transportMethod;

    /**
     * Constructor class
     */
    public Route(){
        index = -1;
    }


    //-----------------------------------Accessors & Mutators-----------------------------------\\

    /**
     * Mutator to add a new node to the route
     *
     * @param latitudeAndLonitude : The latitude and lonitude (LatLng) of the new node
     * @param time : The time from the last node collection
     */
    public void addNode(LatLng latitudeAndLonitude, Integer time){
        routeMap.add(latitudeAndLonitude);
        timeMap.add(time);
        index++;
    }

    /**
     * Accessor method to get the latitude and longitude (LatLng) of the node
     *
     * @param nodeNumber : The index of the node to be added
     * @return  : The node
     */
    public LatLng getLatitudeAndLongitude(int nodeNumber){
        return routeMap.get(nodeNumber);
    }

    /**
     * Accessor method to get the time in between the nodes
     *
     * @param nodeNumber : The number of the node to get
     * @return : The integer of the time between nodes
     */
    public Integer getTime(int nodeNumber){
        return timeMap.get(nodeNumber);
    }

    /**
     * Accessor to get the number of nodes in the route
     *
     * @return : The number of nodes
     */
    public int getNumberOfNodes(){
        //Add 1 to get number of nodes in the route,  not the index
        return index + 1;
    }

    /**
     * Mutator to set route routeName
     * @param nm : The routeName of the route
     */
    public void setRouteName(String nm){
        routeName = nm;
    }

    /**
     * Accessor to get the route routeName
     * @return : The routeName of the route
     */
    public String getRouteName(){
        return routeName;
    }

    public String getJourneyName() {
        return JourneyName;
    }

    public void setJourneyName(String journeyName) {
        JourneyName = journeyName;
    }

    /**
     * Mutator to set transport method
     * @param tm : The transport method of the route
     */
    public void setTransportMethod(String tm){
        transportMethod = tm;
    }

    /**
     * Accessor to get the transport method
     * @return : The transport method of the route
     */
    public String getTransportMethod(){
        return transportMethod;
    }

    /**
     * Set the start time to be the current time
     */
    public void setStartTime(){
        startTime = System.nanoTime();
    }

    /**
     * Get the start time in nano seconds
     * @return : The start time
     */
    public float getStartTime(){
        return startTime;
    }

    /**
     * Set the elapsed time using the start time and the current time
     */
    public void setElapsedTime(){
        //Find the elapsed time in seconds
        elapsedTime = (startTime - System.nanoTime()) * 1000000000;
    }

    /**
     * Get the elapsed time that is stored in seconds
     * @return : The elapsed time stored in seconds
     */
    public float getElapsedTime(){
        return elapsedTime;
    }
}
