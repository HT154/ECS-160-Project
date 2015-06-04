package ecs160.deliveries;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ParcelDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /*Added by Andrew
    Parcel right now covers everything except creating parcels
    Gives UI for editing and viewing parcel information on a specific item basis
    */
    EditText description_edit;
    EditText source_edit;
    EditText destination_edit;
    EditText carrier_edit;
    EditText location_edit;
    EditText time_edit;
    EditText date_edit;
    Button finalize_button;
    Button send_request_button;
    Button accept_request_button;
    Button decline_request_button;

    int pID; //parcel ID
    int mUID; //my user ID

    @Override
    public void onStart(){
        super.onStart();

        //TODO: Get an intent that returns whether or not we are creating a new parcel or just editing/viewing an old one
        boolean is_creating_parcel = false;

        //Setup Getters
        description_edit = (EditText) findViewById(R.id.EditParcelDescription);
        source_edit = (EditText) findViewById(R.id.EditParcelSource);
        destination_edit = (EditText) findViewById(R.id.EditParcelDestination);
        carrier_edit = (EditText) findViewById(R.id.EditParcelCarrier);
        location_edit = (EditText) findViewById(R.id.EditParcelLocation);
        time_edit = (EditText) findViewById(R.id.EditParcelTime);
        date_edit = (EditText) findViewById(R.id.EditParcelDate);

        finalize_button = (Button) findViewById(R.id.ButtonParcelFinalize);
        send_request_button = (Button) findViewById(R.id.ButtonParcelSendRequest);
        accept_request_button = (Button) findViewById(R.id.ButtonParcelAcceptRequest);
        decline_request_button = (Button) findViewById(R.id.ButtonParcelDeclineRequest);

        mUID = getIntent().getIntExtra("uid", -1);
        pID = getIntent().getIntExtra("item_id", -1);

        //Initialize buttons
        finalize_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestFinalize();
                    }
                }
        );

        send_request_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestSend();
                    }
                }
        );

        accept_request_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestResponse(true);
                    }
                }
        );

        decline_request_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestResponse(false);
                    }
                }
        );

        //Get parcel information
        API.parcel(this, "parcelCallback", pID);
    }

    //Callback for the API, returns JSON for parcel
    //Example JSON: [{"id":"1","description":"test1","status":"0","source":"20","destination":"24","courier":"0","carrier":"20","lat":"0","lng":"0","time":"0"}]
    public void parcelCallback(Object ret) throws JSONException{
        //Grab parcel and set the values for all the text edits
        //TODO: probably have to change the way we set information once fancy widgets are in
        JSONObject my_parcel = ((JSONArray) ret).getJSONObject(0);
        description_edit.setText("Description: " + my_parcel.getString("description"));
        source_edit.setText("Source: " + my_parcel.getString("source"));
        destination_edit.setText("Destination: " + my_parcel.getString("destination"));
        carrier_edit.setText("Carrier: " + my_parcel.getString("carrier"));
        location_edit.setText("Lat:" + my_parcel.getString("lat") + "Lng: " + my_parcel.getString("lng"));
        time_edit.setText("Time: " + my_parcel.getString("time"));
        //date_edit.setText("Time: " + my_parcel.getString("time")); //TODO: fill this in once we've got the date conversion working

        //Setup UI based on status
        String p_status = my_parcel.getString("status");
        switch(p_status)
        {
            //0 and 3 are the same
            case "0": //TODO: Make sure that this works, I assume that Java doesn't stop until a break
            case "3":
                //TODO: Replace these with "fancy" widgets for getting time/date/location
                //Set enabled is to enable/disable editing of them by the user
                time_edit.setEnabled(true);
                date_edit.setEnabled(true);
                location_edit.setEnabled(true);
                send_request_button.setVisibility(View.VISIBLE);
                break;
            case "1":
                accept_request_button.setVisibility(View.VISIBLE);
                decline_request_button.setVisibility(View.VISIBLE);
                break;
            case "2":
                finalize_button.setVisibility(View.VISIBLE);
                break;
        }
    }

    //Called when the user wants to finalize a request
    public void requestFinalize() {
        API.finalize(pID, mUID);
        finalize_button.setEnabled(false); //TODO: Visibility.GONE or just disabled when the buttons are clicked?
    }

    //Called when the user wants to send a new request to another user
    public void requestSend() {
        API.rendezvousRequest(pID, 0, 0, 0); //TODO: Put in the values to this function call once the fancy widgets are implemented
        time_edit.setEnabled(false);
        date_edit.setEnabled(false);
        location_edit.setEnabled(false);
        send_request_button.setEnabled(false);
    }

    //Called when the user wants to respond to another users request
    public void requestResponse(boolean is_accept){
        accept_request_button.setEnabled(false);
        decline_request_button.setEnabled(false);
    }
}
