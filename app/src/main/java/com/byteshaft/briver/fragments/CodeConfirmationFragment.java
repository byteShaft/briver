package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.Helpers;

/**
 * Created by fi8er1 on 29/04/2016.
 */
public class CodeConfirmationFragment extends Fragment implements View.OnClickListener{

    EditText etCodeConfirmationEmail;
    Button btnCodeConfirmationSubmitCode;
    Button btnCodeConfirmationResendCode;
    TextView tvCodeConfirmationStatusDisplay;
    TextView tvCodeConfirmationStatusDisplayTimer;

    Animation animTimerFading;

    View baseViewCodeConfirmationFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewCodeConfirmationFragment = inflater.inflate(R.layout.fragment_confirmation_code, container, false);
        
        etCodeConfirmationEmail = (EditText) baseViewCodeConfirmationFragment.findViewById(R.id.et_confirmation_code_email);
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
                    etCodeConfirmationEmail.setEnabled(false);
                    etCodeConfirmationEmail.setClickable(true);
                }
            }
        });
        
        etCodeConfirmationEmail.setOnClickListener(this);
        btnCodeConfirmationResendCode.setOnClickListener(this);
        btnCodeConfirmationSubmitCode.setOnClickListener(this);

        return baseViewCodeConfirmationFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_confirmation_code_email:
                Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(),
                        RegisterFragment.userRegisterEmail, "Want to change?", "Yes", "No", editConfirmationEmail);
                break;
            case R.id.btn_confirmation_code_submit:

                break;
            case R.id.btn_confirmation_code_resend:
                if (!animTimerFading.hasEnded()) {
                    Toast.makeText(getActivity(), "Wait for the CountDown", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    final Runnable editConfirmationEmail = new Runnable() {
        public void run() {
            etCodeConfirmationEmail.setEnabled(true);
            etCodeConfirmationEmail.setClickable(false);
            etCodeConfirmationEmail.requestFocus();
        }
    };

    final Runnable functionSetTimerTextOnTick = new Runnable() {
        public void run() {
            tvCodeConfirmationStatusDisplayTimer.setText(Helpers.secondsToMinutesSeconds(Helpers.countDownTimerMillisUntilFinished / 1000));
        }
    };

    final Runnable functionOnTimerFinish = new Runnable() {
        public void run() {
            animTimerFading.cancel();
            tvCodeConfirmationStatusDisplay.setVisibility(View.GONE);
            tvCodeConfirmationStatusDisplayTimer.setVisibility(View.GONE);
        }
    };
}
