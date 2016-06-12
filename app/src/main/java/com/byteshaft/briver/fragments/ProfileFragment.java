package com.byteshaft.briver.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.WebServiceHelpers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

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

    HttpURLConnection connection;
    public static int responseCode;

    EditProfileTask taskEditProfile;
    boolean isEditProfileTaskRunning;

    RatingBar ratingBarHome;
    TextView tvRatingBarHome;

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

        ratingBarHome = (RatingBar) baseViewProfileFragment.findViewById(R.id.rBar_profile);
        tvRatingBarHome = (TextView) baseViewProfileFragment.findViewById(R.id.tv_rBar_profile);

        float starsValue = AppGlobals.getStarsValue();

        if (starsValue > 0.0) {
            ratingBarHome.setRating(starsValue);
        } else {
            ratingBarHome.setRating((float) 0.0);
        }
        tvRatingBarHome.setText("(" + AppGlobals.getRatingCount() + ")");

        if (AppGlobals.getUserType() == 1) {
            llProfileDriverElements.setVisibility(View.VISIBLE);
            etProfileDrivingExperience.setText(AppGlobals.getDrivingExperience());
            etProfileBio.setText(AppGlobals.getDriverBio());
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
                if (validateProfileChangeInfo()) {
                    taskEditProfile = (EditProfileTask) new EditProfileTask().execute();
                }
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
            if (profileFullName.equals(AppGlobals.getPeronName()) &&
                    profileContactNumber.equals(AppGlobals.getPhoneNumber()) &&
                    profileDrivingExperience.equals(AppGlobals.getDrivingExperience())
                    && profileBio.equals(AppGlobals.getDriverBio())) {
                Helpers.showSnackBar(getView(), "No changes to submit", Snackbar.LENGTH_LONG, "#ffffff");
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public void onClick(View v) {

    }

    private void onEditSuccess(String message) {
        AppGlobals.putPersonName(profileFullName);
        AppGlobals.putPhoneNumber(profileContactNumber);
        if (AppGlobals.getUserType() == 1) {
            AppGlobals.putDrivingExperience(profileDrivingExperience);
            if (!profileBio.trim().isEmpty()) {
                AppGlobals.putDriverBio(profileBio);
            }
        }
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#A4C639");
        MainActivity.tvPersonName.setText(AppGlobals.getPeronName());
        Helpers.closeSoftKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    private void onEditFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
    }

    private class EditProfileTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isEditProfileTaskRunning = true;
            Helpers.showProgressDialog(getActivity(), "Saving Changes");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url;
                if (AppGlobals.getUserType() == 0) {
                    url = EndPoints.SHOW_CUSTOMERS + AppGlobals.getUserID();
                } else {
                    url = EndPoints.SHOW_DRIVERS + AppGlobals.getUserID();
                }
                connection = WebServiceHelpers.openConnectionForUrl(url, "PATCH", true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                String editProfileString;
                if (AppGlobals.getUserType() == 0) {
                   editProfileString = getProfileEditStringForCustomer(profileFullName, profileContactNumber);
                } else {
                    editProfileString = getProfileEditStringForDriver(profileFullName, profileContactNumber,
                            profileDrivingExperience, profileBio);
                }
                Log.i("Login ", "String: " + editProfileString);
                out.writeBytes(editProfileString);
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
            isEditProfileTaskRunning = false;
            Helpers.dismissProgressDialog();
            if (responseCode == 200) {
                onEditSuccess("Details change successful");
            } else {
                onEditFailed("Details change failed!");
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isEditProfileTaskRunning = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isEditProfileTaskRunning) {
            taskEditProfile.cancel(true);
        }
    }

    public static String getProfileEditStringForCustomer (
            String full_name, String phone_number) {
        return "{" +
                String.format("\"full_name\": \"%s\", ", full_name) +
                String.format("\"phone_number\": \"%s\"", phone_number) +
                "}";
    }

    public static String getProfileEditStringForDriver (
            String full_name, String phone_number, String driving_experience, String bio) {
        return "{" +
                String.format("\"full_name\": \"%s\", ", full_name) +
                String.format("\"phone_number\": \"%s\", ", phone_number) +
                String.format("\"driving_experience\": \"%s\", ", driving_experience) +
                String.format("\"bio\": \"%s\"", bio) +
                "}";
    }
}