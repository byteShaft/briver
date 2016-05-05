package com.byteshaft.briver.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.DriverService;
import com.byteshaft.briver.utils.Helpers;
import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class PreferencesFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    public static View baseViewPreferencesFragment;

    Switch switchPreferencesDriverServiceStatus;

    RadioGroup rgPreferencesDriverLocation;
    RadioButton rbPreferencesDriverLocationFixed;
    RadioButton rbPreferencesDriverLocationInterval;

    EditText etPreferencesDriverLocationIntervalTime;
    final Runnable handleDriverLocationIntervalInputError = new Runnable() {
        public void run() {
            etPreferencesDriverLocationIntervalTime.setText("1");
        }
    };
    EditText etPreferencesCustomerDriverSearchRadiusInput;
    Button btnPreferencesDriverLocationGetFixedLocation;
    static TextView tvPreferencesDriverLocationDisplay;
    int intLocationIntervalTime;

    static Animation animTexViewFading;

    public static LatLng latLngDriverLocationFixed;
    public static boolean isPreferencesFragmentOpen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewPreferencesFragment = inflater.inflate(R.layout.fragment_preferences, container, false);

        rgPreferencesDriverLocation = (RadioGroup) baseViewPreferencesFragment.findViewById(R.id.rg_preferences_driver_location);
        rbPreferencesDriverLocationFixed = (RadioButton) baseViewPreferencesFragment.findViewById(R.id.rb_preferences_driver_location_fixed);
        rbPreferencesDriverLocationInterval = (RadioButton) baseViewPreferencesFragment.findViewById(R.id.rb_preferences_driver_location_interval);

        switchPreferencesDriverServiceStatus = (Switch) baseViewPreferencesFragment.findViewById(R.id.switch_driver_availability);
        btnPreferencesDriverLocationGetFixedLocation = (Button) baseViewPreferencesFragment.findViewById(R.id.btn_preferences_driver_location_fixed_set);

        etPreferencesDriverLocationIntervalTime = (EditText) baseViewPreferencesFragment.findViewById(R.id.et_preferences_driver_location_interval_time);
        etPreferencesCustomerDriverSearchRadiusInput = (EditText) baseViewPreferencesFragment.findViewById(R.id.et_preferences_customer_radius_for_driver);

        btnPreferencesDriverLocationGetFixedLocation = (Button) baseViewPreferencesFragment.findViewById(R.id.btn_preferences_driver_location_fixed_set);
        tvPreferencesDriverLocationDisplay = (TextView) baseViewPreferencesFragment.findViewById(R.id.tv_preferences_driver_location);
        animTexViewFading = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_text_complete_fading);
        btnPreferencesDriverLocationGetFixedLocation.setOnClickListener(this);

        switchPreferencesDriverServiceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                } else {

                }
            }
        });

        rgPreferencesDriverLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_preferences_driver_location_fixed) {
                    etPreferencesDriverLocationIntervalTime.setVisibility(View.GONE);
                    btnPreferencesDriverLocationGetFixedLocation.setVisibility(View.VISIBLE);
                    tvPreferencesDriverLocationDisplay.setText("Your location will remain fixed on the map for the customer");
                    tvPreferencesDriverLocationDisplay.setTextColor(Color.parseColor("#ffffff"));
                } else if (checkedId == R.id.rb_preferences_driver_location_interval) {
                    etPreferencesDriverLocationIntervalTime.setVisibility(View.VISIBLE);
                    btnPreferencesDriverLocationGetFixedLocation.setVisibility(View.GONE);
                    tvPreferencesDriverLocationDisplay.setText("Your location will be refreshed on set interval");
                    tvPreferencesDriverLocationDisplay.setTextColor(Color.parseColor("#ffffff"));
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
                }, 1500);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return baseViewPreferencesFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_preferences_driver_location_fixed_set:
                if (Helpers.isAnyLocationServiceAvailable()) {
                    getActivity().startService(new Intent(getActivity(), DriverService.class));
                    tvPreferencesDriverLocationDisplay.setText("Acquiring Location");
                    tvPreferencesDriverLocationDisplay.setTextColor(Color.parseColor("#ffa500"));
                    tvPreferencesDriverLocationDisplay.startAnimation(animTexViewFading);
                } else {
                    Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Location Service disabled",
                            "Enable device GPS to continue driver registration", "Settings", "Exit", "Re-Check",
                            openLocationServiceSettings, closeRegistration, recheckLocationServiceStatus);
                }

                break;
        }
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
                getActivity().startService(new Intent(getActivity(), DriverService.class));
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
        isPreferencesFragmentOpen = false;
        if (DriverService.driverLocationReportingServiceIsRunning) {
            getActivity().stopService(new Intent(getActivity(), DriverService.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isPreferencesFragmentOpen = true;
    }

    public static void setFixedLocationDisplay() {
        tvPreferencesDriverLocationDisplay.clearAnimation();
        tvPreferencesDriverLocationDisplay.setText("Current location set as fixed location");
        tvPreferencesDriverLocationDisplay.setTextColor(Color.parseColor("#A4C639"));
    }
}
