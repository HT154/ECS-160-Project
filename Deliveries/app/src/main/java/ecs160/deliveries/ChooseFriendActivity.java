package ecs160.deliveries;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ChooseFriendActivity extends ListActivity {

    int mUID; //my user ID
    private ArrayAdapter adapter;
    JSONArray friends;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friend);

        inflater = LayoutInflater.from(this);

        friends = new JSONArray();

        adapter = new ArrayAdapter<JSONObject>(this, R.layout.list_item, R.id.list_item_text) {
            @Override
            public int getCount() {
                return friends.length();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;

                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.list_item, parent, false);
                    holder.text = (TextView) convertView.findViewById(R.id.list_item_text);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                try {
                    holder.text.setText(friends.getJSONObject(position).getString("name"));
                } catch (Exception e) {
                    System.out.println(e);
                }

                return convertView;
            }
        };

        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        try {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("chosenFriend", friends.getJSONObject(position).toString());
            setResult(154, resultIntent);
        } catch (JSONException e) {}

        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUID = getIntent().getIntExtra("uid", 20);
        API.friends(this, "friendsCallback", mUID);

    }

    public void friendsCallback(Object res) throws JSONException {
        JSONArray response = (JSONArray) res;
        friends = response.getJSONArray(1);
        System.out.println(friends);
        adapter.notifyDataSetChanged();
    }

    class ViewHolder {
        TextView text;
    }
}
