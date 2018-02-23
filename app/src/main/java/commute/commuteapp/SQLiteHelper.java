package commute.commuteapp;


/**
 * Created by alexmurphy on 23/02/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper  extends SQLiteOpenHelper{


    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "CommuteDB";



    // Journey table name
    private static final String TABLE_JOURNEY = "journey";
    private static final String TABLE_TRIP = "trip";
    private static final String TABLE_ROUTE = "route";

    // Journey Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_DESTINATION = "destination";
    private static final String KEY_JOURNEYID = "journeyID";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_TRANSPORT_TYPE = "transportType";
    private static final String KEY_ROUTEID = "routeID";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_LATLANGFILE = "LatLangFile";
    private static final String KEY_TIMETAKEN = "timetaken";

    private static final String[] COLUMNS_JOURNEY = {KEY_ID,KEY_NAME,KEY_ORIGIN,KEY_DESTINATION};

    private static final String[] COLUMNS_ROUTE = {KEY_ID,KEY_JOURNEYID,KEY_DISTANCE,KEY_TRANSPORT_TYPE};

    private static final String[] COLUMNS_TRIP = {KEY_ID,KEY_ROUTEID,KEY_TIMESTAMP,KEY_LATLANGFILE, KEY_TIMETAKEN};


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table


        String CREATE_JOURNEY_TABLE = "CREATE TABLE journey ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, "+
                "origin TEXT, "+
                "destination TEXT )";

        String CREATE_ROUTE_TABLE = "CREATE TABLE route ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
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


        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TIMESTAMP, trip.getStartTime()); // get title
        values.put(KEY_TIMETAKEN, trip.getElapsedTime());
        values.put(KEY_ROUTEID, trip.getRouteID());
        values.put(KEY_LATLANGFILE, trip.getLatLangFileLocation());// get author

        // 3. insert
        db.insert(TABLE_BOOKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }



}
