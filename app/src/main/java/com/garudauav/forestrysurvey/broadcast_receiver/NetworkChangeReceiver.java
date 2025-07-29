package com.garudauav.forestrysurvey.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.garudauav.forestrysurvey.utils.CommonFunctions;

public abstract class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isNetworkConnected(context)) {
            onNetworkConnected();
        } else {
            onNetworkDisconnected();
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    // Abstract methods to be implemented by subclasses
    public abstract void onNetworkConnected();

    public abstract void onNetworkDisconnected();
}
