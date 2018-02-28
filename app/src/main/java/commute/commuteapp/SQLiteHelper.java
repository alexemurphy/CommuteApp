package commute.commuteapp;


/**
 * Created by alexmurphy on 23/02/2018.
 *
 * This class exists to allow permanent storage of Journeys, Routes and Trips.
 *
 *
 */

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class SQLiteHelper extends SQLiteOpenHelper{



/**
*
* Static variables needed for accessing the database
*
* */


    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "CommuteDB";



    // Table names
    private static final String TABLE_JOURNEY = "journey";
    private static final String TABLE_TRIP = "trip";
    private static final String TABLE_ROUTE = "route";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESTINATION = "destination";
    private static final String KEY_JOURNEYID = "journeyID";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_TRANSPORT_TYPE = "transportType";
    private static final String KEY_ROUTEID = "routeID";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_LATLANGFILE = "LatLangFile";
    private static final String KEY_TIMETAKEN = "timetaken";

    private static final String[] COLUMNS_JOURNEY = {KEY_ID,KEY_ORIGIN,KEY_DESTINATION};

    private static final String[] COLUMNS_ROUTE = {KEY_ID,KEY_NAME, KEY_JOURNEYID,KEY_DISTANCE,KEY_TRANSPORT_TYPE};

    private static final String[] COLUMNS_TRIP = {KEY_ID,KEY_ROUTEID,KEY_TIMESTAMP,KEY_LATLANGFILE, KEY_TIMETAKEN};

    Context c;



    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c = context;

    }

    /**
     * Creating the tables in the database
     *
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table


        String CREATE_JOURNEY_TABLE = "CREATE TABLE journey ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "origin TEXT, "+
                "destination TEXT )";

        String CREATE_ROUTE_TABLE = "CREATE TABLE route ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "journeyID INTEGER, "+
                "distance REAL, "+
                "transportType TEXT ) ";


        String CREATE_TRIP_TABLE = "CREATE TABLE trip ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "routeID INTEGER, "+
                "timestamp TEXT, "+
                "LatLangFile TEXT, "+
                "timetaken REAL )";

        // create books table
        db.execSQL(CREATE_TRIP_TABLE);
        db.execSQL(CREATE_ROUTE_TABLE);
        db.execSQL(CREATE_JOURNEY_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS journey");
        db.execSQL("DROP TABLE IF EXISTS route");
        db.execSQL("DROP TABLE IF EXISTS trip");

        // create fresh books table
        this.onCreate(db);
    }

    

    public void addTrip(Trip trip){


        /*
        Getting the ID for the next trip to be added for the filename
        1. build the query
        */
        String query = "SELECT * FROM " + TABLE_TRIP +" ORDER BY column DESC LIMIT 1";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        trip.setID(Integer.parseInt(cursor.getString(0)) + 1);


        //Saving LatandLangs to file.

        String filename = saveLatLangFile(trip);
        trip.setLatLangFileLocation(filename);



        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTEID, trip.getRouteID());
        values.put(KEY_TIMESTAMP, trip.getStartTime()); // get title
        values.put(KEY_LATLANGFILE, trip.getLatLangFileLocation());
        values.put(KEY_TIMETAKEN, trip.getElapsedTime());

       // get author

        // 3. insert
        db.insert(TABLE_TRIP, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Trip getTrip(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_TRIP, // a. table
                        COLUMNS_TRIP, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        Trip trip = new Trip();
        trip.setID(Integer.parseInt(cursor.getString(0)));
        trip.setStartTime(Long.parseLong(cursor.getString(2)));
        trip.setElapsedTime(Long.parseLong(cursor.getString(4)));
        trip.setRouteID(Integer.parseInt(cursor.getString(1)));
        trip.setLatLangFileLocation(cursor.getString(3));
        trip.setLatLang(loadLatLangFile(trip.getLatLangFileLocation()));

        //log
        //Log.d("getBook("+id+")", book.toString());

        // 5. return book
        return trip;
    }


    public ArrayList<Trip> getAllTrips() {
        ArrayList<Trip> trips = new ArrayList<Trip>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_TRIP;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        //Trip trip = null;

        if (cursor.moveToFirst()) {
            do {
                Trip trip = new Trip();
                trip.setID(Integer.parseInt(cursor.getString(0)));
                trip.setStartTime(Long.parseLong(cursor.getString(2)));
                trip.setElapsedTime(Long.parseLong(cursor.getString(4)));
                trip.setRouteID(Integer.parseInt(cursor.getString(1)));
                trip.setLatLangFileLocation(cursor.getString(3));
                trip.setLatLang(loadLatLangFile(trip.getLatLangFileLocation()));


                // Add book to books
                trips.add(trip);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", trips.toString());

        // return books
        return trips;
    }

    public ArrayList<Trip> getAllTripsInRoute(int routeID) {
        ArrayList<Trip> trips = new ArrayList<Trip>();
        String routeIDStr = Integer.toString(routeID);



        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_TRIP, COLUMNS_TRIP,"routeID=?", new String[] {routeIDStr},  null,null,null);

        // 3. go over each row, build book and add it to list
        //Trip trip = null;

        if (cursor.moveToFirst()) {
            do {
                Trip trip = new Trip();
                trip.setID(Integer.parseInt(cursor.getString(0)));
                trip.setStartTime(Long.parseLong(cursor.getString(2)));
                trip.setElapsedTime(Long.parseLong(cursor.getString(4)));
                trip.setRouteID(Integer.parseInt(cursor.getString(1)));
                trip.setLatLangFileLocation(cursor.getString(3));
                trip.setLatLang(loadLatLangFile(trip.getLatLangFileLocation()));


                // Add book to books
                trips.add(trip);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", trips.toString());

        // return books
        return trips;
    }

    public int updateTrip(Trip trip) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTEID, trip.getRouteID());
        values.put(KEY_TIMESTAMP, trip.getStartTime()); // get title
        values.put(KEY_LATLANGFILE, trip.getLatLangFileLocation());
        values.put(KEY_TIMETAKEN, trip.getElapsedTime());

        // 3. updating row
        int i = db.update(TABLE_TRIP, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(trip.getID()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteTrip(Trip trip) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_TRIP, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(trip.getID()) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteBook", trip.toString());

    }

    public String saveLatLangFile(Trip trip){

        ArrayList<LatLng>  routeMap = trip.getRouteMap();
        int id = trip.getID();
        String filename = "trip_id-" + Integer.toString(id);
        int length = routeMap.size();
        String LatLang = null;

        try {

            FileOutputStream fos = c.getApplicationContext().openFileOutput(filename, c.MODE_PRIVATE);

            for(int i = 0; i < length; i++){

                LatLang =  Double.toString(routeMap.get(i).latitude) + "," + Double.toString(routeMap.get(i).longitude);

                fos.write(LatLang.getBytes());

            }


        }
        catch(FileNotFoundException e){

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return filename;

    }

    public ArrayList<LatLng> loadLatLangFile(String filename){

        ArrayList<LatLng> tripMap = new ArrayList<>();

        try {
            FileInputStream fis = c.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                String[] latLng = line.split(",");
                double latitude = Double.parseDouble(latLng[0]);
                double longitude = Double.parseDouble(latLng[1]);
                LatLng location = new LatLng(latitude, longitude);
                tripMap.add(location);


            }

            return tripMap;
            //return sb.toString();
        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {

        } catch (IOException e) {

        }
        return null;

    }





    public int addRoute(Route route){

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ROUTE, COLUMNS_ROUTE,"name=?", new String[] {route.getName()},  null,null,null);

        boolean empty = cursor.getCount() <= 0 && cursor != null;
        if(empty) {

            // 2. create ContentValues to add key "column"/value
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, route.getName());
            values.put(KEY_JOURNEYID, route.getJourneyID());
            values.put(KEY_DISTANCE, route.getDistance()); // get title
            values.put(KEY_TRANSPORT_TYPE, route.getTransportType());


            // get author

            // 3. insert
            db.insert(TABLE_ROUTE, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values

            // 4. close
            db.close();

            return 0;

        }
        else{

            return -1;

        }

    }

    public Route getRoute(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_ROUTE, // a. table
                        COLUMNS_ROUTE, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        Route route = new Route();
        route.setID(Integer.parseInt(cursor.getString(0)));
        route.setName(cursor.getString(1));
        route.setJourneyID(Integer.parseInt(cursor.getString(2)));
        route.setDistance(Integer.parseInt(cursor.getString(3)));
        route.setTransportType(cursor.getString(4));


        return route;
    }


    public ArrayList<Route> getAllRoutes() {
        ArrayList<Route> routes = new ArrayList<Route>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ROUTE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        //route route = null;

        if (cursor.moveToFirst()) {
            do {
                Route route = new Route();
                route.setID(Integer.parseInt(cursor.getString(0)));
                route.setName(cursor.getString(1));
                route.setJourneyID(Integer.parseInt(cursor.getString(2)));
                route.setDistance(Integer.parseInt(cursor.getString(3)));
                route.setTransportType(cursor.getString(4));


                // Add book to books
                routes.add(route);
            } while (cursor.moveToNext());
        }

        return routes;
    }

    public ArrayList<Route> getAllRoutesInJourney(int journeyID) {
        ArrayList<Route> routes = new ArrayList<Route>();
        String journeyIDStr = Integer.toString(journeyID);


        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
       // Cursor cursor = db.rawQuery(query, null);

        Cursor cursor = db.query(TABLE_ROUTE, COLUMNS_ROUTE,"journeyID=?", new String[] {journeyIDStr},  null,null,null);

        // 3. go over each row, build book and add it to list
        //route route = null;

        if (cursor.moveToFirst()) {
            do {
                Route route = new Route();
                route.setID(Integer.parseInt(cursor.getString(0)));
                route.setName(cursor.getString(1));
                route.setJourneyID(Integer.parseInt(cursor.getString(2)));
                route.setDistance(Integer.parseInt(cursor.getString(3)));
                route.setTransportType(cursor.getString(4));


                // Add book to books
                routes.add(route);
            } while (cursor.moveToNext());
        }

        return routes;
    }

    public int updateRoute(Route route) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, route.getName());
        values.put(KEY_JOURNEYID, route.getJourneyID());
        values.put(KEY_DISTANCE, route.getDistance()); // get title
        values.put(KEY_TRANSPORT_TYPE, route.getTransportType());

        // 3. updating row
        int i = db.update(TABLE_ROUTE, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(route.getID()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteRoute(Route route) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete routes
        db.delete(TABLE_ROUTE, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(route.getID()) }); //selections args

        //delete trips associated with routes
        db.delete(TABLE_TRIP, //table name
                KEY_ROUTEID+" = ?",  // selections
                new String[] { String.valueOf(route.getID()) }); //selections args

        // 3. close
        db.close();


    }



    public int addJourney(Journey journey){


        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_JOURNEY, COLUMNS_JOURNEY,"origin=?", new String[] {journey.getOrigin()},  null,null,null);

        Cursor cursor2 = db.query(TABLE_JOURNEY, COLUMNS_JOURNEY,"destination=?", new String[] {journey.getDestination()},  null,null,null);

        boolean empty = cursor.getCount() <= 0 && cursor != null && cursor2 != null && cursor2.getCount() <= 0;
        if(empty){

            // 2. create ContentValues to add key "column"/value
            ContentValues values = new ContentValues();
            values.put(KEY_ORIGIN, journey.getOrigin());
            values.put(KEY_DESTINATION, journey.getDestination()); // get title

            // 3. insert
            db.insert(TABLE_JOURNEY, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values

            // 4. close
            db.close();

            return 0;
        }
        else{

            return -1;
        }




    }



    public Journey getJourney(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_JOURNEY, // a. table
                        COLUMNS_JOURNEY, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();


        // 4. build book object
        Journey journey = new Journey();
        journey.setID(Integer.parseInt(cursor.getString(0)));
        journey.setOrigin(cursor.getString(1));
        journey.setDestination(cursor.getString(2));


        return journey;
    }


    public ArrayList<Journey> getAllJourneys() {
        ArrayList<Journey> journeys = new ArrayList<Journey>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_JOURNEY;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        //Trip trip = null;

        if (cursor.moveToFirst()) {
            do {
                Journey journey = new Journey();
                journey.setID(Integer.parseInt(cursor.getString(0)));
                journey.setOrigin(cursor.getString(1));
                journey.setDestination(cursor.getString(2));

                // Add book to books
                journeys.add(journey);
            } while (cursor.moveToNext());
        }



        return journeys;
    }

    public int updateJourney(Journey journey) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ORIGIN, journey.getOrigin());
        values.put(KEY_DESTINATION, journey.getDestination());

        // 3. updating row
        int i = db.update(TABLE_JOURNEY, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(journey.getID()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deleteJourney(Journey journey) {
        // Open db
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete journey
        db.delete(TABLE_JOURNEY, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(journey.getID()) }); //selections args



        ArrayList<String> routes = new ArrayList<String>(); //array for routeIDs to delete from trip

        Cursor cursor = db.query(TABLE_ROUTE, COLUMNS_ROUTE,"journeyID=?", new String[] {Integer.toString(journey.getID())},  null,null,null);

        if (cursor.moveToFirst()) {
            do {
                routes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }


        if(!routes.isEmpty()){

            db.delete(TABLE_ROUTE, //table name
                    KEY_JOURNEYID+" = ?",  // selections
                    new String[] { String.valueOf(journey.getID()) }); //selections args



            for(int i = 0; i < routes.size(); i++){

                String currentRoute = routes.get(i);

                db.delete(TABLE_TRIP, //table name
                        KEY_ROUTEID+" = ?",  // selections
                        new String[] { currentRoute }); //selections args

            }
        }

        // 3. close
        db.close();

    }

}
