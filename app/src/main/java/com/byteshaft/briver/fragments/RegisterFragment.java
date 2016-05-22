package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.LocationService;
import com.google.android.gms.maps.model.LatLng;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fi8er1 on 28/04/2016.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    EditText etRegisterUserFullName;
    EditText etRegisterUserEmail;
    EditText etRegisterUserEmailRepeat;
    EditText etRegisterUserPassword;
    EditText etRegisterUserConfirmPassword;
    EditText etRegisterUserContactNumber;
    EditText etRegisterUserDrivingExperience;
    EditText etRegisterUserBasicBio;
    EditText etRegisterUserVehicleMake;
    EditText etRegisterUserVehicleModel;

    LinearLayout llRegisterElements;
    LinearLayout llRegisterElementsDriver;
    LinearLayout llRegisterElementsCustomer;

    RadioGroup rgRegisterSelectUserType;
    RadioButton rbRegisterCustomer;
    RadioButton rbRegisterDriver;

    RadioButton rbVehicleTypeMini;
    RadioButton rbVehicleTypeHatchback;
    RadioButton rbVehicleTypeSedan;
    RadioButton rbVehicleTypeLuxury;

    String userRegisterFullName;
    static String userRegisterEmail;
    String userRegisterEmailRepeat;
    String userRegisterPassword;
    String userRegisterConfirmPassword;
    String userRegisterContactNumber;
    String userRegisterDrivingExperience;
    String userRegisterBasicBio;
    String userRegisterVehicleMake;
    String userRegisterVehicleModel;
    static int registerUserType = -1;
    int userRegisterVehicleType = -1;
    public static LatLng latLngDriverLocationForRegistration;
    public static boolean isRegistrationFragmentOpen;
    static String driverLocationToString;

    HttpURLConnection connection;
    public static int responseCode;
    LocationService mLocationService;

    Button btnCreateUser;

    RegisterUserTask taskRegisterUser;
    boolean isUserRegistrationTaskRunning;

    public static View baseViewRegisterFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        baseViewRegisterFragment = inflater.inflate(R.layout.fragment_register, container, false);

        mLocationService = new LocationService(getActivity());

        etRegisterUserFullName = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_full_name);
        etRegisterUserEmail = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_email);
        etRegisterUserEmailRepeat = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_email_repeat);
        etRegisterUserPassword = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_password);
        etRegisterUserConfirmPassword = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_confirm_password);
        etRegisterUserContactNumber = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_phone_number);
        etRegisterUserDrivingExperience = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_driving_experience);
        etRegisterUserBasicBio = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_bio);
        etRegisterUserVehicleMake = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_vehicle_make);
        etRegisterUserVehicleModel = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_vehicle_model);

        baseViewRegisterFragment.findViewById(R.id.ll_register).requestFocus();

        rgRegisterSelectUserType = (RadioGroup) baseViewRegisterFragment.findViewById(R.id.rg_register_select_user_type);

        rbRegisterCustomer = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_customer);
        rbRegisterDriver = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_driver);

        rbVehicleTypeMini = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_customer_vehicle_type_mini);
        rbVehicleTypeHatchback = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_customer_vehicle_type_hatchback);
        rbVehicleTypeSedan = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_customer_vehicle_type_sedan);
        rbVehicleTypeLuxury = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_customer_vehicle_type_luxury);

        rbVehicleTypeMini.setOnCheckedChangeListener(this);
        rbVehicleTypeHatchback.setOnCheckedChangeListener(this);
        rbVehicleTypeSedan.setOnCheckedChangeListener(this);
        rbVehicleTypeLuxury.setOnCheckedChangeListener(this);

        btnCreateUser = (Button) baseViewRegisterFragment.findViewById(R.id.btn_register_create_account);
        btnCreateUser.setOnClickListener(this);

        llRegisterElements = (LinearLayout) baseViewRegisterFragment.findViewById(R.id.layout_elements_register);
        llRegisterElementsCustomer = (LinearLayout) baseViewRegisterFragment.findViewById(R.id.layout_elements_register_customer);
        llRegisterElementsDriver = (LinearLayout) baseViewRegisterFragment.findViewById(R.id.layout_elements_register_driver);

        rgRegisterSelectUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_register_customer) {
                    rbRegisterDriver.setVisibility(View.INVISIBLE);
                    llRegisterElements.setVisibility(View.VISIBLE);
                    llRegisterElementsCustomer.setVisibility(View.VISIBLE);
                    registerUserType = 0;

                    rbVehicleTypeSedan.setChecked(true);
                } else if (checkedId == R.id.rb_register_driver) {
                    rbRegisterCustomer.setVisibility(View.INVISIBLE);
                    llRegisterElements.setVisibility(View.VISIBLE);
                    llRegisterElementsDriver.setVisibility(View.VISIBLE);
                    registerUserType = 1;
                    if (AppGlobals.checkPlayServicesAvailability()) {
                    if (Helpers.isAnyLocationServiceAvailable()) {
                        mLocationService.startLocationServices();
                        Helpers.showSnackBar(baseViewRegisterFragment, "Acquiring location for registration", Snackbar.LENGTH_SHORT, "#ffffff");
                    } else {
                        Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Location Service disabled",
                                "Enable device GPS to continue driver registration", "Settings", "Exit", "Re-Check",
                                openLocationServiceSettings, closeRegistration, recheckLocationServiceStatus);
                    }

                    }
                }
            }
        });
        return baseViewRegisterFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_create_account:

                userRegisterFullName = etRegisterUserFullName.getText().toString();
                userRegisterEmail = etRegisterUserEmail.getText().toString();
                userRegisterEmailRepeat = etRegisterUserEmailRepeat.getText().toString();
                userRegisterPassword = etRegisterUserPassword.getText().toString();
                userRegisterConfirmPassword = etRegisterUserConfirmPassword.getText().toString();
                userRegisterContactNumber = etRegisterUserContactNumber.getText().toString();
                userRegisterDrivingExperience = etRegisterUserDrivingExperience.getText().toString();
                userRegisterBasicBio = etRegisterUserBasicBio.getText().toString();
                userRegisterVehicleMake = etRegisterUserVehicleMake.getText().toString();
                userRegisterVehicleModel = etRegisterUserVehicleModel.getText().toString();

                if (validateRegisterInfo()) {
                    if (registerUserType == 1) {
                    if (latLngDriverLocationForRegistration != null) {
                        driverLocationToString = latLngDriverLocationForRegistration.latitude + "," + latLngDriverLocationForRegistration.longitude;
                        taskRegisterUser = (RegisterUserTask) new RegisterUserTask().execute();
                    } else {
                        driverLocationToString = null;
                        Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(), "Location Unavailable",
                                "Your location is being acquired. You can either wait or it can be acquired later",
                                "Continue Anyway", "Wait", driverRegistrationContinueAnyway);
                    }

                    } else {
                        new RegisterUserTask().execute();
                    }
                }
                break;
        }
    }

    public void loadFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.animator.anim_transition_fragment_slide_right_enter, R.animator.anim_transition_fragment_slide_left_exit,
                R.animator.anim_transition_fragment_slide_left_enter, R.animator.anim_transition_fragment_slide_right_exit);
        tx.replace(R.id.container, fragment).addToBackStack("Confirmation");
        tx.commit();
    }

    public boolean validateRegisterInfo() {
        boolean valid = true;
    
            if (userRegisterFullName.trim().isEmpty()) {
                etRegisterUserFullName.setError("Empty");
                valid = false;
            }
    
            if (userRegisterEmail.trim().isEmpty()) {
                etRegisterUserEmail.setError("Empty");
                valid = false;
            } else if (!userRegisterEmail.trim().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(userRegisterEmail).matches()) {
                etRegisterUserEmail.setError("Invalid E-Mail");
                valid = false;
            } else if (!userRegisterEmail.equals(userRegisterEmailRepeat)) {
                etRegisterUserEmailRepeat.setError("E-Mail doesn't match");
                valid = false;
            } else {
                etRegisterUserEmail.setError(null);
            }
    
            if (userRegisterPassword.trim().isEmpty() || userRegisterPassword.length() < 6) {
                etRegisterUserPassword.setError("Minimum 6 Characters");
                valid = false;
            } else if (!userRegisterPassword.equals(userRegisterConfirmPassword)) {
                etRegisterUserConfirmPassword.setError("Password doesn't match");
               valid = false;
            } else {
                etRegisterUserPassword.setError(null);
            }
            
            if (userRegisterContactNumber.trim().isEmpty()) {
                etRegisterUserContactNumber.setError("Empty");
                valid = false;
            } else if (!userRegisterContactNumber.isEmpty() && !PhoneNumberUtils.isGlobalPhoneNumber(userRegisterContactNumber)) {
                etRegisterUserContactNumber.setError("Number is invalid");
                valid = false;
            } else {
                etRegisterUserContactNumber.setError(null);
            }

        if (registerUserType == 0) {
            if (userRegisterVehicleMake.isEmpty() || userRegisterVehicleMake.length() < 3) {
                etRegisterUserVehicleMake.setError("Minimum 3 Characters");
                valid = false;
            } else {
                etRegisterUserVehicleMake.setError(null);
            }

            if (userRegisterVehicleModel.isEmpty()) {
                etRegisterUserVehicleModel.setError("Empty");
                valid = false;
            } else if (userRegisterVehicleModel.length() < 4) {
                etRegisterUserVehicleModel.setError("Minimum 4 Characters");
                valid = false;
            }
        }
        
        if (registerUserType == 1) {
            if (userRegisterDrivingExperience.trim().isEmpty()) {
                etRegisterUserDrivingExperience.setError("Empty");
                valid = false;
            } else {
                etRegisterUserDrivingExperience.setError(null);
            }
        }

        if (valid && registerUserType == 0 && userRegisterVehicleType == -1) {
            Toast.makeText(getActivity(), "Select Vehicle Type", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    final Runnable closeRegistration = new Runnable() {
        public void run() {
            getActivity().onBackPressed();
        }
    };

    final Runnable openLocationServiceSettings = new Runnable() {
        public void run() {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };


    final Runnable recheckLocationServiceStatus = new Runnable() {
        public void run() {
            if (Helpers.isAnyLocationServiceAvailable()) {
                mLocationService.startLocationServices();
            } else {
                Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Location Service disabled",
                        "Enable device GPS to continue driver registration", "Settings", "Exit", "Re-Check",
                        openLocationServiceSettings, closeRegistration, recheckLocationServiceStatus);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        isRegistrationFragmentOpen = false;
        if (mLocationService.mGoogleApiClient.isConnected()) {
            mLocationService.startLocationUpdates();
        }
        if (isUserRegistrationTaskRunning) {
            taskRegisterUser.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isRegistrationFragmentOpen = true;
        if (rgRegisterSelectUserType.getCheckedRadioButtonId() == R.id.rb_register_driver) {
            if (Helpers.isAnyLocationServiceAvailable()) {
                mLocationService.startLocationServices();
                Helpers.showSnackBar(baseViewRegisterFragment, "Acquiring location for registration", Snackbar.LENGTH_SHORT, "#ffffff");
            } else {
                Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Location Service disabled",
                        "Enable device GPS to continue driver registration", "Settings", "Exit", "Re-Check",
                        openLocationServiceSettings, closeRegistration, recheckLocationServiceStatus);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        rbVehicleTypeMini.setChecked(false);
        rbVehicleTypeHatchback.setChecked(false);
        rbVehicleTypeSedan.setChecked(false);
        rbVehicleTypeLuxury.setChecked(false);
        switch (buttonView.getId()){
            case R.id.rb_register_customer_vehicle_type_mini:
                userRegisterVehicleType = 0;
                rbVehicleTypeMini.setChecked(isChecked);
                break;
            case R.id.rb_register_customer_vehicle_type_hatchback:
                userRegisterVehicleType = 1;
                rbVehicleTypeHatchback.setChecked(isChecked);
                break;
            case R.id.rb_register_customer_vehicle_type_sedan:
                userRegisterVehicleType = 2;
                rbVehicleTypeSedan.setChecked(isChecked);
                break;
            case R.id.rb_register_customer_vehicle_type_luxury:
                userRegisterVehicleType = 3;
                rbVehicleTypeLuxury.setChecked(isChecked);
                break;
            }
    }

    private class RegisterUserTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(getActivity(), "Registering");
        }

        @Override
        protected Void doInBackground(Void... params) {
                try {
                    URL url;
                    if (registerUserType == 0) {
                        url = new URL(EndPoints.REGISTER_CUSTOMER);
                    } else {
                        url = new URL(EndPoints.REGISTER_DRIVER);
                    }
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("charset", "utf-8");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    if (registerUserType == 0) {
                        String dataForRegistration = getRegistrationStringForCustomer(userRegisterFullName,
                                userRegisterEmail, userRegisterPassword, userRegisterContactNumber, userRegisterVehicleType,
                                userRegisterVehicleMake, userRegisterVehicleModel);
                        Log.i("DataToWrite: ", "Customer: " + dataForRegistration);
                        out.writeBytes(dataForRegistration);
                    } else {
                        String dataForRegisteringDriver = getRegistrationStringForDriver(userRegisterFullName,
                                userRegisterEmail, userRegisterPassword, userRegisterContactNumber, userRegisterDrivingExperience,
                                userRegisterBasicBio, driverLocationToString);
                        Log.i("DataToWrite: ", "Driver: " + dataForRegisteringDriver);
                        out.writeBytes(dataForRegisteringDriver);
                    }
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
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Helpers.dismissProgressDialog();
            if (responseCode == 201) {
                onRegistrationSuccess();
            } else if (responseCode == 400) {
                onRegistrationFailed("Registration failed. Email already in use");
            } else {
                onRegistrationFailed("Registration failed. Check internet and retry");
            }
        }
    }

    public void onRegistrationSuccess() {
        Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();

        Helpers.closeSoftKeyboard(getActivity());
        loadFragment(new CodeConfirmationFragment());
        CodeConfirmationFragment.isFragmentOpenedFromLogin = false;
    }

    public void onRegistrationFailed(String message) {
        Helpers.showSnackBar(baseViewRegisterFragment, message, Snackbar.LENGTH_SHORT, "#f44336");
    }

    public static String getRegistrationStringForCustomer(
            String fullName, String email, String password, String phone, int vehicleType,
            String vehicleMake, String vehicleModel) {

        return "{" +
                String.format("\"full_name\": \"%s\", ", fullName) +
                String.format("\"email\": \"%s\", ", email) +
                String.format("\"password\": \"%s\", ", password) +
                String.format("\"phone_number\": \"%s\", ", phone) +
                String.format("\"vehicle_type\": \"%s\", ", vehicleType) +
                String.format("\"vehicle_make\": \"%s\", ", vehicleMake) +
                String.format("\"vehicle_model\": \"%s\"", vehicleModel) +
                "}";
    }

    public static String getRegistrationStringForDriver(
            String fullName, String email, String password, String phone,
            String experience, String bio, String location) {
        StringBuilder output = new StringBuilder();
        output.append("{");
        output.append(String.format("\"full_name\": \"%s\", ", fullName));
        output.append(String.format("\"email\": \"%s\", ", email));
        output.append(String.format("\"password\": \"%s\", ", password));
        output.append(String.format("\"phone_number\": \"%s\", ", phone));
        output.append(String.format("\"driving_experience\": \"%s\", ", experience));
        if (bio != null) {
            output.append(String.format("\"bio\": \"%s\", ", bio));
        }

        if (location != null) {
            output.append(String.format("\"location\": \"%s\"", location));
        }

        output.append("}");
        return output.toString();
    }

    final Runnable driverRegistrationContinueAnyway = new Runnable() {
        public void run() {
            new RegisterUserTask().execute();
        }
    };


}
