package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.byteshaft.briver.R;

/**
 * Created by fi8er1 on 29/04/2016.
 */

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    View baseViewForgotPasswordFragment;

    EditText etForgotPasswordEmail;
    Button btnForgotPasswordRecover;
    TextView tvForgotPasswordDisplayStatus;

    String passwordRecoveryEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        baseViewForgotPasswordFragment = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        etForgotPasswordEmail = (EditText) baseViewForgotPasswordFragment.findViewById(R.id.et_forgot_password_email);
        btnForgotPasswordRecover = (Button) baseViewForgotPasswordFragment.findViewById(R.id.btn_forgot_password_recover);
        tvForgotPasswordDisplayStatus = (TextView) baseViewForgotPasswordFragment.findViewById(R.id.tv_forgot_password_status_display);

        btnForgotPasswordRecover.setOnClickListener(this);

        return baseViewForgotPasswordFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_forgot_password_recover:
                passwordRecoveryEmail = etForgotPasswordEmail.getText().toString();
                if (validateRecoverInfo()) {

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

}
