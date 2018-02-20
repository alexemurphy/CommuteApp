package commute.commuteapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

//TODO List
/**
 * Save data to file
 * Open data from file
 */




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

    /**
     * Constructor class
     */
    public Route(){
        index = -1;
    }

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

}
