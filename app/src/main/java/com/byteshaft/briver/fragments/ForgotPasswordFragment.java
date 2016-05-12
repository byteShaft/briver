package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fi8er1 on 29/04/2016.
 */

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    public static int responseCode;
    View baseViewForgotPasswordFragment;
    EditText etForgotPasswordEmail;
    EditText etForgotPasswordConfirmationCode;
    EditText etForgotPasswordNewPassword;
    EditText etForgotPasswordConfirmPassword;
    LinearLayout llForgotPasswordNewPassword;
    Button btnForgotPasswordRecover;
    Button btnForgotPasswordNewSubmit;
    TextView tvForgotPasswordDisplayStatus;
    Animation animTextViewFading;
    String passwordRecoveryEmail;
    String forgotPasswordConfirmationCode;
    String forgotPasswordNewPassword;
    String forgotPasswordNewPasswordRepeat;
    HttpURLConnection connection;

    public static String getRecoveryString(
            String email) {
        return "{" +
                String.format("\"email\": \"%s\"", email) +
                "}";
    }

    public static String getPasswordChangeString(String email,
                                                 String confirmationCode, String password) {
        return "{" +
                String.format("\"email\": \"%s\", ", email) +
                String.format("\"password_reset_key\": \"%s\", ", confirmationCode) +
                String.format("\"new_password\": \"%s\"", password) +
                "}";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewForgotPasswordFragment = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        llForgotPasswordNewPassword = (LinearLayout) baseViewForgotPasswordFragment.findViewById(R.id.ll_forgot_password_new_password_layout);

        etForgotPasswordEmail = (EditText) baseViewForgotPasswordFragment.findViewById(R.id.et_forgot_password_email);
        etForgotPasswordConfirmationCode = (EditText) baseViewForgotPasswordFragment.findViewById(R.id.et_forgot_password_confirmation_code);
        etForgotPasswordNewPassword = (EditText) baseViewForgotPasswordFragment.findViewById(R.id.et_forgot_password_new_password);
        etForgotPasswordConfirmPassword = (EditText) baseViewForgotPasswordFragment.findViewById(R.id.et_forgot_password_new_password_confirm);
        btnForgotPasswordRecover = (Button) baseViewForgotPasswordFragment.findViewById(R.id.btn_forgot_password_recover);
        btnForgotPasswordNewSubmit = (Button) baseViewForgotPasswordFragment.findViewById(R.id.btn_forgot_password_new_submit);
        tvForgotPasswordDisplayStatus = (TextView) baseViewForgotPasswordFragment.findViewById(R.id.tv_forgot_password_status_display);

        animTextViewFading = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_text_complete_fading);
        btnForgotPasswordRecover.setOnClickListener(this);
        btnForgotPasswordNewSubmit.setOnClickListener(this);

        return baseViewForgotPasswordFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_forgot_password_recover:
                passwordRecoveryEmail = etForgotPasswordEmail.getText().toString();
                if (validateRecoverInfo()) {
                    Helpers.closeSoftKeyboard(getActivity());
                    new UserRecoveryTask().execute();
                }
                break;
            case R.id.btn_forgot_password_new_submit:
                forgotPasswordConfirmationCode = etForgotPasswordConfirmationCode.getText().toString();
                forgotPasswordNewPassword = etForgotPasswordNewPassword.getText().toString();
                forgotPasswordNewPasswordRepeat = etForgotPasswordConfirmPassword.getText().toString();

                if (validateSubmitInfo()) {
                    Helpers.closeSoftKeyboard(getActivity());
                    new ChangePasswordTask().execute();
                }
                break;
        }
    }

    public boolean validateRecoverInfo() {
        boolean valid = true;
        if (passwordRecoveryEmail.trim().isEmpty()) {
            etForgotPasswordEmail.setError("Empty");
            valid = false;
        } else if (!passwordRecoveryEmail.trim().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(passwordRecoveryEmail).matches()) {
            etForgotPasswordEmail.setError("Invalid E-Mail");
            valid = false;
        } else {
            etForgotPasswordEmail.setError(null);
        }
        return valid;
    }

    public boolean validateSubmitInfo() {
        boolean valid = true;
        if (forgotPasswordConfirmationCode.trim().isEmpty() || forgotPasswordConfirmationCode.trim().length() < 4) {
            etForgotPasswordConfirmationCode.setError("At least 4 characters");
            valid = false;
        } else {
            etForgotPasswordConfirmationCode.setError(null);
        }

        if (forgotPasswordNewPassword.length() < 6) {
            etForgotPasswordNewPassword.setError("At least 6 characters");
            valid = false;
        } else {
            etForgotPasswordNewPassword.setError(null);
        }

        if (!forgotPasswordNewPassword.equals(forgotPasswordNewPasswordRepeat)) {
            etForgotPasswordConfirmPassword.setError("Password does not match");
            valid = false;
        } else {
            etForgotPasswordConfirmPassword.setError(null);
        }
        return valid;
    }

    public void onRecoverySuccess() {
        Helpers.showSnackBar(getView(), "Enter code and proceed with new password", Snackbar.LENGTH_LONG, "#ffffff");
        btnForgotPasswordRecover.setVisibility(View.GONE);
        llForgotPasswordNewPassword.setVisibility(View.VISIBLE);
        etForgotPasswordEmail.setEnabled(false);
        tvForgotPasswordDisplayStatus.setText("Recovery Successful");
        tvForgotPasswordDisplayStatus.setTextColor(Color.parseColor("#A4C639"));
        tvForgotPasswordDisplayStatus.clearAnimation();
    }

    public void onRecoveryFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
        tvForgotPasswordDisplayStatus.setText("Recovery Failed");
        tvForgotPasswordDisplayStatus.setTextColor(Color.parseColor("#f44336"));
        tvForgotPasswordDisplayStatus.clearAnimation();
    }

    public void onPasswordChangeSuccess() {
        Helpers.showSnackBar(getView(), "Password successfully changed", Snackbar.LENGTH_LONG, "#ffffff");
        llForgotPasswordNewPassword.setVisibility(View.VISIBLE);
        etForgotPasswordEmail.setEnabled(false);
        tvForgotPasswordDisplayStatus.setText("Password Change Successful");
        tvForgotPasswordDisplayStatus.setTextColor(Color.parseColor("#A4C639"));
        tvForgotPasswordDisplayStatus.clearAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().onBackPressed();
            }
        }, 1500);
    }

    public void onPasswordChangeFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
        tvForgotPasswordDisplayStatus.setText(message);
        tvForgotPasswordDisplayStatus.setTextColor(Color.parseColor("#f44336"));
        tvForgotPasswordDisplayStatus.clearAnimation();
    }

    private class UserRecoveryTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvForgotPasswordDisplayStatus.setText("Sending Recovery Mail");
            tvForgotPasswordDisplayStatus.setTextColor(Color.parseColor("#ffa500"));
            tvForgotPasswordDisplayStatus.setVisibility(View.VISIBLE);
            tvForgotPasswordDisplayStatus.startAnimation(animTextViewFading);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                URL url = new URL(EndPoints.RESET_PASSWORD);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                String recoveryString = getRecoveryString(passwordRecoveryEmail);
                out.writeBytes(recoveryString);
                out.flush();
                out.close();
                responseCode = connection.getResponseCode();

                Log.i("Response", "Code " + responseCode);
                Log.i("Response", "Message " + connection.getResponseMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if (responseCode == 200) {
                onRecoverySuccess();
            } else {
                if (responseCode == 404 || responseCode == 400) {
                    onRecoveryFailed("Recovery Failed. User does not exist");
                } else {
                    onRecoveryFailed("Recovery Failed");
                }
            }
        }
    }

    private class ChangePasswordTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvForgotPasswordDisplayStatus.setText("Generating New Password");
            tvForgotPasswordDisplayStatus.setTextColor(Color.parseColor("#ffa500"));
            tvForgotPasswordDisplayStatus.setVisibility(View.VISIBLE);
            tvForgotPasswordDisplayStatus.startAnimation(animTextViewFading);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(EndPoints.CHANGE_PASSWORD);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                String loginString = getPasswordChangeString(passwordRecoveryEmail, forgotPasswordConfirmationCode,
                        forgotPasswordNewPassword);
                Log.i("Login ", "String: " + loginString);
                out.writeBytes(loginString);
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
            if (responseCode == 200) {
                Helpers.dismissProgressDialog();
                onPasswordChangeSuccess();
            } else {
                onPasswordChangeFailed("Password Change Failed");
                Helpers.dismissProgressDialog();
            }
        }
    }

}
