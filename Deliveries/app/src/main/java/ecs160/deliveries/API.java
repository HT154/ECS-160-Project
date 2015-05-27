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

/**
 * Created by Josh on 5/25/15.
 */

public class API {

    public void register(Object sender, String method, String username, String password) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("name", username);
        args.put("pass", password);

        APITask task = new APITask(sender, method, "register", args);
        task.execute();
    }

    public void login(Object sender, String method, String username, String password) {
        Dictionary<String,String > args = new Hashtable<String, String>();
        args.put("name", username);
        args.put("pass", password);

        APITask task = new APITask(sender, method, "login", args);
        task.execute();
    }






    public class APITask extends AsyncTask<Void, Void, JSONArray> {

        private final Dictionary<String, String> mArgs;
        private final Object mSender;
        private final String mMethod;

        APITask(Object sender, String method, String action, Dictionary<String, String> args) {
            mSender = sender;
            mMethod = method;
            mArgs = args;
            mArgs.put("action", action);
            mArgs.put("key", "2vdiaytq5gqYUjL4Nm8MCqH2UciBlKEk6gUhW3cW");
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
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

                return new JSONArray(sb.toString());
            } catch (Exception e) {
                System.err.println("HTTP Error: " + responseCode + " at URL " + u);
                System.err.println(e);
            }

            return null;
        }

        protected void onPostExecute(JSONArray result) {
            super.onPostExecute(result);

            try {
                Method m = mSender.getClass().getMethod(mMethod, JSONArray.class);
                m.invoke(mSender, result);
            } catch (Exception e) {
                System.err.println("Callback missing: " + mMethod + " in class " + getClass().getName());
                System.err.println(e);
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
