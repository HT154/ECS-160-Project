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
import android.widget.TextView;

/**
 * An activity representing a single Parcel detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ParcelListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ParcelDetailFragment}.
 */
public class ParcelDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ParcelDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ParcelDetailFragment.ARG_ITEM_ID));
            ParcelDetailFragment fragment = new ParcelDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.parcel_detail_container, fragment)
                    .commit();
        }
    }



    /*Added by Andrew
    Parcel details is pretty simple right now. Just shows the parcel view and that's about it.
    */
    TextView description;
    TextView source;
    TextView destination;
    TextView carrier;
    TextView place;
    TextView time;
    Button finalize_button;

    int pID; //parcel ID
    int mUID; //my user ID

    @Override
    public void onStart(){
        super.onStart();

        //Setup Getters
        description = (TextView) findViewById(R.id.TextParcelDescription);
        source = (TextView) findViewById(R.id.TextParcelSource);
        destination = (TextView) findViewById(R.id.TextParcelDestination);
        carrier = (TextView) findViewById(R.id.TextParcelCarrier);
        place = (TextView) findViewById(R.id.TextParcelPlace);
        time = (TextView) findViewById(R.id.TextParcelTime);
        finalize_button = (Button) findViewById(R.id.ButtonParcelFinalize);

        mUID = getIntent().getIntExtra("uid", -1);
        pID = getIntent().getIntExtra("item_id", -1);

        //Initialize buttons
        finalize_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        API.finalize(pID, mUID);
                    }
                }
        );

        //Get parcel information
        //API.parcel(this, "ParcelCallback", pID);
    }

    public void ParcelCallback(Object ret) throws JSONException{
        JSONObject my_parcel = ((JSONArray) ret).getJSONObject(0);
        description.setText(my_parcel.getString("description"));
        source.setText(my_parcel.getString("source"));
        destination.setText(my_parcel.getString("destination"));
        carrier.setText(my_parcel.getString("carrier"));
        //place.setText(my_parcel.getString("lat")); //TODO: figure out what you want to store here (just lat/lng?)
        time.setText(my_parcel.getString("time"));
    }
}
