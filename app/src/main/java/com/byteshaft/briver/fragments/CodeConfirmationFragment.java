package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.R;
import com.byteshaft.briver.Tasks.EnablePushNotificationsTask;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.SoftKeyboard;
import com.byteshaft.briver.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fi8er1 on 29/04/2016.
 */

public class CodeConfirmationFragment extends Fragment implements View.OnClickListener {

    public static int responseCode;
    public static boolean isFragmentOpenedFromLogin;
    RelativeLayout layoutCodeConfirmation;
    EditText etCodeConfirmationEmail;
    EditText etCodeConfirmationCode;
    Button btnCodeConfirmationSubmitCode;
    Button btnCodeConfirmationResendCode;
    TextView tvCodeConfirmationStatusDisplay;
    TextView tvCodeConfirmationStatusDisplayTimer;
    final Runnable functionSetTimerTextOnTick = new Runnable() {
        public void run() {
            tvCodeConfirmationStatusDisplayTimer.setText(Helpers.secondsToMinutesSeconds(
                    Helpers.countDownTimerMillisUntilFinished / 1000));
        }
    };
    Animation animTimerFading;
    String confirmationCode;
    String textEmailEntry;
    View baseViewCodeConfirmationFragment;
    HttpURLConnection connection;
    SoftKeyboard mSoftKeyboard;
    boolean isTimerActive;
    final Runnable functionOnTimerFinish = new Runnable() {
        public void run() {
            animTimerFading.cancel();
            isTimerActive = false;
            tvCodeConfirmationStatusDisplayTimer.setVisibility(View.GONE);
        }
    };

    public static String getConfirmationString(
            String email, String activationCode) {
        return "{" +
                String.format("\"email\": \"%s\", ", email) +
                String.format("\"activation_key\": \"%s\"", activationCode) +
                "}";
    }

    public static String getResendString(
            String email) {
        return "{" +
                String.format("\"email\": \"%s\"", email) +
                "}";
    }

    UserConfirmationTask taskUserConfirmation;
    UserResendEmail taskResendEmail;
    boolean isUserConfirmationTaskRunning;
    boolean isResendEmailTaskRunning;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewCodeConfirmationFragment = inflater.inflate(R.layout.fragment_confirmation_code, container, false);

