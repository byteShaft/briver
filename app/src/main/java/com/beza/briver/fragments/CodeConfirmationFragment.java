package com.beza.briver.fragments;

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

import com.beza.briver.MainActivity;
import com.beza.briver.R;
import com.beza.briver.Tasks.EnablePushNotificationsTask;
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

import static com.beza.briver.fragments.LoginFragment.responseMessage;

/**
 * Created by fi8er1 on 29/04/2016.
 */

public class CodeConfirmationFragment extends Fragment implements View.OnClickListener {

    public static int responseCode;
    public static boolean isFragmentOpenedFromLogin;
    static boolean isCodeConfirmationFragmentOpen;
    RelativeLayout layoutCodeConfirmation;
    EditText etCodeConfirmationEmail;
    EditText etCodeConfirmationEmailOtp;
    static EditText etCodeConfirmationSmsOtp;
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
    String confirmationOtpEmail;
    String confirmationOtpSms;
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
            String email, String emailOtp, String smsOtp) {
        return "{" +
                String.format("\"email\": \"%s\", ", email) +
                String.format("\"email_otp\": \"%s\", ", emailOtp) +
                String.format("\"sms_otp\": \"%s\"", smsOtp) +
                "}";
    }

    public static String getResendString(
            String email) {
        return "{" +
                String.format("\"email\": \"%s\"", email) +
                "}";
    }

    UserConfirmationTask taskUserConfirmation;
    UserResendOTP taskResendEmail;
    public static String otp;
    boolean isUserConfirmationTaskRunning;
    boolean isResendEmailTaskRunning;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewCodeConfirmationFragment = inflater.inflate(R.layout.fragment_confirmation_code, container, false);

        layoutCodeConfirmation = (RelativeLayout) baseViewCodeConfirmationFragment.findViewById(R.id.layout_code_confirmation);
        etCodeConfirmationEmail = (EditText) baseViewCodeConfirmationFragment.findViewById(R.id.et_confirmation_code_email);
        etCodeConfirmationEmailOtp = (EditText) baseViewCodeConfirmationFragment.findViewById(R.id.et_confirmation_code_email_otp);
        etCodeConfirmationSmsOtp = (EditText) baseViewCodeConfirmationFragment.findViewById(R.id.et_confirmation_code_sms_otp);
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

        tvCodeConfirmationStatusDisplay.setText("OTP Sent - Check Inbox");
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
                confirmationOtpEmail = etCodeConfirmationEmailOtp.getText().toString();
                confirmationOtpSms = etCodeConfirmationSmsOtp.getText().toString();
                if (validateConfirmationCode()) {
                    taskUserConfirmation = (UserConfirmationTask) new UserConfirmationTask().execute();
                }
                break;
            case R.id.btn_confirmation_code_resend:
                textEmailEntry = etCodeConfirmationEmail.getText().toString();
                if (isTimerActive) {
                    Helpers.showSnackBar(baseViewCodeConfirmationFragment,
                            "OTP already sent - Wait for the Countdown",
                            Snackbar.LENGTH_SHORT, "#ffffff");
                } else {
                    taskResendEmail = (UserResendOTP) new UserResendOTP().execute();
                }
                break;
        }
    }

    public boolean validateConfirmationCode() {
        boolean valid = true;
        if (confirmationOtpEmail.isEmpty() || confirmationOtpEmail.length() < 4) {
            etCodeConfirmationEmailOtp.setError("Minimum 4 Characters");
            valid = false;
        } else {
            etCodeConfirmationEmailOtp.setError(null);
        }

        if (confirmationOtpSms.isEmpty() || confirmationOtpSms.length() < 4) {
            etCodeConfirmationSmsOtp.setError("Minimum 4 Characters");
            valid = false;
        } else {
            etCodeConfirmationSmsOtp.setError(null);
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
        otp = null;
    }

    public void onConfirmationFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_SHORT, "#f44336");
    }

    public void onResendSuccess() {
        Helpers.showSnackBar(getView(), "OTP successfully sent", Snackbar.LENGTH_LONG, "#A4C639");
        tvCodeConfirmationStatusDisplay.setText("OTP Sent - Check Inbox");
        tvCodeConfirmationStatusDisplay.setTextColor(Color.parseColor("#A4C639"));
        tvCodeConfirmationStatusDisplay.clearAnimation();

        isTimerActive = true;
        Helpers.setCountDownTimer(120000, 1000, functionSetTimerTextOnTick, functionOnTimerFinish);
        tvCodeConfirmationStatusDisplayTimer.startAnimation(animTimerFading);
        tvCodeConfirmationStatusDisplayTimer.setVisibility(View.VISIBLE);
        tvCodeConfirmationStatusDisplay.setVisibility(View.VISIBLE);
        otp = null;
    }

    public void onResendFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
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
        isCodeConfirmationFragmentOpen = false;
        if (isUserConfirmationTaskRunning) {
            taskUserConfirmation.cancel(true);
        }
        if (isResendEmailTaskRunning) {
            taskResendEmail.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isCodeConfirmationFragmentOpen = true;
        otpReceived();
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

                String confirmationString = getConfirmationString(textEmailEntry, confirmationOtpEmail, confirmationOtpSms);
                Log.i("ConfirmationCode", "String: " + confirmationString);
                out.writeBytes(confirmationString);
                out.flush();
                out.close();
                responseCode = connection.getResponseCode();
                responseMessage = connection.getResponseMessage();
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
            } catch (IOException e) {
                e.printStackTrace();
                onConfirmationFailed("Confirmation failed, code error");
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
            } else if (responseCode == 403) {
                if (responseMessage.equalsIgnoreCase("user deactivated by admin.")) {
                    onConfirmationFailed("Login Failed! User banned by admin");
                } else {
                    onConfirmationFailed("Confirmation failed, code error");
                }
            } else {
                onConfirmationFailed("Confirmation failed, code error");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isUserConfirmationTaskRunning = false;
        }
    }

    private class UserResendOTP extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isResendEmailTaskRunning = true;
            btnCodeConfirmationResendCode.setEnabled(false);
            btnCodeConfirmationSubmitCode.setEnabled(false);
            tvCodeConfirmationStatusDisplay.setText("Resending OTP");
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
                responseMessage = connection.getResponseMessage();
            } catch (IOException e) {
                e.printStackTrace();
                onConfirmationFailed("Confirmation failed, code error");
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
            } else if (responseCode == 403) {
                if (responseMessage.equalsIgnoreCase("user deactivated by admin.")) {
                    onResendFailed("User banned by the admin");
                } else {
                    onResendFailed("Failed to resend OTP");
                }
            } else {
                onResendFailed("Failed to resend OTP");
                Helpers.dismissProgressDialog();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isResendEmailTaskRunning = false;
        }
    }

    public static void otpReceived() {
        if (otp != null && isCodeConfirmationFragmentOpen) {
            etCodeConfirmationSmsOtp.setText(otp);
        }
    }

}
