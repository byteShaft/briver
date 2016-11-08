package com.beza.briver.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.beza.briver.MainActivity;
import com.beza.briver.R;
import com.beza.briver.Tasks.ReviewHireTask;
import com.beza.briver.fragments.HireFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.beza.briver.utils.AppGlobals.getRunningActivityInstance;

public class Helpers {

    public static final Runnable exitApp = new Runnable() {
        public void run() {
            getRunningActivityInstance().finish();
            System.exit(0);
        }
    };
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static String nameForRatingsDialog;
    public static LatLng latLngForNavigation;
    public static int countDownTimerMillisUntilFinished;
    public static boolean isCustomUserDetailsDialogOpenedFromMap;
    public static Spinner spinnerServiceHours;
    public static Runnable openPermissionsSettingsForMarshmallow = new Runnable() {
        public void run() {
            Helpers.openAppDetailsActivityForSettingPermissions(getRunningActivityInstance());
            getRunningActivityInstance().onBackPressed();
        }
    };
    public static Runnable openPlayServicesInstallation = new Runnable() {
        public void run() {
            Helpers.openInstallationActivityForPlayServices(getRunningActivityInstance());
            getRunningActivityInstance().onBackPressed();
        }
    };
    static String docOneMain;
    static String docTwoMain;
    static String docThreeMain;
    static ImageView ibPhotoOne;
    static ImageView ibPhotoTwo;
    static ImageView ibPhotoThree;
    static LinearLayout llDetails;
    static LinearLayout llDocuments;
    static Bitmap bmOne;
    static Bitmap bmTwo;
    static Bitmap bmThree;
    static Button dialogButtonYes;
    static Button dialogButtonNo;
    static Dialog ratingDialog;
    static RetrieveDocumentsTask taskRetrieveDocuments;
    private static ProgressDialog progressDialog;
    private static ProgressDialog progressDialogWithPositiveButton;
    private static CountDownTimer countdownTimer;
    private static boolean isCountDownTimerRunning;
    private static boolean dialogDocumentsShown;
    private static InputMethodManager inputMethodManager;
    private static boolean isSoftKeyboardOpen;
    private static boolean fullImageViewShown;

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

    public static void dismissCustomRatingDialog() {
        ratingDialog.dismiss();
    }

    public static boolean isIsSoftKeyboardOpen() {
        return isSoftKeyboardOpen;
    }

    public static void setIsSoftKeyboardOpen(boolean state) {
        isSoftKeyboardOpen = state;
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
        inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        activity.getCurrentFocus().clearFocus();
    }

    public static void openSoftKeyboard(Activity activity) {
        inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void openSoftKeyboardOnEditText(Activity activity, EditText et) {
        inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
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

    public static void AlertDialogWithPositiveNegativeFunctionsNeutralButton(
            Context context, String title, String message, String positiveButtonText,
            String negativeButtonText, String neutralButtonText, final Runnable listenerYes,
            final Runnable listenerNo) {
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
                        if (listenerNo != null) {
                            listenerNo.run();
                        }
                    }
                })
                .setNeutralButton(neutralButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void AlertDialogWithPositiveNegativeNeutralFunctions (
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
                        if (listenerNo != null) {
                            listenerNo.run();
                        }
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

    public static void customRatingDialog(final Context context, String fullName, final String hireId) {
        ratingDialog = new Dialog(context);
        ratingDialog.setContentView(R.layout.layout_rating_dialog);
        ratingDialog.setTitle("Rate " + fullName);
        ratingDialog.setCancelable(false);
        final RatingBar rBarRatingDialog = (RatingBar) ratingDialog.findViewById(R.id.rBar_rating_dialog);

        Button btnRatingDialogDone = (Button) ratingDialog.findViewById(R.id.btn_rating_dialog_done);
        btnRatingDialogDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("rating", "" + rBarRatingDialog.getRating());
                String[] reviewTaskString = new String[] {hireId, "1", String.valueOf(rBarRatingDialog.getRating())};
                new ReviewHireTask().execute(reviewTaskString);
            }
        });

