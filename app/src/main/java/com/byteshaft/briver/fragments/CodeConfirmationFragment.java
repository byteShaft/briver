package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fi8er1 on 29/04/2016.
 */

public class CodeConfirmationFragment extends Fragment implements View.OnClickListener {

    RelativeLayout layoutCodeConfirmation;
    EditText etCodeConfirmationEmail;
    final Runnable editConfirmationEmail = new Runnable() {
        public void run() {
            etCodeConfirmationEmail.setFocusable(true);
            etCodeConfirmationEmail.setFocusableInTouchMode(true);
            etCodeConfirmationEmail.requestFocus();
        }
    };
    EditText etCodeConfirmationCode;
    Button btnCodeConfirmationSubmitCode;
    Button btnCodeConfirmationResendCode;
    TextView tvCodeConfirmationStatusDisplay;
    TextView tvCodeConfirmationStatusDisplayTimer;
    final Runnable functionSetTimerTextOnTick = new Runnable() {
        public void run() {
            tvCodeConfirmationStatusDisplayTimer.setText(Helpers.secondsToMinutesSeconds(Helpers.countDownTimerMillisUntilFinished / 1000));
        }
    };
    Animation animTimerFading;
    final Runnable functionOnTimerFinish = new Runnable() {
        public void run() {
            animTimerFading.cancel();
            tvCodeConfirmationStatusDisplay.setVisibility(View.GONE);
            tvCodeConfirmationStatusDisplayTimer.setVisibility(View.GONE);
        }
    };
    String confirmationEmailPreviousEntry;
    String confirmationEmailReEntry;
    String confirmationCode;

    String textEmailEntry;
    View baseViewCodeConfirmationFragment;
    HttpURLConnection connection;
    public static int responseCode;


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

        tvCodeConfirmationStatusDisplay.setText("Code Sent - Check Mail");
        tvCodeConfirmationStatusDisplay.setTextColor(Color.parseColor("#A4C639"));

        Helpers.setCountDownTimer(120000, 1000, functionSetTimerTextOnTick, functionOnTimerFinish);
        tvCodeConfirmationStatusDisplayTimer.startAnimation(animTimerFading);

        etCodeConfirmationEmail.setText(RegisterFragment.userRegisterEmail);
        etCodeConfirmationEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    etCodeConfirmationEmail.setFocusable(false);
                    etCodeConfirmationEmail.setFocusableInTouchMode(false);
                }
            }
        });

        etCodeConfirmationEmail.setOnClickListener(this);
        btnCodeConfirmationResendCode.setOnClickListener(this);
        btnCodeConfirmationSubmitCode.setOnClickListener(this);

        etCodeConfirmationEmail.setFocusable(false);
        confirmationEmailPreviousEntry = RegisterFragment.userRegisterEmail;

        return baseViewCodeConfirmationFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_confirmation_code_email:
                if (!etCodeConfirmationEmail.hasFocus()) {
                    Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(),
                            RegisterFragment.userRegisterEmail, "Want to change?", "Yes", "No", editConfirmationEmail);
                }
                break;
            case R.id.btn_confirmation_code_submit:
                confirmationCode = etCodeConfirmationCode.getText().toString();
                if (validateConfirmationCode()) {
                    new UserConfirmationTask().execute();
                }

                break;
            case R.id.btn_confirmation_code_resend:
                confirmationEmailReEntry = etCodeConfirmationEmail.getText().toString();
                if (validateEmail()) {
                    if (tvCodeConfirmationStatusDisplayTimer.isShown() && confirmationEmailReEntry.equals(confirmationEmailPreviousEntry)) {
                        Helpers.showSnackBar(baseViewCodeConfirmationFragment, "Code already sent - Wait for the CountDown", Snackbar.LENGTH_SHORT, "#ffffff");
                    } else {
                        confirmationEmailPreviousEntry = etCodeConfirmationEmail.getText().toString();
                        Helpers.stopCountDownTimer();
                        etCodeConfirmationEmail.clearFocus();
                        layoutCodeConfirmation.requestFocus();
                        Helpers.setCountDownTimer(120000, 1000, functionSetTimerTextOnTick, functionOnTimerFinish);
                        tvCodeConfirmationStatusDisplayTimer.startAnimation(animTimerFading);
                        tvCodeConfirmationStatusDisplay.setVisibility(View.VISIBLE);
                        tvCodeConfirmationStatusDisplayTimer.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    public boolean validateEmail() {
        boolean valid = true;
        if (confirmationEmailReEntry.trim().isEmpty()) {
            etCodeConfirmationEmail.setError("Empty");
            valid = false;
        } else if (!confirmationEmailReEntry.trim().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(confirmationEmailReEntry).matches()) {
            etCodeConfirmationEmail.setError("Invalid E-Mail");
            valid = false;
        } else {
            etCodeConfirmationEmail.setError(null);
        }
        return valid;
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


    private class UserConfirmationTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(getActivity(), "Registering");
            textEmailEntry = etCodeConfirmationEmail.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(EndPoints.ACTIVATE_ACCOUNT);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                String confirmationString = getConfirmationString(textEmailEntry, confirmationCode);
                Log.i("ConfirmationCode", "String: " + confirmationString);
                out.writeBytes(confirmationString);
                out.flush();
                out.close();
                responseCode = connection.getResponseCode();

                InputStream in = (InputStream) connection.getContent();
                int ch;
                StringBuilder sb;

                sb = new StringBuilder();
                while ((ch = in.read()) != -1)
                    sb.append((char) ch);
            } catch (IOException e) {
                e.printStackTrace();
                onConfirmationFailed();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (responseCode == 200) {
                Helpers.dismissProgressDialog();
                onConfirmationSuccess();
            } else {
                Toast.makeText(getActivity(), "Confirmation Failed", Toast.LENGTH_SHORT).show();
                Helpers.dismissProgressDialog();
            }
        }
    }

    public void onConfirmationSuccess() {
        Toast.makeText(getActivity(), "Confirmation successful", Toast.LENGTH_SHORT).show();
        Helpers.closeSoftKeyboard(getActivity());
        if (RegisterFragment.registerUserType == 0) {
            AppGlobals.putUserType(0);
        } else {
            AppGlobals.putUserType(1);
        }
        AppGlobals.setLoggedIn(true);
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    public void onConfirmationFailed() {
        Toast.makeText(getActivity(), "Confirmation failed, check internet and retry", Toast.LENGTH_SHORT).show();
    }

    public static String getConfirmationString (
            String email, String activationCode) {
        return "{" +
                String.format("\"email\": \"%s\", ", email) +
                String.format("\"activation_key\": \"%s\"", activationCode) +
                "}";
    }

}
