package ecs160.deliveries;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.widget.Button;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import android.view.MenuItem;

import java.util.Dictionary;

/**
 * An activity representing a single User detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link UserListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link UserDetailFragment}.
 */
public class UserDetailActivity extends ActionBarActivity {
    //Some simple getters to facilitate grabbing the UI elements without always having to call get ID
    Button negative_feedback;
    Button positive_feedback;
    Button decline_request;
    Button accept_request;
    TextView feedback_score;
    TextView text_name;
    int feedback_int_value;

    int mUID;
    int vUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

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
            arguments.putString(UserDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(UserDetailFragment.ARG_ITEM_ID));
            UserDetailFragment fragment = new UserDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.user_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*Added by Andrew
        Wires up the buttons so that they have the correct functions
        Sets the name and feedback score from the get-go
        Disables feedback if the user's state doesn't permit
        Disables accept/decline request if the user's state doesn't permit
        */
        negative_feedback = (Button) findViewById(R.id.ButtonRateDown);
        positive_feedback = (Button) findViewById(R.id.ButtonRateUp);
        decline_request = (Button) findViewById(R.id.ButtonDeclineFriendRequest);
        accept_request = (Button) findViewById(R.id.ButtonAcceptFriendRequest);
        feedback_score = (TextView) findViewById(R.id.TextFeedbackScore);
        text_name = (TextView) findViewById(R.id.TextUserName);

        mUID = getIntent().getIntExtra("uid", -1);
        vUID = getIntent().getIntExtra("item_id", -1); //TODO: As is this returns the ID for the transaction, we need the ID for the user 2 passed in somehow

        //Call users to setup initial state
        API.user(this, "UserCallback", mUID);

        //Setup wires
        negative_feedback.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Feedback(false);
                    }
                }
        );

        positive_feedback.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Feedback(true);
                    }
                }
        );

        decline_request.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Friend(false);
                    }
                }
        );

        accept_request.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Friend(true);
                    }
                }
        );

        //Enable buttons based on state
        //Don't enable buttons before confirmation
        API.ratable(this, "RatableCallback", mUID, vUID);
        API.friends(this, "CheckFriendCallback", mUID);
    }

    //Returned by API call, sets up some initial values
    public void UserCallback(Object res) throws JSONException{
        JSONObject user = ((JSONArray) res).getJSONObject(0);
        text_name.setText(user.getString("name"));
        feedback_int_value = user.getInt("rating");
        feedback_score.setText(feedback_int_value + "");
    }

    //Check the list to set the initial state of accept/reject friend request
    public void CheckFriendCallback(Object res) throws JSONException {
        JSONArray arr = (JSONArray) ((JSONArray) res).get(0);
        for(int i=0; i<arr.length(); i++) {
            if(arr.getJSONObject(i).getInt
                    ("uid2") == vUID) {
                decline_request.setEnabled(true);
                accept_request.setEnabled(true);
                break;
            }
        }
    }

    //A feedback button was clicked
    private void Feedback(boolean is_positive){
        if(is_positive) feedback_int_value++;
        else feedback_int_value--;

        API.rate(mUID, vUID, is_positive);
        API.ratable(this, "RatableCallback", mUID, vUID);

        //Immediately set to prevent user from pushing the button too much
        negative_feedback.setEnabled(false);
        positive_feedback.setEnabled(false);
    }

    public void RatableCallback(Object res) throws JSONException {
        int rate_count = (int) ((JSONArray) res).get(0); //TODO: Fix this
        negative_feedback.setEnabled(rate_count > 0);
        positive_feedback.setEnabled(rate_count > 0);
        feedback_score.setText(feedback_int_value + "");
    }

    //A friend button was clicked
    private void Friend(boolean is_accept) {
        API.friendResponse(mUID, vUID, is_accept);
        decline_request.setEnabled(false);
        accept_request.setEnabled(false);
    }
}
