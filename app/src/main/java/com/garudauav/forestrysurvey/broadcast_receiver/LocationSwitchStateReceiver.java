package com.garudauav.forestrysurvey.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

public class LocationSwitchStateReceiver extends BroadcastReceiver {
    private LocationStateListener listener;

    public  LocationSwitchStateReceiver(){

    }

    public LocationSwitchStateReceiver(LocationStateListener listener) {
        this.listener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (LocationManager.MODE_CHANGED_ACTION.equals(action)) {
            // Check if the location is enabled
            boolean isLocationEnabled = isLocationEnabled(context);
            if (listener != null) {
                listener.onLocationStateChanged(isLocationEnabled);
            }
        }
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
      //  boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return !isGpsEnabled;
    }
    public interface LocationStateListener {
        void onLocationStateChanged(boolean isLocationEnabled);
    }
}

