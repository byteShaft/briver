package com.beza.briver.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.beza.briver.MainActivity;
import com.beza.briver.utils.AppGlobals;
import com.beza.briver.utils.EndPoints;
import com.beza.briver.utils.Helpers;
import com.beza.briver.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

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
        try {
            connection = WebServiceHelpers.openConnectionForUrl(EndPoints.PUSH_NOTIFICATION_ACTIVATION, "POST", true);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(getPushNotificationEnablingString(AppGlobals.getGcmToken(), Helpers.getDeviceID()));
        out.flush();
        out.close();
        responseCode = connection.getResponseCode();
            Log.i("PSResponseCode", "" + responseCode);
            Log.i("PSResponseMessage", "" + connection.getResponseMessage());
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
            Toast.makeText(AppGlobals.getContext(), "Push Notifications Enabled", Toast.LENGTH_LONG).show();
            AppGlobals.setPushNotificationsEnabled(true);
        } else {
            if (MainActivity.isMainActivityRunning) {
                Helpers.AlertDialogWithPositiveFunctionNegativeButton(AppGlobals.getRunningActivityInstance(),
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
            new EnablePushNotificationsTask().execute();
        }
    };

    public static String getPushNotificationEnablingString (String key, String deviceId) {
        JSONObject json = new JSONObject();
        try {
            json.put("push_key", key);
            json.put("device_id", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
