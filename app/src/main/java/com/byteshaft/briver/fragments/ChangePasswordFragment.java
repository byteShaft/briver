package com.byteshaft.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;

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
}