        Button btnRatingDialogLater = (Button) ratingDialog.findViewById(R.id.btn_rating_dialog_cancel);
        btnRatingDialogLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDialog.dismiss();
            }
        });

        ratingDialog.show();
    }

    public static void WebViewAlertDialog(Context context, String url) {
        final Dialog dialog = new Dialog(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_tos_dialog, null);
        dialog.setContentView(layout);
        dialog.setTitle("Terms of Service");
        dialog.setCancelable(false);
        final ProgressBar pbTOSDialog = (ProgressBar) layout.findViewById(R.id.pb_tos_dialog);
        Button dismissButton = (Button) layout.findViewById(R.id.btn_tos_dialog_dismiss);
        final WebView wvTOS = (WebView) layout.findViewById(R.id.wv_tos_dialog);
        WebSettings webSettings = wvTOS.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        wvTOS.setInitialScale(100);
        wvTOS.loadUrl(url);
        wvTOS.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbTOSDialog.setVisibility(View.GONE);
                wvTOS.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void customDialogWithPositiveFunctionNegativeButtonForOnMapMarkerClickHiring(
            final Context context, String fullName, String eMail, String contact, String address,
            String locationLastUpdated, String experience, String numberOfHires, String bio, String status, String numberOfRatings, String numberOfStars,
            final Runnable listenerYes, final String docOne, final String docTwo, final String docThree) {
        final Dialog onMapMarkerClickHireDialog = new Dialog(context);
        if (!isCustomUserDetailsDialogOpenedFromMap) {
            onMapMarkerClickHireDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        onMapMarkerClickHireDialog.setContentView(R.layout.layout_on_map_marker_click_hire_dialog);

        final RelativeLayout rlDialogDocumentsMain = (RelativeLayout) onMapMarkerClickHireDialog.findViewById(R.id.rl_main_document);
        final RelativeLayout rlDialogButtons = (RelativeLayout) onMapMarkerClickHireDialog.findViewById(R.id.buttonsLayout);
        final ImageView ivDialogDocumentsMain = (ImageView) onMapMarkerClickHireDialog.findViewById(R.id.iv_main_document);
        LinearLayout llSpinner = (LinearLayout) onMapMarkerClickHireDialog.findViewById(R.id.ll_custom_dialog_spinner);
        spinnerServiceHours = (Spinner) onMapMarkerClickHireDialog.findViewById(R.id.spinner_hire_dialog_service_hours);

        ImageButton btnMainDocumentClose = (ImageButton) onMapMarkerClickHireDialog.findViewById(R.id.btn_document_view_close);
        btnMainDocumentClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlDialogDocumentsMain.setVisibility(View.GONE);
                rlDialogButtons.setVisibility(View.VISIBLE);
                llDocuments.setVisibility(View.VISIBLE);
                fullImageViewShown = false;
            }
        });

        ibPhotoOne = (ImageView) onMapMarkerClickHireDialog.findViewById(R.id.ib_user_details_photo_one);
        ibPhotoOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDocuments.setVisibility(View.GONE);
                ivDialogDocumentsMain.setImageBitmap(bmOne);
                rlDialogButtons.setVisibility(View.GONE);
                rlDialogDocumentsMain.setVisibility(View.VISIBLE);
                fullImageViewShown = true;
            }
        });

        ibPhotoTwo = (ImageView) onMapMarkerClickHireDialog.findViewById(R.id.ib_user_details_photo_two);
        ibPhotoTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDocuments.setVisibility(View.GONE);
                ivDialogDocumentsMain.setImageBitmap(bmTwo);
                rlDialogButtons.setVisibility(View.GONE);
                rlDialogDocumentsMain.setVisibility(View.VISIBLE);
                fullImageViewShown = true;
            }
        });

        ibPhotoThree = (ImageView) onMapMarkerClickHireDialog.findViewById(R.id.ib_user_details_photo_three);
        ibPhotoThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDocuments.setVisibility(View.GONE);
                ivDialogDocumentsMain.setImageBitmap(bmThree);
                rlDialogButtons.setVisibility(View.GONE);
                rlDialogDocumentsMain.setVisibility(View.VISIBLE);
                fullImageViewShown = true;
            }
        });

        llDetails = (LinearLayout) onMapMarkerClickHireDialog.findViewById(R.id.ll_custom_user_details_dialog);
        llDocuments = (LinearLayout) onMapMarkerClickHireDialog.findViewById(R.id.ll_custom_user_details_dialog_documents);

        onMapMarkerClickHireDialog.setCancelable(false);
        if (isCustomUserDetailsDialogOpenedFromMap) {
            onMapMarkerClickHireDialog.setTitle("QuickHire this driver?");
        } else {
            onMapMarkerClickHireDialog.setTitle("User Details");
        }

        RatingBar rBarMarkerDialog = (RatingBar) onMapMarkerClickHireDialog.findViewById(R.id.rBar_driver_hiring_dialog);
        Float starsRatingBar = Float.parseFloat(numberOfStars);

        if (starsRatingBar > 0.0) {
            rBarMarkerDialog.setRating(starsRatingBar);
        } else {
            rBarMarkerDialog.setRating((float) 0.0);
        }

        TextView tvNumberOfStars = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_rBar_hiring_dialog_number_of_ratings);
        tvNumberOfStars.setText("(" + numberOfRatings + ")");

        TextView tvFullName = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_full_name);
        tvFullName.setText("Name: " + fullName);

        TextView tvEmail = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_email);
        if (isCustomUserDetailsDialogOpenedFromMap) {
            tvEmail.setText("Email: " + Helpers.replaceFirstThreeCharacters(eMail));
        } else {
            tvEmail.setText("Email: " + eMail);
        }

        TextView tvContact = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_contact);
        if (isCustomUserDetailsDialogOpenedFromMap) {
            tvContact.setText("Contact: " + Helpers.replaceLastThreeCharacters(contact));
        } else {
            tvContact.setText("Contact: " + contact);
            llSpinner.setVisibility(View.GONE);
            TextView tvCaution = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_dialog_caution);
            tvCaution.setVisibility(View.GONE);
            TextView tvCautionTwo = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_dialog_caution_two);
            tvCautionTwo.setVisibility(View.GONE);
        }

        TextView tvStatus = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_status);
        if (status != null) {
            int statusInt = Integer.parseInt(status);
            if (statusInt == 1) {
                tvStatus.setText("Status: Available");
            } else if (statusInt == 2) {
                tvStatus.setText("Status: Online");
            } else {
                tvStatus.setVisibility(View.GONE);
            }
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        TextView tvNumberOfHires = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_number_of_hires);
        tvNumberOfHires.setText("Total Hires: " + numberOfHires);

        TextView tvAddress = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_address);
        if (address != null) {
            tvAddress.setText("Address: " + address);
        } else {
            tvAddress.setVisibility(View.GONE);
        }

        TextView tvLocationLastUpdated = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_location_last_updated);
        if (AppGlobals.getUserType() == 1) {
            if (docOne.equalsIgnoreCase("0")) {
                tvLocationLastUpdated.setText("Transmission Type: Manual");
            } else {
                tvLocationLastUpdated.setText("Transmission Type: Automatic");
            }
        } else {
            if (locationLastUpdated != null) {
                tvLocationLastUpdated.setText("Location Last Updated: " + Helpers.getTimeAgo(Helpers.getTimeInMillis(locationLastUpdated)));
            } else {
                tvLocationLastUpdated.setVisibility(View.GONE);
            }
        }

        TextView tvExperience = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_driving_experience);
        int experienceInt = -1;
        try {
            experienceInt = Integer.parseInt(experience);
        } catch (NumberFormatException ignored) {
        }

        if (AppGlobals.getUserType() == 1) {
            if (docTwo.equalsIgnoreCase("0")) {
                tvExperience.setText("Vehicle Type: Mini");
            } else if (docTwo.equalsIgnoreCase("1")) {
                tvExperience.setText("Vehicle Type: Hatchback");
            } else if (docTwo.equalsIgnoreCase("2")) {
                tvExperience.setText("Vehicle Type: Sedan");
            } else if (docTwo.equalsIgnoreCase("3")) {
                tvExperience.setText("Vehicle Type: Luxury");
            }
        } else {
            if (experience != null) {
                if (experienceInt < 2) {
                    tvExperience.setText("Driving Experience: " + experience + " " + "Year");
                } else {
                    tvExperience.setText("Driving Experience: " + experience + " " + "Years");
                }
            } else {
                tvExperience.setVisibility(View.GONE);
            }
        }

        TextView tvBio = (TextView) onMapMarkerClickHireDialog.findViewById(R.id.tv_driver_hire_dialog_bio);

        if (AppGlobals.getUserType() == 1) {
            tvBio.setText("Vehicle: " + docThree);
        } else {
            if (bio != null && bio.trim().length() > 1) {
                tvBio.setText("Bio: " + bio);
            } else {
                tvBio.setVisibility(View.GONE);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                HireFragment.itemsForHoursSelectingDialog);
        spinnerServiceHours.setAdapter(adapter);

        dialogButtonNo = (Button) onMapMarkerClickHireDialog.findViewById(R.id.btn_driver_hire_dialog_cancel);
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogDocumentsShown) {
                    llDocuments.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            llDocuments.setVisibility(View.GONE);
                            llDetails.setVisibility(View.VISIBLE);
                            dialogButtonNo.setText("Dismiss");
                            dialogButtonYes.setVisibility(View.VISIBLE);
                            dialogDocumentsShown = false;
                        }
                    }, 150);
                } else {
                    onMapMarkerClickHireDialog.dismiss();
                }
            }
        });
        dialogButtonYes = (Button) onMapMarkerClickHireDialog.findViewById(R.id.btn_driver_hire_dialog_hire);
        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCustomUserDetailsDialogOpenedFromMap && AppGlobals.getUserType() == 0) {
                    taskRetrieveDocuments = (RetrieveDocumentsTask) new RetrieveDocumentsTask().execute();
                } else {
                    listenerYes.run();
                    onMapMarkerClickHireDialog.dismiss();
                }
            }
        });

        if (!isCustomUserDetailsDialogOpenedFromMap && AppGlobals.getUserType() == 0) {
            dialogButtonYes.setText("Show Documents");
            dialogButtonNo.setText("Dismiss");
            docOneMain = docOne;
            docTwoMain = docTwo;
            docThreeMain = docThree;
        } else if (!isCustomUserDetailsDialogOpenedFromMap && AppGlobals.getUserType() == 1) {
            dialogButtonYes.setVisibility(View.INVISIBLE);
            dialogButtonNo.setText("Dismiss");
        }
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
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " Minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " Hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + " Days ago";
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

    public static String formatTimeToDisplay(String time) {
        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdfIn.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdfOut = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
        return sdfOut.format(date);
    }

    public static String replaceLastThreeCharacters(String s) {
        int length = s.length();
        return s.substring(0, length - 3) + "***";
    }

    public static String replaceFirstThreeCharacters(String s) {
        return "***" + s.substring(3);
    }

    public static void initiateCallIntent(Activity activity, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        activity.startActivity(intent);
    }

    public static void initiateEmailIntent(Activity activity, String email, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        intent.putExtra("subject", subject);
        intent.putExtra("body", message);
        activity.startActivity(intent);
    }

    public static String getCurrentTimeOfDevice() {
        return android.text.format.DateFormat.format("yyyy-MM-ddThh:mm:ss", new java.util.Date()).toString();
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap getResizedBitmapToDisplay(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > height) {
            maxSize = (int) (maxSize * 1.7);
        }

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap decodeBitmapFromFile(String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(EndPoints.SERVER_URL + src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    public static String writeImageToExternalStorage(ByteArrayOutputStream bytes, String name) {
        File destination = new File(Environment.getExternalStorageDirectory() + File.separator
                + "Android/data" + File.separator + AppGlobals.getContext().getPackageName());
        if (!destination.exists()) {
            destination.mkdirs();
        }
        File file = new File(destination, name + ".jpg");
        FileOutputStream fo;
        try {
            file.createNewFile();
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static class RetrieveDocumentsTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(MainActivity.getInstance(), "Retrieving Documents");
        }

        @Override
        protected Void doInBackground(Void... params) {
            bmOne = getBitmapFromURL(docOneMain);
            bmTwo = getBitmapFromURL(docTwoMain);
            bmThree = getBitmapFromURL(docThreeMain);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgressDialog();
            ibPhotoOne.setImageBitmap(getCroppedBitmap(getResizedBitmapToDisplay(bmOne, 120)));
            ibPhotoTwo.setImageBitmap(getCroppedBitmap(getResizedBitmapToDisplay(bmTwo, 120)));
            ibPhotoThree.setImageBitmap(getCroppedBitmap(getResizedBitmapToDisplay(bmThree, 120)));

            llDetails.setVisibility(View.INVISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    llDetails.setVisibility(View.GONE);
                    llDocuments.setVisibility(View.VISIBLE);
                    dialogButtonNo.setText("Back");
                    dialogButtonYes.setVisibility(View.INVISIBLE);
                }
            }, 250);

            dialogDocumentsShown = true;
        }
    }

    public static String getDeviceID() {
        return Settings.Secure.getString(AppGlobals.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static boolean checkPlayServicesAvailability() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getRunningActivityInstance());
        if (resultCode != ConnectionResult.SUCCESS) {
//            Helpers.AlertDialogWithPositiveNegativeFunctions(getRunningActivityInstance(), "PlayServices not found",
//                    "You need to install Google Play Services to continue using Briver", "Install", "Exit App", Helpers.openPlayServicesInstallation, Helpers.exitApp);
            return false;
        } else {
            return true;
        }
    }


    public static boolean hasPermissions(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
