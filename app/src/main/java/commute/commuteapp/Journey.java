package commute.commuteapp;



/**
 * Created by alexmurphy on 23/02/2018.
 */

public class Journey {

    private int ID;
    private String destination;
    private String origin;


    public int getID(){

        return ID;

    }

    public void setID(int IDIn){

        ID = IDIn;
    }



    public String getOrigin() {

        return origin;

    }

    public void setOrigin(String originIn){

        origin = originIn;

    }

    public String getDestination(){

        return destination;
    }

    public void setDestination(String destinationIn){

        destination = destinationIn;

    }




    
}
