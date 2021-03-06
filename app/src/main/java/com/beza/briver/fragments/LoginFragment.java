package com.beza.briver.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beza.briver.MainActivity;
import com.beza.briver.R;
import com.beza.briver.Tasks.EnablePushNotificationsTask;
import com.beza.briver.gcm.QuickstartPreferences;
import com.beza.briver.gcm.RegistrationIntentService;
import com.beza.briver.utils.AppGlobals;
import com.beza.briver.utils.EndPoints;
import com.beza.briver.utils.Helpers;
import com.beza.briver.utils.SoftKeyboard;
import com.beza.briver.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.beza.briver.utils.AppGlobals.getRunningActivityInstance;

public class LoginFragment extends Fragment implements View.OnClickListener {

    ImageView ivWelcomeLogoMain;
    LinearLayout llWelcomeLogin;
    Button btnLogin;
    Button btnRegister;
    TextView tvForgotPassword;
    EditText etLoginEmail;
    EditText etLoginPassword;
    public static String sLoginEmail;
    String sLoginPassword;
    SoftKeyboard softKeyboard;
    View baseViewLoginFragment;

    Animation animMainLogoFading;
    Animation animMainLogoFadingInfinite;
    Animation animMainLogoTransitionUp;
    Animation animMainLogoTransitionDown;
    Animation animMainLogoFadeIn;
    Animation animMainLogoFadeOut;


    public static Runnable callAdmin = new Runnable() {
        public void run() {
            Helpers.initiateCallIntent(getRunningActivityInstance(), "+91-8750875045");
        }
    };
    private static int driverLocationReportingAlarmTime;

    boolean launchingMainActivity;
    boolean loginInterrupted;
    boolean userDataTaskInterrupted;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    HttpURLConnection connection;
    public static int responseCode;
    public static String responseMessage;

    FragmentManager fragmentManager;
    public static BroadcastReceiver mRegistrationBroadcastReceiver;

    UserLoginTask taskUserLogin;
    GetUserDataTask taskGetUserData;
    EnablePushNotificationsTask taskEnablePushNotification;

    boolean isUserLoginTaskRunning;
    boolean isGetUserDataTaskRunning;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseViewLoginFragment = inflater.inflate(R.layout.fragment_login, container, false);

