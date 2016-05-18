package com.byteshaft.briver.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.R;
import com.byteshaft.briver.gcm.QuickstartPreferences;
import com.byteshaft.briver.gcm.RegistrationIntentService;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.SoftKeyboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fi8er1 on 28/04/2016.
 */

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

    boolean isSoftKeyboardOpen;
    boolean launchingMainActivity;

    HttpURLConnection connection;
    public static int responseCode;

    FragmentManager fragmentManager;
    public static BroadcastReceiver mRegistrationBroadcastReceiver;

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
        InputMethodManager im = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);

        fragmentManager = getFragmentManager();

        if (AppGlobals.isLoggedIn()) {
            ivWelcomeLogoMain.startAnimation(animMainLogoFadingInfinite);
            new GetUserDataTask().execute();
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
                isSoftKeyboardOpen = false;
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
                isSoftKeyboardOpen = true;
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
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llWelcomeLogin.setVisibility(View.VISIBLE);
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Helpers.AlertDialogWithPositiveNegativeFunctions(AppGlobals.getRunningActivityInstance(), "Permission Denied",
                            "You need to grant permissions to use Location Services for Briver", "Settings",
                            "Exit App", Helpers.openPermissionsSettingsForMarshmallow, Helpers.exitApp);
                }
                AppGlobals.checkPlayServicesAvailability();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animMainLogoTransitionDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivWelcomeLogoMain.startAnimation(animMainLogoFadingInfinite);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return baseViewLoginFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login:
                sLoginEmail = etLoginEmail.getText().toString();
                sLoginPassword = etLoginPassword.getText().toString();
                if (validateLoginInput()) {
                    if (isSoftKeyboardOpen) {
                        softKeyboard.closeSoftKeyboard();
                    }
                    new UserLoginTask().execute();
                }
                break;
            case R.id.btn_login_register:
                if (isSoftKeyboardOpen) {
                    softKeyboard.closeSoftKeyboard();
                }
                loadRegisterFragment(new RegisterFragment());
                break;
            case R.id.tv_login_forgot_password:
                if (isSoftKeyboardOpen) {
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
        new GetUserDataTask().execute();
    }

    public void onLoginFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
        ivWelcomeLogoMain.startAnimation(animMainLogoTransitionUp);
    }

    public void onGetUserDataSuccess() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
        startActivity(intent);
        AppGlobals.setLoggedIn(true);


        startGcmService();
    }

    public void onGetUserDataFailed() {
        Helpers.showSnackBar(getView(), "Failed to retrieve UserData", Snackbar.LENGTH_LONG, "#f44336");
        if (AppGlobals.isLoggedIn()) {
            Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Retrieving Failed",
                    "UserData cannot be retrieved at the moment", "Retry", "Logout", "Exit App",
                    retryGetUserDataTask, logOut, exitApp);
        }
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
                URL url = new URL(EndPoints.LOGIN);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                String loginString = getLoginString(sLoginEmail, sLoginPassword);
                Log.i("Login ", "String: " + loginString);
                out.writeBytes(loginString);
                out.flush();
                out.close();
                responseCode = connection.getResponseCode();
                Log.i("ResponseCode", " " + responseCode);
                Log.i("ResponseMessage", " " + connection.getResponseMessage());
                InputStream in = (InputStream) connection.getContent();
                int ch;
                StringBuilder sb;

                sb = new StringBuilder();
                while ((ch = in.read()) != -1)
                    sb.append((char) ch);
                JSONObject jsonObject = new JSONObject(sb.toString());
                AppGlobals.putToken(jsonObject.getString("token"));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (responseCode == 200) {
                onLoginSuccess();
            } else {
                if (responseCode == 404) {
                    onLoginFailed("Login Failed! Account not found");
                } else if (responseCode == 403) {
                    onLoginFailed("Login Failed! Account not activated");
                    loadFragment(new CodeConfirmationFragment());
                    CodeConfirmationFragment.isFragmentOpenedFromLogin = true;
                } else {
                    onLoginFailed("Login Failed! Invalid Email or Password");
                }
            }
        }
    }

    public static String getLoginString (
            String email, String password) {
        return "{" +
                String.format("\"username\": \"%s\", ", email) +
                String.format("\"password\": \"%s\"", password) +
                "}";
    }

    private class GetUserDataTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i("getUserDetailTask", " RUN");
                URL url = new URL(EndPoints.BASE_ACCOUNTS + "me");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                Log.i("TOKEN: ", "" + AppGlobals.getToken());
                connection.setRequestProperty("Authorization", "Token " + AppGlobals.getToken());

                InputStream in = (InputStream) connection.getContent();
                Log.i("InputStream", ": " + in);
                int ch;
                StringBuilder sb;
                sb = new StringBuilder();
                while ((ch = in.read()) != -1)
                    sb.append((char) ch);
                JSONObject jsonObject = new JSONObject(sb.toString());
                Log.i("IncomingData", " UserData: " + jsonObject);
                AppGlobals.putPersonName(jsonObject.getString("full_name"));
                AppGlobals.putUsername(jsonObject.getString("email"));
                AppGlobals.putUserType(jsonObject.getInt("user_type"));
                responseCode = connection.getResponseCode();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (responseCode == 200) {
                onGetUserDataSuccess();
            } else {
                onGetUserDataFailed();
                if (!AppGlobals.isLoggedIn()) {
                    onLoginFailed("Login Failed! UserData cannot be retrieved");
                }
            }
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
            new GetUserDataTask().execute();
        }
    };

    private static int getAccountStatus(String userEmail) {
        String request = EndPoints.BASE_ACCOUNTS + "status?email=" + userEmail;
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

        if (AppGlobals.checkPlayServicesAvailability()) {
            Log.i("PlayService", "Available");
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
            getActivity().startService(intent);
        }
    }


    public class EnablePushNotifications extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(getActivity(), "Enabling Push Notifications");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONArray jsonArray = new JSONArray(AppGlobals.getToken());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String ID = jsonObject.getString("id");
                URL url = new URL("http://46.101.75.194:8080/users/" + ID);

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

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Helpers.dismissProgressDialog();
            if (responseCode == 200) {
                Toast.makeText(getActivity(), "Push Notifications Enabled", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                getActivity().finish();
//                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Cannot enable push notifications", Toast.LENGTH_LONG).show();
                AppGlobals.putToken(null);
                AppGlobals.putGcmToken(null);
            }
        }
    }

}
