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
    ArrayList<Journey> journeyList = new ArrayList<>();

    //Database Access
    SaveTripClass dbAccess;

    //Journey Table
    TableLayout journeyTable;
    boolean journeyTableEdit = false;
    ArrayList<ArrayList<String>> allJourneys;
    ArrayList<String> selectedJourney;

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
        Button edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                journeyTable = toggleEditButtons(journeyTable, journeyTableEdit);
                journeyTableEdit = !journeyTableEdit;
            }
        });

        //Get All Journeys [[ID, Origin, Destination], ...]
        allJourneys = dbAccess.getJourneyList();


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

        //Back button
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

        //Back button
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
    }
}
