package ecs160.deliveries;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;


public class AddFriendActivity extends ActionBarActivity {

    private int mUID;
    private EditText mUsernameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mUsernameView = (EditText) findViewById(R.id.add_friend_username);

        Button requestButton = (Button) findViewById(R.id.add_friend_request);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAddFriend();
            }
        });

        mUID = getIntent().getIntExtra("uid", 0);
    }

    public void attemptAddFriend() {
        API.addFriend(this, "addFriendCallback", mUID, mUsernameView.getText().toString());
    }

    public void addFriendCallback(Object res) throws JSONException {
        JSONArray response = (JSONArray) res;
        System.out.println("***");
        System.out.println(response);
        if (response.length() > 0) {
            //error
            new AlertDialog.Builder(AddFriendActivity.this).setMessage(response.getString(0)).setTitle("Error").setNeutralButton("OK", new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) {}}).show();
        } else {
            finish();
        }
    }
}
