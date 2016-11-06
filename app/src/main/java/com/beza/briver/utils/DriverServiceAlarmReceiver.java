package com.beza.briver.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

public class DriverServiceAlarmReceiver extends BroadcastReceiver {

    public static boolean driverLocationReportingServiceIsRunning;
    public static int responseCode;
    public static boolean isLocationServiceCalledFromService;
    private static HttpURLConnection connection;
    private static DriverLocationPostingTask taskDriverLocationPosting;
    private static boolean isDriverLocationPostingTaskRunning;
    LocationService mLocationService;

    public static String getDriverLocationPostingString(
            String location) {
        return "{" +
                String.format("\"location\": \"%s\"", location) +
                "}";
    }

    public static void postLocationFromDriverService() {
        taskDriverLocationPosting = (DriverLocationPostingTask) new DriverLocationPostingTask().execute();
        DriverServiceAlarmReceiver.isLocationServiceCalledFromService = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppGlobals.getUserType() == 1) {
            if (AppGlobals.isLoggedIn() && AppGlobals.isAlarmSet() && AppGlobals.getLocationReportingType() == 1) {
                if (Helpers.isNetworkAvailable(context) && Helpers.isAnyLocationServiceAvailable()) {
                    mLocationService = new LocationService(context);
                    isLocationServiceCalledFromService = true;
                    mLocationService.startLocationServices();
                } else {
                    AppGlobals.setLocationUploadPending(true);
                }
            }
        }
        Log.i("Alarm", "Receive");
    }

    private static class DriverLocationPostingTask extends AsyncTask<Void, Integer, Void> {
        String locationString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isDriverLocationPostingTaskRunning = true;
            locationString = LocationService.driverCurrentLocation.latitude + "," + LocationService.driverCurrentLocation.longitude;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url;
            try {
                url = EndPoints.BASE_ACCOUNTS_ME;
                connection = WebServiceHelpers.openConnectionForUrl(url, "PUT", true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(getDriverLocationPostingString(locationString));
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
            isDriverLocationPostingTaskRunning = false;
            AppGlobals.setLocationUploadPending(false);
            DriverLocationAlarmHelper.setAlarm(AppGlobals.getDriverLocationReportingIntervalTime());
            if (responseCode == 200) {
                Log.i("LocationPosting", "Success");
            } else {
                Log.e("LocationPosting", "Failed");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isDriverLocationPostingTaskRunning = false;
        }
    }
}
