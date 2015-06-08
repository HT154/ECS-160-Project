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

public class UserDetailActivity extends ActionBarActivity {
    //Some simple getters to facilitate grabbing the UI elements without always having to call get ID
    Button negative_feedback;
    Button positive_feedback;
    Button decline_request;
    Button accept_request;
    TextView feedback_score;
    TextView text_name;
    TextView text_status;
    int feedback_int_value;

    int mUID;
    int vUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
        text_status = (TextView) findViewById(R.id.TextFriendStatus);

        negative_feedback.setVisibility(View.GONE);
        positive_feedback.setVisibility(View.GONE);

        mUID = getIntent().getIntExtra("uid", -1);
        vUID = getIntent().getIntExtra("target", -1);

        //Call users to setup initial state
        API.user(this, "userCallback", vUID, mUID);

        //Setup wires
        negative_feedback.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feedback(false);
                    }
                }
        );

        positive_feedback.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feedback(true);
                    }
                }
        );

        decline_request.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        friend(false);
                    }
                }
        );

        accept_request.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        friend(true);
                    }
                }
        );

        //Enable buttons based on state
        //Don't enable buttons before confirmation
        API.ratable(this, "ratableCallback", mUID, vUID);
    }

    //Returned by API call, sets up some initial values
    public void userCallback(Object res) throws JSONException{
        JSONArray response = (JSONArray) res;

        if (response.length() > 0) {
            JSONObject user = response.getJSONObject(0);

            text_name.setText(user.getString("name"));
            feedback_int_value = Integer.parseInt(user.getString("rating"));
            feedback_score.setText(feedback_int_value + "");

            int friendStatus = Integer.parseInt(user.getString("status"));
            System.out.println("!!!");
            System.out.println(friendStatus);
            if (friendStatus == 2) {
                text_status.setText("Friend Request Received");
                decline_request.setVisibility(View.VISIBLE);
                accept_request.setVisibility(View.VISIBLE);
            } else if (friendStatus == 1) {
                text_status.setText("Friend Request Sent");
            } else {
                text_status.setText("Confirmed Friend");
            }
        }
    }

    //A feedback button was clicked
    private void feedback(boolean is_positive){
        if(is_positive) feedback_int_value++;
        else feedback_int_value--;

        API.rate(mUID, vUID, is_positive);
        API.ratable(this, "ratableCallback", mUID, vUID);

        //Immediately set to prevent user from pushing the button too much
        negative_feedback.setEnabled(false);
        positive_feedback.setEnabled(false);
    }

    public void ratableCallback(Object res) throws JSONException {
        JSONArray response = (JSONArray) res;
        int rate_count = response.getInt(0);
        if (rate_count > 0) {
            negative_feedback.setEnabled(true);
            positive_feedback.setEnabled(true);
            negative_feedback.setVisibility(View.VISIBLE);
            positive_feedback.setVisibility(View.VISIBLE);
        }

        feedback_score.setText(feedback_int_value + "");
    }

    //A friend button was clicked
    private void friend(boolean is_accept) {
        API.friendResponse(mUID, vUID, is_accept);
        decline_request.setEnabled(false);
        accept_request.setEnabled(false);
    }
}