        ivWelcomeLogoMain = (ImageView) baseViewLoginFragment.findViewById(R.id.iv_welcome_logo_main);
        llWelcomeLogin = (LinearLayout) baseViewLoginFragment.findViewById(R.id.ll_welcome_login);
        etLoginEmail = (EditText) baseViewLoginFragment.findViewById(R.id.et_login_email);
        etLoginPassword = (EditText) baseViewLoginFragment.findViewById(R.id.et_login_password);
        btnLogin = (Button) baseViewLoginFragment.findViewById(R.id.btn_login_login);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) baseViewLoginFragment.findViewById(R.id.btn_login_register);
        btnRegister.setOnClickListener(this);
        tvForgotPassword = (TextView) baseViewLoginFragment.findViewById(R.id.tv_login_forgot_password);
        tvForgotPassword.setOnClickListener(this);
        llWelcomeLogin.setVisibility(View.GONE);
        animMainLogoFading = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_partial_fading);
        animMainLogoFadingInfinite = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_partial_fading_infinite);
        animMainLogoTransitionUp = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_transition_up);
        animMainLogoTransitionDown = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_transition_down);
        animMainLogoFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_fade_in);
        animMainLogoFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_fade_out);

        RelativeLayout mainLayout = (RelativeLayout) baseViewLoginFragment.findViewById(R.id.layout_fragment_login);
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);

        fragmentManager = getFragmentManager();

        if (AppGlobals.isLoggedIn()) {
            ivWelcomeLogoMain.startAnimation(animMainLogoFadingInfinite);
            taskGetUserData = (GetUserDataTask) new GetUserDataTask().execute();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivWelcomeLogoMain.startAnimation(animMainLogoTransitionUp);
                }
            }, 350);
        }

        softKeyboard = new SoftKeyboard(mainLayout, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                Helpers.setIsSoftKeyboardOpen(false);
                if (!launchingMainActivity) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivWelcomeLogoMain.startAnimation(animMainLogoFadeIn);
                        }
                    });
                }
            }

            @Override
            public void onSoftKeyboardShow() {
                Helpers.setIsSoftKeyboardOpen(true);
                if (!launchingMainActivity) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivWelcomeLogoMain.startAnimation(animMainLogoFadeOut);
                        }
                    });
                }
            }
        });

        animMainLogoTransitionUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                llWelcomeLogin.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        animMainLogoTransitionDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                ivWelcomeLogoMain.startAnimation(animMainLogoFadingInfinite);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        if (Helpers.isNetworkAvailable(getActivity())) {
            startGcmService();
        }
        return baseViewLoginFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login:
                if (!Helpers.hasPermissions(getActivity(), PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                } else {
                    sLoginEmail = etLoginEmail.getText().toString();
                    sLoginPassword = etLoginPassword.getText().toString();
                    if (validateLoginInput()) {
                        if (Helpers.isIsSoftKeyboardOpen()) {
                            softKeyboard.closeSoftKeyboard();
                        }
                        taskUserLogin = (UserLoginTask) new UserLoginTask().execute();
                    }
                }
                break;
            case R.id.btn_login_register:
                if (!Helpers.hasPermissions(getActivity(), PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                } else {
                    if (Helpers.isIsSoftKeyboardOpen()) {
                        softKeyboard.closeSoftKeyboard();
                    }
                    loadRegisterFragment(new RegisterFragment());
                }
                break;
            case R.id.tv_login_forgot_password:
                if (Helpers.isIsSoftKeyboardOpen()) {
                    softKeyboard.closeSoftKeyboard();
                }
                loadPasswordRecoverFragment(new ForgotPasswordFragment());
                break;
            default:

                break;
        }
    }

    public boolean validateLoginInput() {
        boolean valid = true;
        if (sLoginEmail.trim().isEmpty()) {
            etLoginEmail.setError("Empty");
            valid = false;
        } else if (!sLoginEmail.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(sLoginEmail).matches()) {
            etLoginEmail.setError("Invalid E-Mail");
            valid = false;
        } else {
            etLoginEmail.setError(null);
        }

        if (sLoginPassword.trim().isEmpty() || sLoginPassword.length() < 6) {
            etLoginPassword.setError("Minimum 6 Characters");
            valid = false;
        } else {
            etLoginPassword.setError(null);
        }
        return valid;
    }

    public void onLoginSuccess() {
        Log.i("Login", "Success");
        taskGetUserData = (GetUserDataTask) new GetUserDataTask().execute();
        AppGlobals.putUserPassword(sLoginPassword);
        AppGlobals.setLoggedIn(true);
    }

    public void onLoginFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
        ivWelcomeLogoMain.startAnimation(animMainLogoTransitionUp);
        if (message.equals("Login Failed! Account forbidden")) {
            Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(), "Forbidden",
                    "Contact Briver administration to request approval", "Call", "Cancel",
                    callAdmin);
        }
    }

    public void onGetUserDataSuccess() {
        Log.i("UserDataRetrieval", "Success");
        if (!AppGlobals.isPushNotificationsEnabled()) {
            taskEnablePushNotification = (EnablePushNotificationsTask) new EnablePushNotificationsTask().execute();
        }
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
        startActivity(intent);
    }

    public void onGetUserDataFailed() {
        Log.i("UserDataRetrieval", "Failed");
        Helpers.showSnackBar(getView(), "Failed to retrieve UserData", Snackbar.LENGTH_LONG, "#f44336");
        if (AppGlobals.isLoggedIn()) {
            Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Retrieving Failed",
                    "UserData cannot be retrieved at the moment", "Retry", "Logout", "Exit App",
                    retryGetUserDataTask, logOut, exitApp);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelAllTasks();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }

    public void loadRegisterFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.animator.anim_transition_fragment_slide_right_enter, R.animator.anim_transition_fragment_slide_left_exit,
                R.animator.anim_transition_fragment_slide_left_enter, R.animator.anim_transition_fragment_slide_right_exit);
        tx.replace(R.id.container, fragment).addToBackStack("Register");
        tx.commit();
    }

    public void loadPasswordRecoverFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.animator.anim_transition_fragment_slide_left_enter, R.animator.anim_transition_fragment_slide_right_exit,
                R.animator.anim_transition_fragment_slide_right_enter, R.animator.anim_transition_fragment_slide_left_exit);
        tx.replace(R.id.container, fragment).addToBackStack("Recover");
        tx.commit();
    }

    private class UserLoginTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isUserLoginTaskRunning = true;
            llWelcomeLogin.setVisibility(View.GONE);
            ivWelcomeLogoMain.startAnimation(animMainLogoTransitionDown);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                int accountStatus = getAccountStatus(sLoginEmail);
                if (accountStatus == 404 || accountStatus == 403) {
                    responseCode = accountStatus;
                    return null;
                }
                String url = EndPoints.LOGIN;
                connection = WebServiceHelpers.openConnectionForUrl(url, "POST", false);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                String loginString = getLoginString(sLoginEmail, sLoginPassword);
                out.writeBytes(loginString);
                out.flush();
                out.close();
                responseCode = connection.getResponseCode();
                responseMessage = connection.getResponseMessage();

                Log.i("responseCode", "" + responseCode + " " + responseMessage);

                JSONObject jsonObject = new JSONObject(WebServiceHelpers.readResponse(connection));
                AppGlobals.putToken(jsonObject.getString("token"));


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isUserLoginTaskRunning = false;
            loginInterrupted = false;
            if (responseCode == 200) {
                onLoginSuccess();
            } else {
                if (responseCode == 404) {
                    onLoginFailed("Login Failed! Account not found");
                } else if (responseCode == 403) {
                    if (responseMessage.equalsIgnoreCase("Forbidden")) {
                        onLoginFailed("Login Failed! Account forbidden");
                    } else {
                        onLoginFailed("Login Failed! Account not activated");
                        loadFragment(new CodeConfirmationFragment());
                        CodeConfirmationFragment.isFragmentOpenedFromLogin = true;
                    }
                } else {
                    onLoginFailed("Login Failed! Invalid Email or Password");
                }
            }
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            isUserLoginTaskRunning = false;
        }
    }

    public static String getLoginString (
            String email, String password) {
        return "{" +
                String.format("\"email\": \"%s\", ", email) +
                String.format("\"password\": \"%s\"", password) +
                "}";
    }

    private class GetUserDataTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isGetUserDataTaskRunning = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                connection = WebServiceHelpers.openConnectionForUrl(EndPoints.BASE_ACCOUNTS_ME, "GET", true);
                JSONObject jsonObject = new JSONObject(WebServiceHelpers.readResponse(connection));
                Log.i("IncomingData", " UserData: " + jsonObject);
                Log.i("USERTOKEN", AppGlobals.getToken());
                Log.i("ResponseCode", "" + connection.getResponseCode());
                Log.i("ResponseMessage", connection.getResponseMessage());

                AppGlobals.putPersonName(jsonObject.getString("full_name"));
                AppGlobals.putUsername(jsonObject.getString("email"));
                AppGlobals.putPhoneNumber(jsonObject.getString("phone_number"));
                AppGlobals.putNumberOfHires(jsonObject.getInt("number_of_hires"));
                AppGlobals.putUserType(jsonObject.getInt("user_type"));
                AppGlobals.putUserID(jsonObject.getInt("id"));
                AppGlobals.putRatingCount(jsonObject.getString("review_count"));
                float starsValue = Float.parseFloat(jsonObject.getString("review_stars"));
                AppGlobals.putStarsValue(starsValue);
                AppGlobals.putTransmissionType(jsonObject.getInt("transmission_type"));
                if (jsonObject.getInt("user_type") == 0) {
                    AppGlobals.putDriverSearchRadius(jsonObject.getInt("driver_filter_radius"));
                    AppGlobals.putVehicleType(jsonObject.getInt("vehicle_type"));
                    AppGlobals.putVehicleMake(jsonObject.getString("vehicle_make"));
                    AppGlobals.putVehicleModel(jsonObject.getString("vehicle_model"));
                    Log.i("modelLogin", "" + jsonObject.getString("vehicle_model"));
                    AppGlobals.putVehicleModelYear(jsonObject.getString("vehicle_model_year"));
                } else if (jsonObject.getInt("user_type") == 1) {
                    AppGlobals.putDrivingExperience(jsonObject.getString("driving_experience"));
                    AppGlobals.putDriverLocationReportingIntervalTime(jsonObject.getInt("location_reporting_interval"));
                    AppGlobals.putLocationReportingType(jsonObject.getInt("location_reporting_type"));
                    AppGlobals.putDriverGender(jsonObject.getInt("gender"));
                    AppGlobals.putDocOne(jsonObject.getString("doc1"));
                    AppGlobals.putDocTwo(jsonObject.getString("doc2"));
                    AppGlobals.putDocThree(jsonObject.getString("doc3"));
                    AppGlobals.putDriverBio(jsonObject.getString("bio"));
                }
                responseCode = connection.getResponseCode();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isGetUserDataTaskRunning = false;
            if (responseCode == 200) {
                onGetUserDataSuccess();
            } else {
                onGetUserDataFailed();
                if (!AppGlobals.isLoggedIn()) {
                    onLoginFailed("Login Failed! UserData cannot be retrieved");
                }
            }
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            isGetUserDataTaskRunning = false;
        }
    }

    final Runnable exitApp = new Runnable() {
        public void run() {
            getActivity().finish();
            System.exit(0);
        }
    };

    final Runnable logOut = new Runnable() {
        public void run() {
            AppGlobals.setLoggedIn(false);
            ivWelcomeLogoMain.startAnimation(animMainLogoTransitionUp);
        }
    };

    final Runnable retryGetUserDataTask = new Runnable() {
        public void run() {
            taskGetUserData = (GetUserDataTask) new GetUserDataTask().execute();
        }
    };

    private static int getAccountStatus(String userEmail) {
        String request = EndPoints.BASE_URL_USER + "status?email=" + userEmail;
        int responseCodeStatus = -1;
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            responseCodeStatus = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCodeStatus;
    }

    public void loadFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.animator.anim_transition_fragment_slide_right_enter, R.animator.anim_transition_fragment_slide_left_exit,
                R.animator.anim_transition_fragment_slide_left_enter, R.animator.anim_transition_fragment_slide_right_exit);
        tx.replace(R.id.container, fragment).addToBackStack("Confirmation");
        tx.commit();
    }

    public void startGcmService() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("GCM", "onReceive");
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    System.out.println(R.string.gcm_send_message);
                } else {
                    System.out.println(R.string.token_error_message);
                }
            }
        };
            Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
            getActivity().startService(intent);
    }

    private void cancelAllTasks() {
        if (isUserLoginTaskRunning) {
            taskUserLogin.cancel(true);
            loginInterrupted = true;
        }

        if (isGetUserDataTaskRunning) {
            taskGetUserData.cancel(true);
            userDataTaskInterrupted = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        RegisterFragment.locationAcquired = false;
        if (loginInterrupted) {
            taskUserLogin = (UserLoginTask) new UserLoginTask().execute();
        }
        else if (userDataTaskInterrupted) {
            taskGetUserData = (GetUserDataTask) new GetUserDataTask().execute();
        }
    }
}
