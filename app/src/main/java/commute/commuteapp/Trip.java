package commute.commuteapp;



import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;


/**
 * The class that tracks the route and times between each
 */
public class Trip {
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
    int journeyID;
    int routeID;
    int ID;
    String tripName;
    String TransportMethod;



    String LatLangFileLocation;

    /**
     * Constructor class
     */
    public Trip(){
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


    public int getID() {
        return ID;


    }

    public void setID(int ID) {
        this.ID = ID;
    }


    public void setLatLangFileLocation(String latLangFileLocation) {
        LatLangFileLocation = latLangFileLocation;
    }

    public String getLatLangFileLocation() {
        return LatLangFileLocation;

    }



    /**
     * Set the start time to be the current time
     */
    public void setStartTime(){
        startTime = System.nanoTime();
    }

    public void setStartTime(long startTime1) {
        startTime = startTime1;
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

    public void setElapsedTime(long elapsedTime1) {

        elapsedTime = elapsedTime1;
    }

    /**
     * Get the elapsed time that is stored in seconds
     * @return : The elapsed time stored in seconds
     */
    public float getElapsedTime(){
        return elapsedTime;
    }


    public int getRouteID() {

       return routeID;

    }

    public void setRouteID(int routeID1){

        routeID = routeID1;
    }

    public int getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(int journeyID) {
        this.journeyID = journeyID;
    }
    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTransportMethod() {
        return TransportMethod;
    }

    public void setTransportMethod(String transportMethod) {
        TransportMethod = transportMethod;
    }


}

