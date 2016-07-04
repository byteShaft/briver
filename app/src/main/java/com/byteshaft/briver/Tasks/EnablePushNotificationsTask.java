package com.byteshaft.briver.Tasks;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.Helpers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fi8er1 on 01/07/2016.
 */

public class EnablePushNotificationsTask extends AsyncTask<Void, Void, Void> {

    HttpURLConnection connection;
    public static int responseCode;
    public static boolean isEnablePushNotificationsTaskRunning;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isEnablePushNotificationsTaskRunning = true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        URL url = null;
        try {
            url = new URL("http://46.101.75.194:8080/users/" + AppGlobals.getUserID());
        Log.i("UserID", "" + AppGlobals.getUserID());
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("X-Api-Key", AppGlobals.getToken());
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes("token=" + AppGlobals.getGcmToken());
        out.flush();
        out.close();
        responseCode = connection.getResponseCode();
    } catch (IOException e) {
        e.printStackTrace();
    }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        isEnablePushNotificationsTaskRunning = false;
        if (responseCode == 200) {
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().getWindow().getDecorView(),
                    "Push Notifications Enabled", Snackbar.LENGTH_LONG, "#ffffff");
            AppGlobals.setPushNotificationsEnabled(true);
        } else {
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().getWindow().getDecorView(),
                    "Failed to enable Push Notifications", Snackbar.LENGTH_LONG, "#f44336");
            if (MainActivity.isMainActivityRunning) {
                Helpers.AlertDialogWithPositiveFunctionNegativeButton(AppGlobals.getRunningActivityInstance().getApplicationContext(),
                        "Caution", "Failed to enable PushNotifications", "Retry", "Dismiss", retry);
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        isEnablePushNotificationsTaskRunning = false;
    }


    final Runnable retry = new Runnable() {
        public void run() {
            execute();
        }
    };
}
