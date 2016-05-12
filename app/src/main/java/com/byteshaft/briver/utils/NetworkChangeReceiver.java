package com.byteshaft.briver.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.WelcomeActivity;

/**
 * Created by fi8er1 on 26/04/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public static boolean isNetworkConnected;
    final Runnable exitApp = new Runnable() {
        public void run() {
            if (MainActivity.isMainActivityRunning) {
                MainActivity.getInstance().finish();
            } else if (WelcomeActivity.isWelcomeActivityRunning) {
                WelcomeActivity.getInstance().finish();
            }
            System.exit(0);
        }
    };
    final Runnable recheckNetworkConnectivity = new Runnable() {
        public void run() {
            if (!isNetworkConnected) {
                Helpers.AlertDialogWithPositiveNegativeFunctions(getActivityToShowDialogOnNetworkChange(),
                        "Network Unavailable", "Device is disconnected from the network. Check Network Connectivity.",
                        "Exit App", "ReCheck", exitApp, recheckNetworkConnectivity);
            }
        }
    };

    private static Activity getActivityToShowDialogOnNetworkChange() {
        if (MainActivity.isMainActivityRunning) {
            return MainActivity.getInstance();
        } else if (WelcomeActivity.isWelcomeActivityRunning) {
            return WelcomeActivity.getInstance();
        }
        return null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        isNetworkConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
        if (isNetworkConnected) {
            Log.d("Network Available ", "YES");
        } else {
            if (MainActivity.isMainActivityRunning || WelcomeActivity.isWelcomeActivityRunning) {
                Helpers.AlertDialogWithPositiveNegativeFunctions(getActivityToShowDialogOnNetworkChange(),
                        "Network Unavailable", "Device is disconnected from the network. Check Network Connectivity.",
                        "Exit App", "ReCheck", exitApp, recheckNetworkConnectivity);
            }
            Log.d("Network Available ", "NO");
        }
    }
}
