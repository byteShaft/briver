package com.byteshaft.briver.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class ChangePasswordFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    View baseViewChangePasswordFragment;
    String passwordOld;
    String passwordNew;
    String passwordRepeat;

    EditText etChangePasswordOldPassword;
    EditText etChangePasswordNewPassword;
    EditText etChangePasswordNewPasswordRepeat;

    Button btnChangePassword;


    HttpURLConnection connection;
    public static int responseCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewChangePasswordFragment = inflater.inflate(R.layout.fragmnet_change_password, container, false);

        etChangePasswordOldPassword = (EditText) baseViewChangePasswordFragment.findViewById(R.id.et_change_password_old_password);
        etChangePasswordNewPassword = (EditText) baseViewChangePasswordFragment.findViewById(R.id.et_change_password_new_password);
        etChangePasswordNewPasswordRepeat = (EditText) baseViewChangePasswordFragment.findViewById(R.id.et_change_password_new_password_repeat);

        btnChangePassword = (Button) baseViewChangePasswordFragment.findViewById(R.id.btn_change_password);
        btnChangePassword.setOnClickListener(this);
        return baseViewChangePasswordFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_password:
                changePassword();
                break;
        }
    }

    public void changePassword() {
        if (validatePasswordChangeInfo()) {
//            btnChangePassword.setEnabled(false);
        }
    }

    public void onChangeSuccess() {
        btnChangePassword.setEnabled(true);
    }

    public void onChangeFailed() {
        btnChangePassword.setEnabled(true);
    }

    public boolean validatePasswordChangeInfo() {
        boolean valid = true;

        passwordOld = etChangePasswordOldPassword.getText().toString();
        passwordNew = etChangePasswordNewPassword.getText().toString();
        passwordRepeat = etChangePasswordNewPasswordRepeat.getText().toString();

        if (passwordOld.trim().isEmpty() || passwordOld.length() < 6) {
            etChangePasswordOldPassword.setError("at least 6 characters");
            valid = false;
        } else if (!passwordOld.equals(AppGlobals.getUserPassword())) {
            etChangePasswordOldPassword.setError("current password is invalid");
            valid = false;
        } else {
            etChangePasswordOldPassword.setError(null);
        }

        if (passwordNew.trim().isEmpty() || passwordNew.length() < 6) {
            etChangePasswordNewPassword.setError("at least 6 characters");
            valid = false;
        } else {
            etChangePasswordNewPassword.setError(null);
        }

        if (!passwordNew.equals(passwordRepeat)) {
            etChangePasswordNewPasswordRepeat.setError("password does not match");
            valid = false;
        } else {
            etChangePasswordNewPasswordRepeat.setError(null);
        }
        return valid;
    }



    private class ChangePasswordTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

//                String loginString = getPasswordChangeString(passwordRecoveryEmail, forgotPasswordConfirmationCode,
//                        forgotPasswordNewPassword);
//
//                Log.i("Login ", "String: " + loginString);
//                out.writeBytes(loginString);
//                out.flush();
//                out.close();
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


    public static String getPasswordChangeString(String email,
                                                 String confirmationCode, String password) {
        return "{" +
                String.format("\"email\": \"%s\", ", email) +
                String.format("\"password_reset_key\": \"%s\", ", confirmationCode) +
                String.format("\"new_password\": \"%s\"", password) +
                "}";
    }

    public void onPasswordChangeSuccess() {
        Helpers.showSnackBar(getView(), "Password successfully changed", Snackbar.LENGTH_LONG, "#ffffff");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().onBackPressed();
            }
        }, 2000);
    }

    public void onPasswordChangeFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
    }

}