        layoutCodeConfirmation = (RelativeLayout) baseViewCodeConfirmationFragment.findViewById(R.id.layout_code_confirmation);
        etCodeConfirmationEmail = (EditText) baseViewCodeConfirmationFragment.findViewById(R.id.et_confirmation_code_email);
        etCodeConfirmationCode = (EditText) baseViewCodeConfirmationFragment.findViewById(R.id.et_confirmation_code);
        btnCodeConfirmationSubmitCode = (Button) baseViewCodeConfirmationFragment.findViewById(R.id.btn_confirmation_code_submit);
        btnCodeConfirmationResendCode = (Button) baseViewCodeConfirmationFragment.findViewById(R.id.btn_confirmation_code_resend);
        tvCodeConfirmationStatusDisplay = (TextView) baseViewCodeConfirmationFragment.findViewById(R.id.tv_confirmation_code_status_display);
        tvCodeConfirmationStatusDisplayTimer = (TextView) baseViewCodeConfirmationFragment.findViewById(R.id.tv_confirmation_code_resend_timer);
        animTimerFading = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_text_complete_fading);

        btnCodeConfirmationResendCode.setOnClickListener(this);
        btnCodeConfirmationSubmitCode.setOnClickListener(this);

        if (isFragmentOpenedFromLogin) {
            etCodeConfirmationEmail.setText(LoginFragment.sLoginEmail);
            textEmailEntry = LoginFragment.sLoginEmail;
            tvCodeConfirmationStatusDisplay.setText("Activate Account");
            tvCodeConfirmationStatusDisplay.setTextColor(Color.parseColor("#ffffff"));
            return baseViewCodeConfirmationFragment;
        }

        tvCodeConfirmationStatusDisplay.setText("Code Sent - Check Mail");
        tvCodeConfirmationStatusDisplay.setTextColor(Color.parseColor("#A4C639"));

        isTimerActive = true;
        Helpers.setCountDownTimer(120000, 1000, functionSetTimerTextOnTick, functionOnTimerFinish);
        tvCodeConfirmationStatusDisplayTimer.startAnimation(animTimerFading);
        tvCodeConfirmationStatusDisplayTimer.setVisibility(View.VISIBLE);
        etCodeConfirmationEmail.setText(RegisterFragment.userRegisterEmail);


        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        mSoftKeyboard = new SoftKeyboard(layoutCodeConfirmation, im);
        mSoftKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                Helpers.setIsSoftKeyboardOpen(false);
            }

            @Override
            public void onSoftKeyboardShow() {
                Helpers.setIsSoftKeyboardOpen(true);
            }
        });
        return baseViewCodeConfirmationFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirmation_code_submit:
                textEmailEntry = etCodeConfirmationEmail.getText().toString();
                confirmationCode = etCodeConfirmationCode.getText().toString();
                if (validateConfirmationCode()) {
                    taskUserConfirmation = (UserConfirmationTask) new UserConfirmationTask().execute();
                }
                break;
            case R.id.btn_confirmation_code_resend:
                textEmailEntry = etCodeConfirmationEmail.getText().toString();
                if (isTimerActive) {
                    Helpers.showSnackBar(baseViewCodeConfirmationFragment,
                            "Code already sent - Wait for the CountDown",
                            Snackbar.LENGTH_SHORT, "#ffffff");
                } else {
                    taskResendEmail = (UserResendEmail) new UserResendEmail().execute();
                }
                break;
        }
    }

    public boolean validateConfirmationCode() {
        boolean valid = true;
        if (confirmationCode.isEmpty() || confirmationCode.length() < 4) {
            etCodeConfirmationCode.setError("Minimum 4 Characters");
            valid = false;
        } else {
            etCodeConfirmationCode.setError(null);
        }
        return valid;
    }

    public void onConfirmationSuccess() {
        Toast.makeText(getActivity(), "Confirmation successful", Toast.LENGTH_SHORT).show();
        if (Helpers.isIsSoftKeyboardOpen()) {
            Helpers.closeSoftKeyboard(getActivity());
        }
        if (RegisterFragment.registerUserType == 0) {
            AppGlobals.putUserType(0);
        } else {
            AppGlobals.putUserType(1);
        }
        if (!AppGlobals.isPushNotificationsEnabled()) {
            new EnablePushNotificationsTask().execute();
        }
        AppGlobals.setLoggedIn(true);
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));

    }

    public void onConfirmationFailed() {
        Helpers.showSnackBar(getView(), "Confirmation failed, code error", Snackbar.LENGTH_SHORT, "#f44336");
    }

    public void onResendSuccess() {
        Helpers.showSnackBar(getView(), "E-Mail successfully sent", Snackbar.LENGTH_LONG, "#A4C639");
        tvCodeConfirmationStatusDisplay.setText("Code Sent - Check Mail");
        tvCodeConfirmationStatusDisplay.setTextColor(Color.parseColor("#A4C639"));
        tvCodeConfirmationStatusDisplay.clearAnimation();

        isTimerActive = true;
        Helpers.setCountDownTimer(120000, 1000, functionSetTimerTextOnTick, functionOnTimerFinish);
        tvCodeConfirmationStatusDisplayTimer.startAnimation(animTimerFading);
        tvCodeConfirmationStatusDisplayTimer.setVisibility(View.VISIBLE);
        tvCodeConfirmationStatusDisplay.setVisibility(View.VISIBLE);
    }

    public void onResendFailed() {
        Helpers.showSnackBar(getView(), "Failed to resend E-Mail", Snackbar.LENGTH_LONG, "#f44336");
        tvCodeConfirmationStatusDisplay.setText("Resend Failed");
        tvCodeConfirmationStatusDisplay.setTextColor(Color.parseColor("#f44336"));
        tvCodeConfirmationStatusDisplay.clearAnimation();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isUserConfirmationTaskRunning) {
            taskUserConfirmation.cancel(true);
        }
        if (isResendEmailTaskRunning) {
            taskResendEmail.cancel(true);
        }
    }

    private class UserConfirmationTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isUserConfirmationTaskRunning = true;
            btnCodeConfirmationResendCode.setEnabled(false);
            btnCodeConfirmationSubmitCode.setEnabled(false);
            Helpers.showProgressDialog(getActivity(), "Activating User");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url = EndPoints.ACTIVATE_ACCOUNT;
                 connection = WebServiceHelpers.openConnectionForUrl(url, "POST", false);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                String confirmationString = getConfirmationString(textEmailEntry, confirmationCode);
                Log.i("ConfirmationCode", "String: " + confirmationString);
                out.writeBytes(confirmationString);
                out.flush();
                out.close();
                responseCode = connection.getResponseCode();
                JSONObject jsonObject = new JSONObject(WebServiceHelpers.readResponse(connection));
                Log.i("UserConfirmationTask", jsonObject.toString());

                AppGlobals.putToken(jsonObject.getString("token"));
                AppGlobals.putPersonName(jsonObject.getString("full_name"));
                AppGlobals.putUsername(jsonObject.getString("email"));
                AppGlobals.putUserType(jsonObject.getInt("user_type"));
                AppGlobals.putNumberOfHires(jsonObject.getInt("number_of_hires"));
                AppGlobals.putPhoneNumber(jsonObject.getString("phone_number"));
                AppGlobals.putTransmissionType(jsonObject.getInt("transmission_type"));
                if (jsonObject.getInt("user_type") == 0) {
                    AppGlobals.putDriverSearchRadius(jsonObject.getInt("driver_filter_radius"));
                    AppGlobals.putVehicleType(jsonObject.getInt("vehicle_type"));
                    AppGlobals.putVehicleMake(jsonObject.getString("vehicle_make"));
                    AppGlobals.putVehicleModel(jsonObject.getString("vehicle_model"));
                } else if (jsonObject.getInt("user_type") == 1) {
                    AppGlobals.putDrivingExperience(jsonObject.getString("driving_experience"));
                    AppGlobals.putDriverLocationReportingIntervalTime(jsonObject.getInt("location_reporting_interval"));
                    AppGlobals.putLocationReportingType(jsonObject.getInt("location_reporting_type"));
                    AppGlobals.putDocOne(jsonObject.getString("doc1"));
                    AppGlobals.putDocTwo(jsonObject.getString("doc2"));
                    AppGlobals.putDocThree(jsonObject.getString("doc3"));
                    AppGlobals.putDriverBio(jsonObject.getString("bio"));
                }
            } catch (IOException e) {
                e.printStackTrace();
                onConfirmationFailed();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isUserConfirmationTaskRunning = false;
            Helpers.dismissProgressDialog();
            btnCodeConfirmationResendCode.setEnabled(true);
            btnCodeConfirmationSubmitCode.setEnabled(true);
            if (responseCode == 200) {
                onConfirmationSuccess();
            } else {
                onConfirmationFailed();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isUserConfirmationTaskRunning = false;
        }
    }

    private class UserResendEmail extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isResendEmailTaskRunning = true;
            btnCodeConfirmationResendCode.setEnabled(false);
            btnCodeConfirmationSubmitCode.setEnabled(false);
            tvCodeConfirmationStatusDisplay.setText("Resending Email");
            tvCodeConfirmationStatusDisplayTimer.setTextColor(Color.parseColor("#ffa500"));
            tvCodeConfirmationStatusDisplay.startAnimation(animTimerFading);
            tvCodeConfirmationStatusDisplayTimer.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(EndPoints.BASE_URL_USER + "request-activation-key");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                String resendString = getResendString(textEmailEntry);
                out.writeBytes(resendString);
                out.flush();
                out.close();
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
                onConfirmationFailed();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isResendEmailTaskRunning = false;
            btnCodeConfirmationResendCode.setEnabled(true);
            btnCodeConfirmationSubmitCode.setEnabled(true);
            if (responseCode == 200) {
                onResendSuccess();
            } else {
                onResendFailed();
                Helpers.dismissProgressDialog();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isResendEmailTaskRunning = false;
        }
    }

}
