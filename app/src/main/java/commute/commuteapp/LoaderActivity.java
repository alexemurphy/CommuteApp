package commute.commuteapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;


public class LoaderActivity extends AppCompatActivity {

    //Database Access
    SaveTripClass dbAccess = new SaveTripClass();

    //Journey Table
    TableLayout journeyTable;
    boolean journeyTableEdit = false;
    ArrayList<ArrayList<String>> allJourneys;
    ArrayList<String> selectedJourney;

    //Route Table
    TableLayout routeTable;
    boolean routeTableEdit = false;
    ArrayList<ArrayList<String>> allRoutes;
    ArrayList<String> selectedRoute;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialise the journey GUI
        journeyInit();
    }

    /**
     * Toggles the edit button on every row of the table
     *
     * @param inputTable: The table being worked on
     * @return : the table being returned
     */
    private TableLayout toggleEditButtons(TableLayout inputTable, boolean enabled){
        View child;
        TableRow row;
        for (int i = 0; i < inputTable.getChildCount(); i++) {
            child = inputTable.getChildAt(i);

            if (child instanceof TableRow) {
                row = (TableRow) child;

                for (int x = 0; x < row.getChildCount(); x++) {

                    //Check whether the child is the delete button
                    boolean isDelBtn = false;
                    //Check if the tag of the element includes the edit tag
                    try {isDelBtn = ((String)row.getChildAt(x).getTag()).contains("btnEdit");}catch(Exception e){}
                    if (isDelBtn) {
                        if(enabled) {
                            row.getChildAt(x).setVisibility(View.GONE);
                        }
                        else{
                            row.getChildAt(x).setVisibility(View.VISIBLE);
                        }
                    }
                }

            }
        }
        return inputTable;
    }

    //----------------------------------------Journey GUI----------------------------------------

    /**
     * Initialise the GUI for journey loading
     */
    private void journeyInit(){
        //Set the layout to be the load journey GUI
        setContentView(R.layout.activity_loadjourney);


        journeyTable = findViewById(R.id.journeyTable);

        //Back Button
        Button back = findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                returnToMain();
            }
        });

        //Edit Button
        Button edit = findViewById(R.id.editButton);
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                journeyTable = toggleEditButtons(journeyTable, journeyTableEdit);
                journeyTableEdit = !journeyTableEdit;
            }
        });

        //Get All Journeys [[ID, Origin, Destination], ...]
        //TODO For some reason this doesn't work, but it does for TrackRouteActivity which does basically the same thing??!?!?!?!
        allJourneys = dbAccess.getJourneyList();

        Log.d("All Journeys", "Starting");

        for(int i = 0; i < allJourneys.size(); i++){
            Log.d("All Journeys", allJourneys.get(i).get(0) + " -> " + allJourneys.get(i).get(2));
            addNewJourney(allJourneys.get(i));
        }
        Log.d("All Journeys", "DONE");
    }


    /**
     * Add a new journey to the table
     *
     * @param journeyToAdd : The Journey to add to the table as an array
     */
    private void addNewJourney(ArrayList<String> journeyToAdd){
        TableRow row = new TableRow(this);

        //Delete Button
        Button btn = new Button(this);
        //Add the tag & add the ID to the end to get when the button is pressed
        btn.setTag("btnEdit" + journeyToAdd.get(0));
        btn.setText("Edit");
        btn.setBackgroundColor(Color.parseColor("#3582ff"));
        btn.setLayoutParams(new TableRow.LayoutParams(200, 150, 1));
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                editJourney(view.getTag().toString().replace("btnEdit", ""));
            }
        });
        btn.setVisibility(View.GONE);
        row.addView(btn);

        //Text
        TextView journeyName = new TextView(this);
        journeyName.setTag("txtName");
        journeyName.setText(journeyToAdd.get(1) + " -> " + journeyToAdd.get(2));
        journeyName.setLayoutParams(new TableRow.LayoutParams(150, 150, 10));
        journeyName.setTextColor(Color.parseColor("#000000"));
        journeyName.setGravity(Gravity.CENTER);
        row.addView(journeyName);

        //View Button
        Button btnView = new Button(this);
        btnView.setTag("btnView" + journeyToAdd.get(0));
        btnView.setText("View");
        btnView.setLayoutParams(new TableRow.LayoutParams(200, 150, 1));
        btnView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Get the ID of the selected journey from the tag & send it to the route
                routeInit(view.getTag().toString().replace("btnView", ""));
            }
        });
        row.addView(btnView);

        journeyTable.addView(row);

    }

    /**
     * Method to return focus to the main activity
     */
    private void returnToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Gets the journey data based on the ID
     *
     * @param ID : The ID of the journey
     * @return : The ArrayList<String> of the journey
     */
    private ArrayList<String> getIndexFromJourneyID(String ID){
        for(int i = 0; i < allJourneys.size(); i++){
            if(allJourneys.get(i).get(0).equals(ID)){
                return allJourneys.get(i);
            }
        }
        return null;
    }

    /**
     * Show the GUI for editing a journey
     *
     * @param journeyID : The ID of the journey to edit
     */
    private void editJourney(String journeyID){
        //Get the journey
        selectedJourney = getIndexFromJourneyID(journeyID);

        //Set the layout to be the edit journey menu
        setContentView(R.layout.activity_editjourney);

        //Set the original values
        ((EditText)findViewById(R.id.originName2)).setText(selectedJourney.get(1));
        ((EditText)findViewById(R.id.destinationName2)).setText(selectedJourney.get(2));

        //Back button
        Button back = findViewById(R.id.btnCancel2);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                journeyInit();
            }
        });

        //Delete button
        Button delete = findViewById(R.id.btnDelete);
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Delete the journey
                dbAccess.deleteJourney(selectedJourney.get(0));
                //Return to the menu
                journeyInit();
            }
        });

        //Save button
        Button save = findViewById(R.id.btnSaveJourney2);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Save the journey
                dbAccess.saveJourney(selectedJourney);
                //Return to the menu
                journeyInit();
            }
        });
    }

    //----------------------------------------Route GUI----------------------------------------
    private void routeInit(String journeyID){
        Log.d("Route Init", journeyID);
        //Set the layout to be the load route GUI
        setContentView(R.layout.activity_loadroute);

        routeTable = findViewById(R.id.routeTable);

        //Back Button
        Button back = findViewById(R.id.backButtonRoute);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                returnToMain();
            }
        });

        //Edit Button
        Button edit = findViewById(R.id.editButtonRoute);
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                routeTable = toggleEditButtons(routeTable, routeTableEdit);
                routeTableEdit = !routeTableEdit;
            }
        });

        //Get All Journeys [[ID, Origin, Destination], ...]
        allRoutes = dbAccess.getRouteList(journeyID);

        //TODO REMOVE THIS
        ArrayList<String> f = new ArrayList<>();
        f.add("1");f.add("Home");f.add("Work");
        allRoutes.add(f);
        //TODO REMOVE END
        for(int i = 0; i < allRoutes.size(); i++){
            addNewRoute(allRoutes.get(i));
        }
    }

    /**
     * Add a new route to the table
     *
     * @param routeToAdd : The route to add to the table as an array
     */
    private void addNewRoute(ArrayList<String> routeToAdd){
        TableRow row = new TableRow(this);

        //Delete Button
        Button btn = new Button(this);
        //Add the tag & add the ID to the end to get when the button is pressed
        btn.setTag("btnEdit" + routeToAdd.get(0));
        btn.setText("Edit");
        btn.setBackgroundColor(Color.parseColor("#3582ff"));
        btn.setLayoutParams(new TableRow.LayoutParams(200, 150, 1));
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                editRoute(view.getTag().toString().replace("btnEdit", ""));
            }
        });
        btn.setVisibility(View.GONE);
        row.addView(btn);

        //Text
        TextView routeName = new TextView(this);
        routeName.setTag("txtName");
        routeName.setText(routeToAdd.get(1) + " -> " + routeToAdd.get(2));
        routeName.setLayoutParams(new TableRow.LayoutParams(150, 150, 10));
        routeName.setTextColor(Color.parseColor("#000000"));
        routeName.setGravity(Gravity.CENTER);
        row.addView(routeName);

        //View Button
        Button btnView = new Button(this);
        btnView.setTag("btnView" + routeToAdd.get(0));
        btnView.setText("View");
        btnView.setLayoutParams(new TableRow.LayoutParams(200, 150, 1));
        btnView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Get the ID of the selected journey from the tag & send it to the route
                tripInit(view.getTag().toString().replace("btnView", ""));
            }
        });
        row.addView(btnView);

        journeyTable.addView(row);

    }

    private void editRoute(String h){}

    //----------------------------------------Route GUI----------------------------------------
    private void tripInit(String h){}

}
