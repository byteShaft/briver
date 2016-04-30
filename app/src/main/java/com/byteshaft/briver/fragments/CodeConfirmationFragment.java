package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.graphics.Color;
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

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.Helpers;

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
    View baseViewCodeConfirmationFragment;

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

        Log.i("BackStack", "Number: " + getFragmentManager().getBackStackEntryCount());

        Helpers.setCountDownTimer(120000, 1000, functionSetTimerTextOnTick, functionOnTimerFinish);
        tvCodeConfirmationStatusDisplayTimer.startAnimation(animTimerFading);

        etCodeConfirmationEmail.setText(RegisterFragment.userRegisterEmail);
        etCodeConfirmationEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("EditText", "GotFocus");
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
}
