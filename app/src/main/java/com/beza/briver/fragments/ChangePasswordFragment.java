package com.beza.briver.fragments;

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

import com.beza.briver.R;
import com.beza.briver.utils.AppGlobals;
import com.beza.briver.utils.EndPoints;
import com.beza.briver.utils.Helpers;
import com.beza.briver.utils.WebServiceHelpers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

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

    ChangePasswordTask taskChangePassword;
    boolean isChangePasswordTaskRunning;

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
            taskChangePassword = (ChangePasswordTask) new ChangePasswordTask().execute();
        }
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
            Helpers.showProgressDialog(getActivity(), "Changing Password");
            isChangePasswordTaskRunning = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url = EndPoints.BASE_ACCOUNTS_ME;
                connection = WebServiceHelpers.openConnectionForUrl(url, "PUT", true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(getPasswordChangeString(passwordNew));
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
            Helpers.dismissProgressDialog();
            isChangePasswordTaskRunning = false;
            if (responseCode == 200) {
                onPasswordChangeSuccess("Password successfully changed");
            } else {
                onPasswordChangeFailed("Password Change Failed");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isChangePasswordTaskRunning = false;
        }
    }

    public static String getPasswordChangeString(String password) {
        return "{" +
                String.format("\"password\": \"%s\"", password) +
                "}";
    }

    public void onPasswordChangeSuccess(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#A4C639");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().onBackPressed();
            }
        }, 1500);
        AppGlobals.putUserPassword(passwordNew);
    }

    public void onPasswordChangeFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isChangePasswordTaskRunning) {
            taskChangePassword.cancel(true);
        }
    }
}