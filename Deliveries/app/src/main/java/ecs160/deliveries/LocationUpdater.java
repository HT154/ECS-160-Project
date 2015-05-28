package ecs160.deliveries;

import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

public class LocationUpdater extends Service {
    private final Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask;
    private LocationManager mLocationManager;
    private int mUID;
    private Location mLatestLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationUpdaterListener listener = new LocationUpdaterListener();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, listener);
    }

    @Override
    public void onDestroy() {
        stopUpdating();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUID = intent.getIntExtra("uid", 0);

        mUpdateTimeTask = new Runnable() {
            public void run() {
                if (mLatestLocation != null) {
                    System.out.print("Updating " + mUID + " location to: ");
                    System.out.println(mLatestLocation);
                    API.updateLocation(mUID, mLatestLocation.getLatitude(), mLatestLocation.getLongitude());
                } else {
                    System.out.println("No Location Acquired");
                }

                mHandler.postDelayed (mUpdateTimeTask, 60000);
            }
        };

        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void stopUpdating() {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private class LocationUpdaterListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            mLatestLocation = loc;
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}