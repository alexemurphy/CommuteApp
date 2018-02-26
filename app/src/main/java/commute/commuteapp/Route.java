package commute.commuteapp;

/**
 * Created by alexmurphy on 23/02/2018.
 */

public class Route {

    private int ID;
    private int journeyID;
    private float distance;
    private String transportType;

    public int getID(){

        return ID;

    }

    public void setID(int IDIn){

        ID = IDIn;
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





}
