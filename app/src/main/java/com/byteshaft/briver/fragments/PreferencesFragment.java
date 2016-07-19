package com.byteshaft.briver.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.DriverLocationAlarmHelper;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.LocationService;
import com.byteshaft.briver.utils.WebServiceHelpers;
import com.google.android.gms.maps.model.LatLng;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class PreferencesFragment extends android.support.v4.app.Fragment implements CompoundButton.OnCheckedChangeListener {

    public static View baseViewPreferencesFragment;
    public static LatLng latLngDriverLocationFixed;
    public static boolean isPreferencesFragmentOpen;
    public static boolean locationSet = true;
    public static String locationString;
    public static int responseCode;
    static TextView tvPreferencesDriverLocationDisplay;
    static Animation animTexViewFading;
    static int driverPreferencesLocationReportingType = -1;
    LinearLayout llDriverPreferences;
    LinearLayout llCustomerPreferences;
    Switch switchPreferencesDriverServiceStatus;
    RadioGroup rgPreferencesDriverLocation;
    final Runnable openLocationServiceSettings = new Runnable() {
        public void run() {
            rgPreferencesDriverLocation.check(R.id.rb_preferences_driver_location_interval);
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };
    final Runnable dismiss = new Runnable() {
        public void run() {
            rgPreferencesDriverLocation.check(R.id.rb_preferences_driver_location_interval);
        }
    };
    RadioGroup rgPreferencesDriverTransmissionType;
    RadioGroup rgPreferencesCustomerTransmissionType;
    RadioButton rbPreferencesDriverLocationFixed;
    RadioButton rbPreferencesDriverLocationInterval;
    RadioButton rbVehicleTypeMini;
    RadioButton rbVehicleTypeHatchback;
    RadioButton rbVehicleTypeSedan;
    RadioButton rbVehicleTypeLuxury;

    EditText etPreferencesDriverLocationIntervalTime;
    final Runnable handleDriverLocationIntervalInputError = new Runnable() {
        public void run() {
            etPreferencesDriverLocationIntervalTime.setText("1");
        }
    };
    EditText etPreferencesCustomerDriverSearchRadiusInput;
    EditText etPreferencesCustomerVehicleMake;
    EditText etPreferencesCustomerVehicleModel;
    int intLocationIntervalTime;
    int userPreferencesVehicleType = -1;
    LocationService mLocationService;
    final Runnable recheckLocationServiceStatus = new Runnable() {
        public void run() {
            if (Helpers.isAnyLocationServiceAvailable()) {
                mLocationService.startLocationServices();
            } else {
                Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Location Service disabled",
                        "Enable device GPS to continue driver registration", "Settings", "Dismiss", "Re-Check",
                        openLocationServiceSettings, null, recheckLocationServiceStatus);
            }
        }
    };
    int driverStatus;
    String preferencesSearchRadius;
    String preferencesVehicleMake;
    String preferencesVehicleModel;
    String preferencesLocationReportingIntervalTime;
    int transmissionType;
    HttpURLConnection connection;
    EditPreferenceTask taskEditPreference;
    boolean isEditPreferenceTaskRunning;

    public static void setFixedLocationDisplay() {
        locationSet = true;
        locationString = latLngDriverLocationFixed.latitude + "," + latLngDriverLocationFixed.longitude;
        tvPreferencesDriverLocationDisplay.clearAnimation();
        tvPreferencesDriverLocationDisplay.setText("Current location set as fixed location");
        tvPreferencesDriverLocationDisplay.setTextColor(Color.parseColor("#A4C639"));
    }

    public static String getProfileEditStringForCustomer(
            String driver_filter_radius, int transmissionType, int vehicle_type, String vehicle_make, String vehicle_model) {
        return "{" +
                String.format("\"driver_filter_radius\": \"%s\", ", driver_filter_radius) +
                String.format("\"transmission_type\": \"%s\", ", transmissionType) +
                String.format("\"vehicle_type\": \"%s\", ", vehicle_type) +
                String.format("\"vehicle_make\": \"%s\", ", vehicle_make) +
                String.format("\"vehicle_model\": \"%s\"", vehicle_model) +
                "}";
    }

    public static String getProfileEditStringForDriverLocationNull(
            int status, int transmissionType, String location_reporting_type) {
        return "{" +
                String.format("\"status\": \"%s\", ", status) +
                String.format("\"transmission_type\": \"%s\", ", transmissionType) +
                String.format("\"location_reporting_type\": \"%s\"", location_reporting_type) +
                "}";
    }

    public static String getProfileEditStringForDriver(
            int status, int transmissionType, String location_reporting_type, String location_reporting_interval) {
        return "{" +
                String.format("\"status\": \"%s\", ", status) +
                String.format("\"transmission_type\": \"%s\", ", transmissionType) +
                String.format("\"location_reporting_type\": \"%s\", ", location_reporting_type) +
                String.format("\"location_reporting_interval\": \"%s\"", location_reporting_interval) +
                "}";
    }

    public static String getProfileEditStringForDriverWithLocation(
            String location, int status, int transmissionType, String location_reporting_type, String location_reporting_interval) {
        return "{" +
                String.format("\"location\": \"%s\", ", location) +
                String.format("\"status\": \"%s\", ", status) +
                String.format("\"transmission_type\": \"%s\", ", transmissionType) +
                String.format("\"location_reporting_type\": \"%s\", ", location_reporting_type) +
                String.format("\"location_reporting_interval\": \"%s\"", location_reporting_interval) +
                "}";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewPreferencesFragment = inflater.inflate(R.layout.fragment_preferences, container, false);
        mLocationService = new LocationService(getActivity());
        rgPreferencesDriverLocation = (RadioGroup) baseViewPreferencesFragment.findViewById(R.id.rg_preferences_driver_location);
        rgPreferencesDriverTransmissionType = (RadioGroup) baseViewPreferencesFragment.findViewById(R.id.rg_preferences_driver_select_transmission_type);
        rgPreferencesCustomerTransmissionType = (RadioGroup) baseViewPreferencesFragment.findViewById(R.id.rg_preferences_customer_select_transmission_type);
        rbPreferencesDriverLocationFixed = (RadioButton) baseViewPreferencesFragment.findViewById(R.id.rb_preferences_driver_location_fixed);
        rbPreferencesDriverLocationInterval = (RadioButton) baseViewPreferencesFragment.findViewById(R.id.rb_preferences_driver_location_interval);
        switchPreferencesDriverServiceStatus = (Switch) baseViewPreferencesFragment.findViewById(R.id.switch_driver_availability);
        etPreferencesDriverLocationIntervalTime = (EditText) baseViewPreferencesFragment.findViewById(R.id.et_preferences_driver_location_interval_time);
        etPreferencesCustomerDriverSearchRadiusInput = (EditText) baseViewPreferencesFragment.findViewById(R.id.et_preferences_customer_radius_for_driver);
        etPreferencesCustomerVehicleMake = (EditText) baseViewPreferencesFragment.findViewById(R.id.et_preferences_customer_vehicle_make);
        etPreferencesCustomerVehicleModel = (EditText) baseViewPreferencesFragment.findViewById(R.id.et_preferences_customer_vehicle_model);
        tvPreferencesDriverLocationDisplay = (TextView) baseViewPreferencesFragment.findViewById(R.id.tv_preferences_driver_location);
        animTexViewFading = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_text_complete_fading);
        rbVehicleTypeMini = (RadioButton) baseViewPreferencesFragment.findViewById(R.id.rb_preferences_customer_vehicle_type_mini);
        rbVehicleTypeHatchback = (RadioButton) baseViewPreferencesFragment.findViewById(R.id.rb_preferences_customer_vehicle_type_hatchback);
        rbVehicleTypeSedan = (RadioButton) baseViewPreferencesFragment.findViewById(R.id.rb_preferences_customer_vehicle_type_sedan);
        rbVehicleTypeLuxury = (RadioButton) baseViewPreferencesFragment.findViewById(R.id.rb_preferences_customer_vehicle_type_luxury);

        rbVehicleTypeMini.setOnCheckedChangeListener(this);
        rbVehicleTypeHatchback.setOnCheckedChangeListener(this);
        rbVehicleTypeSedan.setOnCheckedChangeListener(this);
        rbVehicleTypeLuxury.setOnCheckedChangeListener(this);

        llCustomerPreferences = (LinearLayout) baseViewPreferencesFragment.findViewById(R.id.layout_preferences_customer);
        llDriverPreferences = (LinearLayout) baseViewPreferencesFragment.findViewById(R.id.layout_preferences_driver);

        transmissionType = AppGlobals.getTransmissionType();

        if (AppGlobals.getUserType() == 0) {
            llCustomerPreferences.setVisibility(View.VISIBLE);
            llDriverPreferences.setVisibility(View.GONE);
            etPreferencesCustomerDriverSearchRadiusInput.setText(String.valueOf(AppGlobals.getDriverSearchRadius()));
            setVehicleTypeRadioButton(AppGlobals.getVehicleType());
            etPreferencesCustomerVehicleMake.setText(AppGlobals.getVehicleMake());
            etPreferencesCustomerVehicleModel.setText(AppGlobals.getVehicleModel());
            if (AppGlobals.getTransmissionType() == 0) {
                rgPreferencesCustomerTransmissionType.check(R.id.rb_preferences_customer_transmission_type_manual);
            } else if (AppGlobals.getTransmissionType() == 1) {
                rgPreferencesCustomerTransmissionType.check(R.id.rb_preferences_customer_transmission_type_auto);
            }
        } else if (AppGlobals.getUserType() == 1) {
            intLocationIntervalTime = AppGlobals.getDriverLocationReportingIntervalTime();
            driverStatus = AppGlobals.getDriverServiceStatus();
            llCustomerPreferences.setVisibility(View.GONE);
            llDriverPreferences.setVisibility(View.VISIBLE);
            if (driverStatus > 0) {
                switchPreferencesDriverServiceStatus.setChecked(true);
            }
            if (AppGlobals.getLocationReportingType() == 0) {
                rgPreferencesDriverLocation.check(R.id.rb_preferences_driver_location_fixed);
                driverPreferencesLocationReportingType = 0;
                tvPreferencesDriverLocationDisplay.setText("Fixed location set");
                tvPreferencesDriverLocationDisplay.setTextColor(Color.parseColor("#ffffff"));
            } else {
                rgPreferencesDriverLocation.check(R.id.rb_preferences_driver_location_interval);
                setIntervalButtonUI();
            }
            if (AppGlobals.getTransmissionType() == 0) {
                rgPreferencesDriverTransmissionType.check(R.id.rb_preferences_driver_transmission_type_manual);
            } else if (AppGlobals.getTransmissionType() == 1) {
                rgPreferencesDriverTransmissionType.check(R.id.rb_preferences_driver_transmission_type_auto);
            } else if (AppGlobals.getTransmissionType() == 2) {
                rgPreferencesDriverTransmissionType.check(R.id.rb_preferences_driver_transmission_type_both);
            }
        }

        switchPreferencesDriverServiceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    driverStatus = 2;
                } else {
                    driverStatus = 0;
                }
            }
        });

        rgPreferencesDriverLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_preferences_driver_location_fixed) {
                    etPreferencesDriverLocationIntervalTime.setVisibility(View.GONE);
                    if (Helpers.isAnyLocationServiceAvailable()) {
                        driverPreferencesLocationReportingType = 0;
                        locationSet = false;
                        mLocationService.startLocationServices();
                        tvPreferencesDriverLocationDisplay.setText("Acquiring Location");
                        tvPreferencesDriverLocationDisplay.setTextColor(Color.parseColor("#ffa500"));
                        tvPreferencesDriverLocationDisplay.startAnimation(animTexViewFading);
                    } else {
                        Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Location Service disabled",
                                "Enable device GPS to set driver's fixed location", "Settings", "Dismiss", "Re-Check",
                                openLocationServiceSettings, dismiss, recheckLocationServiceStatus);
                    }
                } else if (checkedId == R.id.rb_preferences_driver_location_interval) {
                    if (mLocationService.mGoogleApiClient != null && mLocationService.mGoogleApiClient.isConnected()) {
                        mLocationService.stopLocationService();
                    }
                    setIntervalButtonUI();
                }
            }
        });

        rgPreferencesCustomerTransmissionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_preferences_customer_transmission_type_manual) {
                    transmissionType = 0;
                } else if (checkedId == R.id.rb_preferences_customer_transmission_type_auto) {
                    transmissionType = 1;
                }
            }
        });

        rgPreferencesDriverTransmissionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_preferences_driver_transmission_type_manual) {
                    transmissionType = 0;
                } else if (checkedId == R.id.rb_preferences_driver_transmission_type_auto) {
                    transmissionType = 1;
                } else if (checkedId == R.id.rb_preferences_driver_transmission_type_both) {
                    transmissionType = 2;
                }
            }
        });

        etPreferencesDriverLocationIntervalTime.addTextChangedListener(new TextWatcher() {
            Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timer.cancel();
                String etInput = etPreferencesDriverLocationIntervalTime.getText().toString();
                if (etInput.equals("")) {
                    etInput = "0";
                }
                final String finalEtInput = etInput;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        intLocationIntervalTime = Integer.parseInt(finalEtInput);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (intLocationIntervalTime > 0) {
                                    if (intLocationIntervalTime == 1) {
                                        tvPreferencesDriverLocationDisplay.setText("Location will be refreshed every hour");
                                    } else {
                                        tvPreferencesDriverLocationDisplay.setText("Location will be refreshed every " +
                                                intLocationIntervalTime + " hours");
                                    }
                                } else if (intLocationIntervalTime < 1) {
                                    Helpers.AlertDialogMessageWithPositiveFunction(getActivity(), "Wrong Input",
                                            "Interval input cannot be set to less than 1", "Ok",
                                            handleDriverLocationIntervalInputError);
                                    etPreferencesDriverLocationIntervalTime.setText("1");
                                    tvPreferencesDriverLocationDisplay.setText("Location will be refreshed every hour");
                                }
                            }
                        });
                    }
                }, 750);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return baseViewPreferencesFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPreferencesFragmentOpen = true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        rbVehicleTypeMini.setChecked(false);
        rbVehicleTypeHatchback.setChecked(false);
        rbVehicleTypeSedan.setChecked(false);
        rbVehicleTypeLuxury.setChecked(false);
        switch (buttonView.getId()) {
            case R.id.rb_preferences_customer_vehicle_type_mini:
                userPreferencesVehicleType = 0;
                rbVehicleTypeMini.setChecked(isChecked);
                break;
            case R.id.rb_preferences_customer_vehicle_type_hatchback:
                userPreferencesVehicleType = 1;
                rbVehicleTypeHatchback.setChecked(isChecked);
                break;
            case R.id.rb_preferences_customer_vehicle_type_sedan:
                userPreferencesVehicleType = 2;
                rbVehicleTypeSedan.setChecked(isChecked);
                break;
            case R.id.rb_preferences_customer_vehicle_type_luxury:
                userPreferencesVehicleType = 3;
                rbVehicleTypeLuxury.setChecked(isChecked);
                break;
        }
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
                if (AppGlobals.getUserType() == 1) {
                    preferencesLocationReportingIntervalTime = etPreferencesDriverLocationIntervalTime.getText().toString();
                } else {
                    preferencesSearchRadius = etPreferencesCustomerDriverSearchRadiusInput.getText().toString();
                    preferencesVehicleMake = etPreferencesCustomerVehicleMake.getText().toString();
                    preferencesVehicleModel = etPreferencesCustomerVehicleModel.getText().toString();
                }
                if (validateProfileChangeInfo()) {
                    if (AppGlobals.getUserType() == 0) {
                        taskEditPreference = (EditPreferenceTask) new EditPreferenceTask().execute();
                    } else if(driverPreferencesLocationReportingType == 1 || driverPreferencesLocationReportingType == 0 &&
                            tvPreferencesDriverLocationDisplay.getText().toString().equalsIgnoreCase("Fixed location set") ||
                            driverPreferencesLocationReportingType == 0 && tvPreferencesDriverLocationDisplay.getText().toString().equalsIgnoreCase("Current location set as fixed location")) {
                        taskEditPreference = (EditPreferenceTask) new EditPreferenceTask().execute();
                    } else {
                        Helpers.showSnackBar(getView(), "Location is being acquired, please wait",
                                Snackbar.LENGTH_LONG, "#ffffff");
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validateProfileChangeInfo() {
        boolean valid = true;
        if (AppGlobals.getUserType() == 1) {
            if (driverPreferencesLocationReportingType == 1) {
                if (preferencesLocationReportingIntervalTime.trim().isEmpty()) {
                    etPreferencesDriverLocationIntervalTime.setError("Empty");
                    valid = false;
                } else {
                    etPreferencesDriverLocationIntervalTime.setError(null);
                }
            }
        } else {
            if (preferencesSearchRadius.trim().isEmpty()) {
                etPreferencesCustomerDriverSearchRadiusInput.setError("Empty");
                valid = false;
            } else {
                etPreferencesCustomerDriverSearchRadiusInput.setError(null);
            }

            if (preferencesVehicleMake.trim().isEmpty()) {
                etPreferencesCustomerVehicleMake.setError("empty");
                valid = false;
            } else if (preferencesVehicleMake.trim().length() < 3) {
                etPreferencesCustomerVehicleMake.setError("at least 3 characters");
                valid = false;
            } else {
                etPreferencesCustomerVehicleMake.setError(null);
            }

            if (preferencesVehicleModel.trim().isEmpty()) {
                etPreferencesCustomerVehicleModel.setError("empty");
                valid = false;
            } else if (preferencesVehicleModel.trim().length() < 4) {
                etPreferencesCustomerVehicleModel.setError("at least 4 characters");
                valid = false;
            } else {
                etPreferencesCustomerVehicleModel.setError(null);
            }
        }
        return valid;
    }

    private void onEditSuccess(String message) {
        if (AppGlobals.getUserType() == 1) {
            AppGlobals.putDriverServiceStatus(driverStatus);
            AppGlobals.putLocationReportingType(driverPreferencesLocationReportingType);

            if (driverPreferencesLocationReportingType == 1) {
                AppGlobals.putDriverLocationReportingIntervalTime(intLocationIntervalTime);
                DriverLocationAlarmHelper.cancelAlarm();
                DriverLocationAlarmHelper.setAlarm(intLocationIntervalTime);
            } else {
                DriverLocationAlarmHelper.cancelAlarm();
            }
        } else if (AppGlobals.getUserType() == 0) {
            AppGlobals.putDriverSearchRadius(Integer.parseInt(preferencesSearchRadius));
            AppGlobals.putVehicleType(userPreferencesVehicleType);
            AppGlobals.putVehicleMake(preferencesVehicleMake);
            AppGlobals.putVehicleModel(preferencesVehicleModel);
        }
        AppGlobals.putTransmissionType(transmissionType);
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#A4C639");
        Helpers.closeSoftKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    private void onEditFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
    }

    @Override
    public void onPause() {
        super.onPause();
        locationSet = false;
        isPreferencesFragmentOpen = false;
        if (mLocationService.mGoogleApiClient != null && mLocationService.mGoogleApiClient.isConnected()) {
            mLocationService.stopLocationService();
        }
        if (isEditPreferenceTaskRunning) {
            taskEditPreference.cancel(true);
        }
    }

    private void setIntervalButtonUI() {
        driverPreferencesLocationReportingType = 1;
        etPreferencesDriverLocationIntervalTime.setVisibility(View.VISIBLE);
        tvPreferencesDriverLocationDisplay.clearAnimation();
        etPreferencesDriverLocationIntervalTime.setText(String.valueOf(AppGlobals.getDriverLocationReportingIntervalTime()));
        int intervalValue = Integer.parseInt(etPreferencesDriverLocationIntervalTime.getText().toString());
        if (intervalValue > 1) {
            tvPreferencesDriverLocationDisplay.setText("Your location will be updated every " + intervalValue + " hours");
        } else {
            tvPreferencesDriverLocationDisplay.setText("Your location will be updated every " + intervalValue + " hour");
        }
        tvPreferencesDriverLocationDisplay.setTextColor(Color.parseColor("#ffffff"));
    }

    private void setVehicleTypeRadioButton(int rb) {
        if (rb == 0) {
            rbVehicleTypeMini.setChecked(true);
        } else if (rb == 1) {
            rbVehicleTypeHatchback.setChecked(true);
        } else if (rb == 2) {
            rbVehicleTypeSedan.setChecked(true);
        } else if (rb == 3) {
            rbVehicleTypeLuxury.setChecked(true);
        }
    }

    private class EditPreferenceTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isEditPreferenceTaskRunning = true;
            Helpers.showProgressDialog(getActivity(), "Saving Changes");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url;
                if (AppGlobals.getUserType() == 0) {
                    url = EndPoints.BASE_ACCOUNTS_ME;
                } else {
                    url = EndPoints.BASE_ACCOUNTS_ME;
                }
                connection = WebServiceHelpers.openConnectionForUrl(url, "PUT", true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                String editProfileString = "";
                if (AppGlobals.getUserType() == 0) {
                    editProfileString = getProfileEditStringForCustomer(preferencesSearchRadius, transmissionType,
                            userPreferencesVehicleType, preferencesVehicleMake, preferencesVehicleModel);
                } else if (driverPreferencesLocationReportingType == 1) {
                    editProfileString = getProfileEditStringForDriver(driverStatus, transmissionType,
                            String.valueOf(driverPreferencesLocationReportingType),
                            preferencesLocationReportingIntervalTime);
                } else if (driverPreferencesLocationReportingType == 0) {
                    if (!locationSet || latLngDriverLocationFixed == null) {
                        editProfileString = getProfileEditStringForDriverLocationNull(driverStatus, transmissionType,
                                String.valueOf(driverPreferencesLocationReportingType));
                    } else {
                        editProfileString = getProfileEditStringForDriverWithLocation(locationString, driverStatus, transmissionType,
                                String.valueOf(driverPreferencesLocationReportingType),
                                preferencesLocationReportingIntervalTime);
                    }
                }
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
            isEditPreferenceTaskRunning = false;
            Helpers.dismissProgressDialog();
            if (responseCode == 200) {
                onEditSuccess("Preference change successful");
            } else {
                onEditFailed("Preference change failed!");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isEditPreferenceTaskRunning = false;
        }
    }
}
