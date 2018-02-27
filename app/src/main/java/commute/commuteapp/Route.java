package commute.commuteapp;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by alexmurphy on 23/02/2018.
 */

public class Route {

    private int ID;
    private String name;
    private int journeyID;
    private float distance;
    private String transportType;


    public int getID(){

        return ID;

    }

    public void setID(int IDIn){

        ID = IDIn;
    }

    public String getName(){

        return name;
    }

    public void setName(String nameIn){

        name = nameIn;

    }

    public int getJourneyID(){

        return journeyID;
    }

    public void setJourneyID(int journeyIDin){

        journeyID = journeyIDin;

    }

    public float getDistance() {

        return distance;

    }

    public void setDistance(float distanceIn){

        distance = distanceIn;

    }

    public String getTransportType(){

        return transportType;
    }

    public void setTransportType(String transportTypeIn){

        transportType = transportTypeIn;

    }

    public ArrayList<String> getAllTrips(){

        //TODO access database and return all trips with same route ID

        return null;

    }





}
