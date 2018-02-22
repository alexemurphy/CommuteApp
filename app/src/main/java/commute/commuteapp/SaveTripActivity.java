package commute.commuteapp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Class for saving the route
 */

public class SaveTripActivity extends AppCompatActivity {
    Route routeToSave;

    /**
     * Constructor
     * @param inputRoute : The route to be input
     */
    public SaveTripActivity(Route inputRoute){
        routeToSave = inputRoute;

        Log.d("Save Route: ", "Done");
    }

    /**
     * Initialise the menu
     */
    public void init(){
        Log.d("Save Route: ", "Initialise Button");
        final Button save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveRoute();
            }
        });
    }

    public void saveRoute(){
        getData();

        //TODO Save the data from Route class
    }

    private void getData(){
        Log.d("Save Route: ", "Get transport information");
        //Save transport method
        routeToSave.setTransportMethod(((EditText)findViewById(R.id.transportMethodInput)).getText().toString());
        Log.d("Save Route: ", "Transport info got");
    }


}
