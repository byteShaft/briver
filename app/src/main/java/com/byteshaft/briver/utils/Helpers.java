package com.byteshaft.briver.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Helpers {

    public static int countDownTimerMillisUntilFinished;
    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

//    public static String getAddress(Context context, LatLng latLng) {
//        Geocoder geocoder;
//        List<Address> addresses;
//        String address = null;
//        geocoder = new Geocoder(context, Locale.getDefault());
//        try {
//            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//            address = addresses.get(0).getAddressLine(0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return address;
//    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi != null && wifi.isConnectedOrConnecting() || mobile != null && mobile.isConnectedOrConnecting();
    }

    public static boolean isInternetWorking() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean isAnyLocationServiceAvailable() {
        LocationManager locationManager = getLocationManager();
        return isGpsEnabled(locationManager) || isNetworkBasedGpsEnabled(locationManager);
    }

    public static boolean isHighAccuracyLocationServiceAvailable() {
        LocationManager locationManager = getLocationManager();
        return isGpsEnabled(locationManager) && isNetworkBasedGpsEnabled(locationManager);
    }

    private static LocationManager getLocationManager() {
        return (LocationManager) AppGlobals.getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    private static boolean isGpsEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private static boolean isNetworkBasedGpsEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled((LocationManager.NETWORK_PROVIDER));
    }

    public static void AlertDialogMessage(Context context, String title, String message, String neutralButtonText) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(neutralButtonText, null)
                .show();
    }

    public static void AlertDialogMessageWithPositiveFunction(
            Context context, String title, String message, String positiveButtonText,
            final Runnable listenerOk) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listenerOk.run();
                    }
                })
                .show();
    }


    public static void AlertDialogWithPositiveFunctionNegativeButton(
            Context context, String title, String message, String positiveButtonText,
            String negativeButtonText, final Runnable listenerYes) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listenerYes.run();
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void AlertDialogWithPositiveNegativeFunctions(
            Context context, String title, String message, String positiveButtonText,
            String negativeButtonText, final Runnable listenerYes, final Runnable listenerNo) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listenerYes.run();
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerNo.run();
                    }
                })
                .show();
    }

    public static String secondsToMinutesSeconds(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public static void setCountDownTimer(int totalTime, int tickTime, final Runnable functionTick, final Runnable functionFinished) {
        CountDownTimer Count = new CountDownTimer(totalTime, tickTime) {
            public void onTick(long millisUntilFinished) {
                functionTick.run();
                countDownTimerMillisUntilFinished = (int) millisUntilFinished;
            }

            public void onFinish() {
                functionFinished.run();
            }
        };
        Count.start();
    }

}
