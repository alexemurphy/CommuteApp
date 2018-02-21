package commute.commuteapp;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Class for saving the route
 */

public class SaveRouteActivity extends AppCompatActivity {
    Route routeToSave;

    /**
     * Constructor
     * @param inputRoute : The route to be input
     */
    public SaveRouteActivity(Route inputRoute){
        routeToSave = inputRoute;

        //Display the save screen
        setContentView(R.layout.activity_trackmap);

        //Initialise the menu
        init();
    }

    /**
     * Initialise the menu
     */
    public void init(){
        final Button save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveRoute();
            }
        });
    }

    private void saveRoute(){
        getData();

        //TODO Save the data from Route class
    }

    private void getData(){
        //Save transport method
        routeToSave.setTransportMethod(((EditText)findViewById(R.id.transportMethodInput)).getText().toString());
        //Save description
        routeToSave.setDescription(((EditText)findViewById(R.id.descriptionInput)).getText().toString());
    }


}
