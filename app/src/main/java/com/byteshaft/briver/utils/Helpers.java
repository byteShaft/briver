package com.byteshaft.briver.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.byteshaft.briver.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Helpers {

    public static int countDownTimerMillisUntilFinished;
    private static ProgressDialog progressDialog;
    private static ProgressDialog progressDialogWithPositiveButton;
    private static CountDownTimer countdownTimer;
    private static boolean isCountDownTimerRunning;

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

    public static void showProgressDialogWithPositiveButton(Context context, String message, String positiveButtonText, final Runnable listenerOk) {
        progressDialogWithPositiveButton = new ProgressDialog(context);
        progressDialogWithPositiveButton.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialogWithPositiveButton.setMessage(message);
        progressDialogWithPositiveButton.setCancelable(false);
        progressDialogWithPositiveButton.setIndeterminate(true);
        progressDialogWithPositiveButton.setCanceledOnTouchOutside(false);
        progressDialogWithPositiveButton.setButton(DialogInterface.BUTTON_POSITIVE, positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listenerOk != null) {
                    listenerOk.run();
                }
            }
        });
        progressDialogWithPositiveButton.show();
    }

    public static void dismissProgressWithPositiveButtonDialog() {
        if (progressDialogWithPositiveButton != null) {
            progressDialogWithPositiveButton.dismiss();
        }
    }

    public static void closeSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        activity.getCurrentFocus().clearFocus();
    }

    public static void openSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
        activity.getCurrentFocus().clearFocus();
    }


    public static String getAddress(Context context, LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        String address;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            address = "Address not found";
            e.printStackTrace();
        }
        return address;
    }

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
                .setCancelable(false)
                .setPositiveButton(neutralButtonText, null)
                .show();
    }

    public static void AlertDialogMessageWithPositiveFunction(
            Context context, String title, String message, String positiveButtonText,
            final Runnable listenerOk) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
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
                .setCancelable(false)
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
                .setCancelable(false)
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

    public static void AlertDialogWithPositiveNegativeNeutralFunctions(
            Context context, String title, String message, String positiveButtonText,
            String negativeButtonText, String neutralButtonText, final Runnable listenerYes,
            final Runnable listenerNo, final Runnable listenerNeutral) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
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
                .setNeutralButton(neutralButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listenerNeutral.run();
                    }
                })
                .show();
    }



    public static void customDialogWithPositiveFunctionNegativeButtonForOnMapMarkerClickHiring (
            Context context, String fullName, String eMail, String contact, String address,
            String locationLastUpdated, String experience, String numberOfHires, String bio,
            final Runnable listenerYes) {
        final Dialog onMapMarkerClickHireDialog = new Dialog(context);
        onMapMarkerClickHireDialog.setContentView(R.layout.layout_on_map_marker_click_hire_dialog);

        onMapMarkerClickHireDialog.setCancelable(false);
        onMapMarkerClickHireDialog.setTitle("QuickHire this driver?");

        TextView tvFullName = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_full_name);
        tvFullName.setText("Name: " + fullName);

        TextView tvEmail = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_email);
        tvEmail.setText("Email: " + Helpers.replaceFirstThreeCharacters(eMail));

        TextView tvContact = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_contact);
        tvContact.setText("Contact: " + Helpers.replaceLastThreeCharacters(contact));

        TextView tvNumberOfHires = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_number_of_hires);
        tvNumberOfHires.setText("Total Hires: " + numberOfHires);

        TextView tvAddress = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_address);
        tvAddress.setText("Address: " + address);

        TextView tvLocationLastUpdated = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_location_last_updated);
        tvLocationLastUpdated.setText("Location Last Updated: " + Helpers.getTimeAgo(Helpers.getTimeInMillis(locationLastUpdated)));

        TextView tvExperience = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_driving_experience);
        int experienceInt = -1;
        try {
            experienceInt = Integer.parseInt(experience);
        } catch (NumberFormatException ignored) {}

        if (experienceInt < 2) {
            tvExperience.setText("Driving Experience: " + experience + " " + "Year");
        } else {
            tvExperience.setText("Driving Experience: " + experience + " " + "Years");
        }

        TextView tvBio = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_bio);
        if (bio.trim().length() > 1) {
            tvBio.setText("Bio: " + bio);
        } else {
            tvBio.setVisibility(View.GONE);
        }

        Button buttonNo = (Button) onMapMarkerClickHireDialog.findViewById(R.id.btn_driver_hire_dialog_cancel);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapMarkerClickHireDialog.dismiss();
            }
        });

        Button buttonYes = (Button) onMapMarkerClickHireDialog.findViewById(R.id.btn_driver_hire_dialog_hire);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerYes.run();
            }
        });
        Helpers.dismissProgressDialog();
        onMapMarkerClickHireDialog.show();

    }

    public static String secondsToMinutesSeconds(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public static void setCountDownTimer(int totalTime, int tickTime, final Runnable functionTick, final Runnable functionFinished) {
        countdownTimer = new CountDownTimer(totalTime, tickTime) {
            public void onTick(long millisUntilFinished) {
                isCountDownTimerRunning = true;
                functionTick.run();
                countDownTimerMillisUntilFinished = (int) millisUntilFinished;
            }

            public void onFinish() {
                functionFinished.run();
                isCountDownTimerRunning = false;
            }
        };
        countdownTimer.start();
    }

    public static void stopCountDownTimer() {
        if (isCountDownTimerRunning) {
            countdownTimer.cancel();
        }
    }

    public static void showSnackBar(View view, String message, int time, String textColor) {
        Snackbar snackbar = Snackbar.make(view, message, time);
        View snackBarView = snackbar.getView();
        TextView snackBarText = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        snackBarText.setGravity(Gravity.CENTER_HORIZONTAL);
        snackBarText.setTextColor(Color.parseColor(textColor));
        snackbar.show();
    }

    public static void openAppDetailsActivityForSettingPermissions(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }


    public static void openInstallationActivityForPlayServices(final Activity context) {
        if (context == null) {
            return;
        }
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_BROWSABLE);
        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms&hl=en"));
        context.startActivity(i);
    }


    public static final Runnable exitApp = new Runnable() {
        public void run() {
            AppGlobals.getRunningActivityInstance().finish();
            System.exit(0);
        }
    };

    public static Runnable openPermissionsSettingsForMarshmallow = new Runnable() {
        public void run() {
            Helpers.openAppDetailsActivityForSettingPermissions(AppGlobals.getRunningActivityInstance());
            AppGlobals.getRunningActivityInstance().onBackPressed();
        }
    };

    public static Runnable openPlayServicesInstallation = new Runnable() {
        public void run() {
            Helpers.openAppDetailsActivityForSettingPermissions(AppGlobals.getRunningActivityInstance());
            AppGlobals.getRunningActivityInstance().onBackPressed();
        }
    };

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static long getTimeInMillis(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(time);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static String replaceLastThreeCharacters(String s) {
        int length = s.length();
        return s.substring(0, length - 3) + "***";
    }

    public static String replaceFirstThreeCharacters(String s) {
        return "***" + s.substring(3);
    }

}
