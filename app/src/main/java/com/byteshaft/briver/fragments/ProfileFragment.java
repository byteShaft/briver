package com.byteshaft.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.Helpers;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class ProfileFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    View baseViewProfileFragment;

    LinearLayout llProfileDriverElements;

    EditText etProfileEmail;
    EditText etProfileFullName;
    EditText etProfileContactNumber;
    EditText etProfileDrivingExperience;
    EditText etProfileBio;

    String profileFullName;
    String profileContactNumber;
    String profileDrivingExperience;
    String profileBio;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewProfileFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        etProfileEmail = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_email);
        etProfileFullName = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_full_name);
        etProfileContactNumber = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_phone_number);
        etProfileDrivingExperience = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_driving_experience);
        etProfileBio = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_bio);

        llProfileDriverElements = (LinearLayout) baseViewProfileFragment.findViewById(R.id.layout_profile_elements_driver);

        if (AppGlobals.getUserType() == 1) {
            llProfileDriverElements.setVisibility(View.VISIBLE);
        }
        etProfileFullName.setText(AppGlobals.getPeronName());
        etProfileEmail.setText(AppGlobals.getUsername());
        etProfileContactNumber.setText(AppGlobals.getPhoneNumber());
        return baseViewProfileFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_done, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                profileFullName = etProfileFullName.getText().toString();
                profileContactNumber = etProfileContactNumber.getText().toString();

                if (AppGlobals.getUserType() == 1) {
                    profileDrivingExperience = etProfileDrivingExperience.getText().toString();
                    profileBio = etProfileBio.getText().toString();
                }

                validateProfileChangeInfo();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validateProfileChangeInfo() {
        boolean valid = true;

        if (profileFullName.trim().isEmpty()) {
            etProfileFullName.setError("Empty");
            valid = false;
        } else if (profileFullName.trim().length() < 3) {
            etProfileFullName.setError("at least 3 characters");
            valid = false;
        }

        if (profileContactNumber.trim().isEmpty()) {
            etProfileContactNumber.setError("Empty");
            valid = false;
        } else if (!profileContactNumber.isEmpty() && !PhoneNumberUtils.isGlobalPhoneNumber(profileContactNumber)) {
            etProfileContactNumber.setError("Number is invalid");
            valid = false;
        } else {
            etProfileContactNumber.setError(null);
        }

        if (AppGlobals.getUserType() == 1) {
            if (profileDrivingExperience.trim().isEmpty()) {
                etProfileDrivingExperience.setError("Empty");
                valid = false;
            } else {
                etProfileDrivingExperience.setError(null);
            }
        }

        if (profileFullName.equals(AppGlobals.getPeronName()) &&
                profileContactNumber.equals(AppGlobals.getPhoneNumber())) {
            Helpers.showSnackBar(getView(), "No changes to submit", Snackbar.LENGTH_LONG, "#ffffff");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onClick(View v) {

    }
}