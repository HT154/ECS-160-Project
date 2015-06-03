package ecs160.deliveries;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import org.json.JSONArray;

import ecs160.deliveries.dummy.DummyContent;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ParcelListFragment extends Fragment {

    private int mUID;
    private SLHAdapter adapter;
    private static String[] sectionTitles = {"Uninitiated Parcels", "Rendezvous Requested", "Rendezvous Accepted", "With Courier", "Completed"};
    private static String idKey = "id";
    private JSONArray parcels = new JSONArray();

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onParcelSelected(int id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onParcelSelected(int id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ParcelListFragment() {
    }

    public void setUID(int uid) {
        mUID = uid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh();
    }

    public void refresh() {
        API.parcels(this, "parcelsCallback", mUID);
    }

    public void parcelsCallback(Object res) {
        parcels = (JSONArray) res;
        adapter.setSections(parcels);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_list, container, false);

        StickyListHeadersListView stickyList = (StickyListHeadersListView) content.findViewById(R.id.slh_list);
        adapter = new SLHAdapter(this.getActivity(), mUID, parcels, sectionTitles, idKey, "description");
        stickyList.setAdapter(adapter);
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onParcelSelected((int) id);
            }
        });

        return content;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }
}
