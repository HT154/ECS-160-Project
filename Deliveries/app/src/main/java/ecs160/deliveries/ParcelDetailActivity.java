package ecs160.deliveries;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Console;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ParcelDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /*
    Parcel right now covers everything except lat/lng
    Gives UI for editing and viewing parcel information on a specific item basis
    */
    EditText description_edit;
    TextView source_text;
    EditText source_edit;
    EditText destination_edit;
    TextView courier_text;
    EditText courier_edit;
    CheckBox courier_checkbox;
    TextView carrier_text;
    EditText carrier_edit;
    EditText lat_edit;
    EditText lng_edit;

    Button destination_button;
    Button time_button;
    Button date_button;
    Button finalize_button;
    Button send_request_button;
    Button accept_request_button;
    Button decline_request_button;
    Button create_parcel_button;

    int pID; //parcel ID
    int mUID; //my user ID
    boolean is_creating_parcel;
    JSONArray confirmed_friends;

    int year, month, day, minute, hour, destID;

    @Override
    public void onStart() {
        super.onStart();

        //Setup Getters
        description_edit = (EditText) findViewById(R.id.EditParcelDescription);
        source_text = (TextView) findViewById(R.id.TextParcelSource);
        source_edit = (EditText) findViewById(R.id.EditParcelSource);
        destination_edit = (EditText) findViewById(R.id.EditParcelDestination);
        courier_text = (TextView) findViewById(R.id.TextParcelCourier);
        courier_edit = (EditText) findViewById(R.id.EditParcelCourier);
        courier_checkbox = (CheckBox) findViewById(R.id.CheckboxParcelCourier);
        carrier_text = (TextView) findViewById(R.id.TextParcelCarrier);
        carrier_edit = (EditText) findViewById(R.id.EditParcelCarrier);
        lat_edit = (EditText) findViewById(R.id.EditParcelLatitude);
        lng_edit = (EditText) findViewById(R.id.EditParcelLongitude);

        time_button = (Button) findViewById(R.id.ButtonParcelTime);
        date_button = (Button) findViewById(R.id.ButtonParcelDate);
        finalize_button = (Button) findViewById(R.id.ButtonParcelFinalize);
        send_request_button = (Button) findViewById(R.id.ButtonParcelSendRequest);
        accept_request_button = (Button) findViewById(R.id.ButtonParcelAcceptRequest);
        decline_request_button = (Button) findViewById(R.id.ButtonParcelDeclineRequest);
        create_parcel_button = (Button) findViewById(R.id.ButtonParcelCreateParcel);
        destination_button = (Button) findViewById(R.id.ButtonParcelSetDestination);

        //Setup intents
        mUID = getIntent().getIntExtra("uid", -1);
        pID = getIntent().getIntExtra("target", -1);
        is_creating_parcel = pID < 0;

        //Initialize buttons
        time_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeClicked();
                    }
                }
        );

        date_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dateClicked();
                    }
                }
        );

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

        create_parcel_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createParcel();
                    }
                }
        );

        destination_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destClicked();
            }
        });

        API.friends(this, "friendsCallback", mUID);
        if (is_creating_parcel) {
            create_parcel_button.setVisibility(View.VISIBLE);

            description_edit.setEnabled(true);
            source_text.setVisibility(View.GONE);
            source_edit.setVisibility(View.GONE);
            destination_edit.setVisibility(View.GONE);
            destination_button.setVisibility(View.VISIBLE);
            courier_text.setVisibility(View.VISIBLE);
            courier_checkbox.setVisibility(View.VISIBLE);
            carrier_text.setVisibility(View.GONE);
            carrier_edit.setVisibility(View.GONE);
            lat_edit.setEnabled(true);
            lng_edit.setEnabled(true);
            time_button.setEnabled(true);
            date_button.setEnabled(true);
        } else {
            //Get parcel information
            API.parcel(this, "parcelCallback", pID);
        }
    }

    //Callback for the API, returns JSON for parcel
    //Example JSON: [{"id":"1","description":"test1","status":"0","source":"20","destination":"24","courier":"0","carrier":"20","lat":"0","lng":"0","time":"0"}]
    JSONObject my_parcel;
    public void parcelCallback(Object ret) throws JSONException {
        my_parcel = ((JSONArray) ret).getJSONObject(0);
        callbackMain();
    }

    JSONArray all_friends;
    public void friendsCallback(Object ret) throws JSONException{
        all_friends = (JSONArray) ret;
        confirmed_friends = (JSONArray) all_friends.get(1);
        callbackMain();
    }

    public String getName(int uid)  {
        if(uid == mUID) return "Me";
        try {
            for (int i = 0; i < 3; i++) {
                JSONArray check_array = (JSONArray) all_friends.get(i);
                for(int j=0; j<check_array.length(); j++) {
                    JSONObject person = check_array.getJSONObject(j);
                    if(person.getInt("uid2") == uid)
                        return person.getString("name");
                }
            }
        } catch (Exception e){}

        Toast.makeText(getApplicationContext(), "Couldn't find a friend matching ID " + uid, Toast.LENGTH_SHORT).show();
        return "";
    }

    boolean is_called = false;
    public void callbackMain() throws JSONException{
        //Must get both the parcel and friends callback before running
        if(!is_called) {
            is_called = true;
            return;
        }

        boolean is_courier = my_parcel.getInt("courier") > 0;
        if(is_courier) {
            courier_text.setVisibility(View.VISIBLE);
            courier_edit.setVisibility(View.VISIBLE);
        }

        //Grab parcel and set the values for all the text edit
        description_edit.setText(my_parcel.getString("description"));
        source_edit.setText(my_parcel.getString("srcName"));
        destination_edit.setText(my_parcel.getString("destName"));
        if(is_courier)
            courier_edit.setText(my_parcel.getString("courName"));

        if (my_parcel.getInt("carrier") == my_parcel.getInt("source")) {
            carrier_edit.setText(my_parcel.getString("srcName"));
        } else if (my_parcel.getInt("carrier") == my_parcel.getInt("courier")) {
            carrier_edit.setText(my_parcel.getString("courName"));
        } else if (my_parcel.getInt("carrier") == my_parcel.getInt("destination")) {
            carrier_edit.setText(my_parcel.getString("destName"));
        }

        lat_edit.setText(my_parcel.getString("lat"));
        lng_edit.setText(my_parcel.getString("lng"));

        FromTime(my_parcel.getLong("time"));
        time_button.setText(String.valueOf(hour) + ":" + String.format("%02d", minute));
        date_button.setText(String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year));

        //Setup UI based on status
        int p_status = Integer.parseInt(my_parcel.getString("status"));
        switch (p_status) {
            //0 and 3 are the same
            case 0:
            case 3:
                //Set enabled is to enable/disable editing of them by the user
                time_button.setEnabled(true);
                date_button.setEnabled(true);
                lat_edit.setEnabled(true);
                lng_edit.setEnabled(true);
                send_request_button.setVisibility(View.VISIBLE);
                break;
            case 1:
                accept_request_button.setVisibility(View.VISIBLE);
                decline_request_button.setVisibility(View.VISIBLE);
                break;
            case 2:
                finalize_button.setVisibility(View.VISIBLE);
                break;
        }
    }

    //Sends signal to select time
    public void timeClicked() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        TimePickerDialog picker = new TimePickerDialog(this,
                timePickerListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true);
        picker.setCancelable(false);
        picker.setTitle("Select the time");
        picker.show();
    }

    //Called back after time is set
    public TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int set_hour, int set_minute) {
            hour = set_hour;
            minute = set_minute;
            time_button.setText(String.valueOf(hour) + ":" + String.format("%02d", minute));
        }
    };

    //Sends signal to select date
    public void dateClicked() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog picker = new DatePickerDialog(this,
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        picker.setCancelable(false);
        picker.show();
    }

    //Called back after date is set
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int set_year, int set_month, int set_day) {
            month = set_month;
            day = set_day;
            year = set_year;
            date_button.setText(String.valueOf(month + 1) + "/" + String.valueOf(day) + "/" + String.valueOf(year));
        }
    };

    public void destClicked() {
        Intent chooseFriendIntent = new Intent(this, ChooseFriendActivity.class);
        chooseFriendIntent.putExtra("uid", mUID);
        startActivityForResult(chooseFriendIntent, 154);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 154 && resultCode == 154) {
            try {
                JSONObject dest = new JSONObject(data.getStringExtra("chosenFriend"));
                destID = Integer.parseInt(dest.getString("id"));
                destination_button.setText(dest.getString("name"));
            } catch (Exception e) {}
        }
    }

    //Called when the user wants to finalize a request
    public void requestFinalize() {
        API.finalize(pID, mUID);
        finalize_button.setEnabled(false);
    }

    //Called when the user wants to send a new request to another user
    public void requestSend() {
        if (Validate()) {
            API.rendezvousRequest(pID, Double.parseDouble(lat_edit.getText().toString()), Double.parseDouble(lng_edit.getText().toString()), ToTime());
            time_button.setEnabled(false);
            date_button.setEnabled(false);
            lat_edit.setEnabled(false);
            lng_edit.setEnabled(false);
            send_request_button.setEnabled(false);
        }
    }

    //Called when the user wants to respond to another users request
    public void requestResponse(boolean is_accept) {
        API.rendezvousResponse(pID, is_accept);
        accept_request_button.setEnabled(false);
        decline_request_button.setEnabled(false);
    }

    //Called when the user is happy with their parcel they've created
    //Creates the parcel and creates a request at the same time
    public void createParcel() {
        if (Validate()) {
            create_parcel_button.setEnabled(false);
            double send_lat = Double.parseDouble(lat_edit.getText().toString());
            double send_lng = Double.parseDouble(lng_edit.getText().toString());
            API.addParcel(this, "createParcelCallback", mUID, destID, description_edit.getText().toString(),
                        send_lat, send_lng, ToTime(), courier_checkbox.isChecked());
        } else {
            Toast.makeText(getApplicationContext(), "Invalid time of Year:" + year + " Month:" + month + " Day:" + day + " Hour:" + hour + " Minute:" + minute,
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Callback for the parcel creation, makes sure that the parcel was created
    public void createParcelCallback(Object obj) {
        JSONArray arr = (JSONArray) obj;
        if(arr.length() > 0) {
            Toast.makeText(getApplicationContext(), "Could not find a valid courier for your package",
                    Toast.LENGTH_SHORT).show();
            create_parcel_button.setEnabled(true);
        }
        else {
            finish();
        }
    }

    public boolean Validate() //TODO: include any other values that may need validation (destination ID for example)
    {
        System.out.println(year);
        System.out.println(month);
        System.out.println(day);
        System.out.println(hour);
        System.out.println(minute);

        return (year > 0 && month > 0 && day > 0 && hour > 0 && minute > 0);
    }

    public long ToTime() {
        GregorianCalendar d = new GregorianCalendar(year, month, day, hour, 0);
        return d.getTimeInMillis() / 1000;
    }

    public void FromTime(long time_val) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time_val * 1000);
        minute = cal.get(Calendar.MINUTE);
        hour = cal.get(Calendar.HOUR);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
    }
}