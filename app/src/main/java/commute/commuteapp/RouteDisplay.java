package commute.commuteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Tom on 26/02/2018.
 */

public class RouteDisplay extends AppCompatActivity {
    ArrayList<ArrayList<String>> journeys;
    SaveTripActivity saveTrip = new SaveTripActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    //----------------------------------------Journeys Display----------------------------------------//

    /**
     * Initialise the GUI
     */
    private void init(){
        //Show the journeys GUI
        setContentView(R.layout.activity_showjourneys);
        setJourneys();

        //Setup set journey button
        final Button loadJourney = findViewById(R.id.loadJourneyButton);
        loadJourney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Set the routes compared to the journey
                String ID = getIDFromName(journeys, ((Spinner)findViewById(R.id.routeDropdownLoad)).getSelectedItem().toString());
                displayRoutes(ID);
            }
        });

        //Setup back button
        final Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Change to main menu
                Intent intent = new Intent(RouteDisplay.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Fill the journey dropdown menu with every journey in the database
     */
    private void setJourneys(){
        journeys = saveTrip.getJourneyList();

        //Get all of the strings of the journey
        ArrayList<String> journeyStrings = new ArrayList<String>();
        for(int i = 0; i < journeys.size(); i++){
            journeyStrings.add(journeys.get(i).get(1));
        }

        //Add all elements to the list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, journeyStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) findViewById(R.id.routeDropdown)).setAdapter(adapter);
    }

    /**
     * Gets the ID from the String List using the name presented to the user
     *
     * @param mainList : The list that stores the IDs and the names
     * @param target : The String of the name
     * @return  : The ID or null if the name cannot be found
     */
    private String getIDFromName(ArrayList<ArrayList<String>> mainList, String target){
        for(int i = 0; i < mainList.size(); i++){
            if(mainList.get(i).get(1).equals(target)){
                return mainList.get(i).get(1);
            }
        }
        return null;
    }

    //----------------------------------------Routes Display----------------------------------------//
    private void displayRoutes(String journeyID){
        setContentView(R.layout.activity_showroutesmenu);
    }
}
