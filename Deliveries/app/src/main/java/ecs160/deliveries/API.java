package ecs160.deliveries;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class API {

    public static void register(Object sender, String method, String username, String password, boolean courier) {
        API.get_instance()._register(sender, method, username, password, courier);
    } //returns 1-item array with integer id of new user
    public static void login(Object sender, String method, String username, String password) {
        API.get_instance()._login(sender, method, username, password);
    } //returns empty array on failure, 1-item array with integer id of user on success

    public static void addFriend(Object sender, String method, int uid, String name) {
        API.get_instance()._addFriend(sender, method, uid, name);
    } //returns empty array on success, 1-item array with string error message on failure
    public static void friends(Object sender, String method, int uid) {
        API.get_instance()._friends(sender, method, uid);
    } //returns dictionary: {'confirmed': array of confirmed friends, 'requestSent': array of outgoing friend requests, 'requestReceived': array of incoming friend requests}
    public static void friendResponse(int uid1, int uid2, boolean accept) {
        API.get_instance()._friendResponse(uid1, uid2, accept);
    }

    public static void parcels(Object sender, String method, int uid) {
        API.get_instance()._parcels(sender, method, uid);
    } //returns dictionary: {'rendezvousDeclined': array, 'rendezvousRequested': array, 'rendezvousAccepted': array,
        // 'inCourierPosession': array, 'completed': array}
    public static void addParcel(Object sender, String method, int uid, int destination, String description, double lat,
                                 double lng, int time, boolean courier) {
        API.get_instance()._addParcel(sender, method, uid, destination, description, lat, lng, time, courier);
    } //returns empty array on success, 1-item array with string error message on failure

    public static void ratable(Object sender, String method, int uid1, int uid2) {
        API.get_instance()._ratable(sender, method, uid1, uid2);
    } //returns 1-item array with integer number of times uid1 can rate uid2
    public static void rate(int uid1, int uid2, boolean up) {
        API.get_instance()._rate(uid1, uid2, up);
    }

    public static void rendezvousRequest(int pid, double lat, double lng, int time) {
        API.get_instance()._rendezvousRequest(pid, lat, lng, time);
    }
    public static void rendezvousResponse(int pid, boolean accept) {
        API.get_instance()._rendezvousResponse(pid, accept);
    }
    public static void finalize(int pid, int uid) {
        API.get_instance()._finalize(pid, uid);
    }

    public static void updateLocation(int uid, double lat, double lng) {
        API.get_instance()._updateLocation(uid, lat, lng);
    }









    private void _register(Object sender, String method, String username, String password, boolean courier) {
        Dictionary<String, String> args = new Hashtable<String, String>();
        args.put("name", username);
        args.put("pass", password);
        args.put("courier", Boolean.toString(courier));

        APITask task = new APITask(sender, method, "register", args);
        task.execute();
    }

    private void _login(Object sender, String method, String username, String password) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("name", username);
        args.put("pass", password);

        APITask task = new APITask(sender, method, "login", args);
        task.execute();
    }

    private void _addFriend(Object sender, String method, int uid, String name) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("uid", Integer.toString(uid));
        args.put("name", name);

        APITask task = new APITask(sender, method, "addfriend", args);
        task.execute();
    }

    private void _friends(Object sender, String method, int uid) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("uid", Integer.toString(uid));

        APITask task = new APITask(sender, method, "friends", args);
        task.execute();
    }

    private void _addParcel(Object sender, String method, int uid, int destination, String description, double lat,
                            double lng, int time, boolean courier) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("uid", Integer.toString(uid));
        args.put("destination", Integer.toString(destination));
        args.put("description", description);
        args.put("lat", Double.toString(lat));
        args.put("lng", Double.toString(lng));
        args.put("time", Integer.toString(time));
        args.put("courier", Boolean.toString(courier));

        APITask task = new APITask(sender, method, "addparcel", args);
        task.execute();
    }

    private void _parcels(Object sender, String method, int uid) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("uid", Integer.toString(uid));

        APITask task = new APITask(sender, method, "parcels", args);
        task.execute();
    }

    private void _ratable(Object sender, String method, int uid1, int uid2) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("uid1", Integer.toString(uid1));
        args.put("uid2", Integer.toString(uid2));

        APITask task = new APITask(sender, method, "ratable", args);
        task.execute();
    }

    private void _rate(int uid1, int uid2, boolean up) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("uid1", Integer.toString(uid1));
        args.put("uid2", Integer.toString(uid2));
        args.put("up", Boolean.toString(up));

        APITask task = new APITask("rate", args);
        task.execute();
    }

    private void _friendResponse(int uid1, int uid2, boolean accept) {
        Dictionary<String, String> args = new Hashtable<String, String>();
        args.put("uid1", Integer.toString(uid1));
        args.put("uid2", Integer.toString(uid2));
        args.put("accept", Boolean.toString(accept));

        APITask task = new APITask("friendresponse", args);
        task.execute();
    }

    private void _rendezvousResponse(int pid, boolean accept) {
        Dictionary<String, String> args = new Hashtable<String, String>();
        args.put("pid", Integer.toString(pid));
        args.put("accept", Boolean.toString(accept));

        APITask task = new APITask("rendezvousresponse", args);
        task.execute();
    }

    private void _updateLocation(int uid, double lat, double lng) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("uid", Integer.toString(uid));
        args.put("lat", Double.toString(lat));
        args.put("lng", Double.toString(lng));

        APITask task = new APITask("updatelocation", args);
        task.execute();
    }

    private void _rendezvousRequest(int pid, double lat, double lng, int time) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("pid", Integer.toString(pid));
        args.put("lat", Double.toString(lat));
        args.put("lng", Double.toString(lng));
        args.put("time", Integer.toString(time));

        APITask task = new APITask("rendezvousrequest", args);
        task.execute();
    }

    private void _finalize(int pid, int uid) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("pid", Integer.toString(pid));
        args.put("uid", Integer.toString(uid));

        APITask task = new APITask("finalize", args);
        task.execute();
    }



    public class APITask extends AsyncTask<Void, Void, Object> {

        private final Dictionary<String, String> mArgs;
        private final Object mSender;
        private final String mMethod;

        APITask(String action, Dictionary<String, String> args) {
            this(null, null, action, args);
        }

        APITask(Object sender, String method, String action, Dictionary<String, String> args) {
            mSender = sender;
            mMethod = method;
            mArgs = args;
            mArgs.put("action", action);
            mArgs.put("key", "2vdiaytq5gqYUjL4Nm8MCqH2UciBlKEk6gUhW3cW");
        }

        @Override
        protected Object doInBackground(Void... params) {
            StringBuilder u = new StringBuilder("http://ht154.com/deliveries.php?");
            Enumeration<String> keys = mArgs.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = mArgs.get(key);
                u.append(key);
                u.append("=");
                u.append(value);
                u.append("&");
            }

            int responseCode = 0;

            try {
                StringBuilder sb = new StringBuilder();
                BufferedInputStream bis = null;
                URL url = new URL(u.toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);
                responseCode = con.getResponseCode();

                if (responseCode == 200) {
                    bis = new java.io.BufferedInputStream(con.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
                    String line = null;

                    while ((line = reader.readLine()) != null)
                        sb.append(line);

                    bis.close();
                }

                String resp = sb.toString();
                if (resp.charAt(0) == '[') {
                    return new JSONArray(resp);
                } else {
                    return new JSONObject(resp);
                }
            } catch (Exception e) {
                System.err.println("HTTP Error: " + responseCode + " at URL " + u);
                System.err.println(e);
            }

            return null;
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (mSender != null && mMethod != null && !mMethod.equals("")) {
                try {
                    Method m = mSender.getClass().getMethod(mMethod, Object.class);
                    m.invoke(mSender, result);
                } catch (Exception e) {
                    System.err.println("Callback missing: " + mMethod + " in class " + getClass().getName());
                    System.err.println(e);
                }
            }
        }
    }

    private static API _instance;

    private API() {

    }

    public static API get_instance() {
        if (_instance == null) {
            _instance = new API();
        }

        return _instance;
    }

}
