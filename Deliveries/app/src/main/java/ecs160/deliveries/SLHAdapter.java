package ecs160.deliveries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SLHAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private JSONArray arrayOfSections;
    private String[] sectionNames;
    private String mKey;
    private ArrayList<Integer> cumulativeCounts = new ArrayList<Integer>();
    private int totalCount = 0;

    private LayoutInflater inflater;

    public SLHAdapter(Context context, int uid, JSONArray sections, String[] names, String key) {
        inflater = LayoutInflater.from(context);
        mKey = key;
        sectionNames = names;
        setSections(sections);
    }

    public void setSections(JSONArray sections) {
        arrayOfSections = sections;
        cumulativeCounts.clear();

        for (int i = 0; i < arrayOfSections.length(); i++) {
            try {
                int sCount = arrayOfSections.getJSONArray(i).length();
                totalCount += sCount;
                cumulativeCounts.add(totalCount);
            } catch (JSONException e) {}
        }
    }

    @Override
    public int getCount() {
        return totalCount;
    }

    @Override
    public Object getItem(int position) {
        int i = 0;
        try {
            for (; cumulativeCounts.get(i) <= position; i++);

            int offset = 0;
            if (i > 0) {
                offset = cumulativeCounts.get(i - 1);
            }

            return arrayOfSections.getJSONArray(i).getJSONObject(position - offset);
        } catch (Exception e) {
            System.err.println(e);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        try {
            System.out.println(getItem(position));
            return Long.parseLong(((JSONObject)getItem(position)).getString("id"));
        } catch (Exception e) {
            System.out.println(e);
        }

        return 0;
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
            holder.text.setText(((JSONObject)getItem(position)).getString(mKey));
        } catch (Exception e) {
            holder.text.setText(mKey);
            System.out.println(e);
        }

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.list_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.list_header_text);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String headerText = sectionNames[(int)getHeaderId(position)];
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        int i = 0;
        for (; cumulativeCounts.get(i) <= position; i++);
        return i;
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text;
    }

}
