package com.byteshaft.briver.utils;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DriverService extends Service {

    public static boolean driverLocationReportingServiceIsRunning;
    public static int responseCode;

    HttpURLConnection connection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        driverLocationReportingServiceIsRunning = true;
//        startService(new Intent(getApplicationContext(), LocationService.class));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        driverLocationReportingServiceIsRunning = false;
//        stopService(new Intent(getApplicationContext(), LocationService.class));
    }


    private class DriverLocationPostingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://46.101.75.194:8080/locations/set");

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("X-Api-Key", AppGlobals.getToken());

                InputStream in = (InputStream) connection.getContent();
                int ch;
                StringBuilder sb;

                sb = new StringBuilder();
                while ((ch = in.read()) != -1)
                    sb.append((char) ch);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (responseCode == 200 && DriverService.driverLocationReportingServiceIsRunning) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new DriverLocationPostingTask().execute();
                    }
                }, 10000);
            }
        }
    }
}
