package com.google.android.apps.miyagi.development.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by marcin on 20.01.2017.
 */

public class NetworkUtils {

    /**
     * Checks network connection, returns true if device is online.
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean available = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            available = true;
        }

        return available;
    }

    /**
     * Checks network connection, returns true if device is offline.
     */
    public static boolean isOffline(Context context) {
        return !isOnline(context);
    }

}
