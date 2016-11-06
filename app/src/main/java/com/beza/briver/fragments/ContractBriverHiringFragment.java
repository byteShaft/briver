package com.beza.briver.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.beza.briver.R;

/**
 * Created by fi8er1 on 02/11/2016.
 */

public class ContractBriverHiringFragment extends android.support.v4.app.Fragment {

    View baseViewContractBriverHiringFragment;

    Button btnSendEmail;
    RadioGroup rgContractBriverHiringFragmentGender;
    RadioButton rbContractBriverHiringFragmentMale;
    RadioButton rbContractBriverHiringFragmentFemale;

    EditText etContractBriverHiringFragmentName;
    EditText etContractBriverHiringFragmentAddress;
    EditText etContractBriverHiringFragmentWorkingHoursPerDay;
    EditText etContractBriverHiringFragmentWorkingDaysInAWeek;
    EditText etContractBriverHiringFragmentSalaryRange;
    EditText etContractBriverHiringFragmentLeavesInAMonth;
    EditText etContractBriverHiringFragmentSpecialPreferences;

    LinearLayout llContractBriverHiringFragmentWorkingHoursPerDay;

    String name, address, workingHoursPerDay, workingDaysInAWeek, salaryRange, leavesInAMonth, specialPreferences;

    CheckBox cbContractBriverHiringFragmentLiveInBoolean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewContractBriverHiringFragment = inflater.inflate(R.layout.fragment_contract_briver_hiring, container, false);

        btnSendEmail = (Button) baseViewContractBriverHiringFragment.findViewById(R.id.btn_fragment_contract_send_email);

        llContractBriverHiringFragmentWorkingHoursPerDay = (LinearLayout) baseViewContractBriverHiringFragment.findViewById(R.id.ll_fragment_contract_working_hours_a_day);

        rgContractBriverHiringFragmentGender = (RadioGroup) baseViewContractBriverHiringFragment.findViewById(R.id.rg_fragment_contract_briver_type);
        rbContractBriverHiringFragmentMale = (RadioButton) baseViewContractBriverHiringFragment.findViewById(R.id.rb_fragment_contract_briver_type_male);
        rbContractBriverHiringFragmentFemale = (RadioButton) baseViewContractBriverHiringFragment.findViewById(R.id.rb_fragment_contract_briver_type_female);

        etContractBriverHiringFragmentName = (EditText) baseViewContractBriverHiringFragment.findViewById(R.id.et_fragment_contract_name);
        etContractBriverHiringFragmentAddress = (EditText) baseViewContractBriverHiringFragment.findViewById(R.id.et_fragment_contract_address);
        etContractBriverHiringFragmentWorkingHoursPerDay = (EditText) baseViewContractBriverHiringFragment.findViewById(R.id.et_fragment_contract_working_hours_per_day);
        etContractBriverHiringFragmentWorkingDaysInAWeek = (EditText) baseViewContractBriverHiringFragment.findViewById(R.id.et_fragment_contract_working_days_per_week);
        etContractBriverHiringFragmentSalaryRange = (EditText) baseViewContractBriverHiringFragment.findViewById(R.id.et_fragment_contract_salary_range_you_are_willing_to_offer);
        etContractBriverHiringFragmentLeavesInAMonth = (EditText) baseViewContractBriverHiringFragment.findViewById(R.id.et_fragment_contract_leaves_you_are_willing_to_offer_in_a_month);
        etContractBriverHiringFragmentSpecialPreferences = (EditText) baseViewContractBriverHiringFragment.findViewById(R.id.et_fragment_contract_special_preferences);

        cbContractBriverHiringFragmentLiveInBoolean = (CheckBox) baseViewContractBriverHiringFragment.findViewById(R.id.cb_fragment_contract_live_in_driver);

        rbContractBriverHiringFragmentMale.setChecked(true);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = etContractBriverHiringFragmentName.getText().toString();
                address = etContractBriverHiringFragmentAddress.getText().toString();
                workingHoursPerDay = etContractBriverHiringFragmentWorkingHoursPerDay.getText().toString();
                workingDaysInAWeek = etContractBriverHiringFragmentWorkingDaysInAWeek.getText().toString();
                salaryRange = etContractBriverHiringFragmentSalaryRange.getText().toString();
                leavesInAMonth = etContractBriverHiringFragmentLeavesInAMonth.getText().toString();
                specialPreferences = etContractBriverHiringFragmentSpecialPreferences.getText().toString();

                String gender;
                String liveIn;
                if (rbContractBriverHiringFragmentMale.isChecked()) {
                    gender = "Male";
                    if (cbContractBriverHiringFragmentLiveInBoolean.isChecked()) {
                        liveIn = "Yes";
                    } else {
                        liveIn = "No";
                    }
                } else {
                    gender = "Female";
                    liveIn = "N/A";
                }

                if (cbContractBriverHiringFragmentLiveInBoolean.isChecked()) {
                    workingHoursPerDay = "N/A";
                }

                if (validateContractInfo()) {
                    String emailBody = "Name: " + name + "\n" + "Briver Gender Preference: " + gender + "\n" + "Address: " + address + "\n" +
                            "Working hours per day: " + workingHoursPerDay + "\n" + "Working days in a week: " + workingDaysInAWeek +
                            "\n" + "Salary range: " + salaryRange + "\n" + "Allowed leaves in a month: " + leavesInAMonth + "\n" +
                            "Live-In Driver: " + liveIn + "\n" + "Special Preferences: " + specialPreferences;

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + "omezzle@gmail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Permanent Briver hiring request");
                    intent.putExtra(Intent.EXTRA_TEXT, emailBody);
                    startActivity(intent);
                }
            }
        });

        rgContractBriverHiringFragmentGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_fragment_contract_briver_type_male) {
                    cbContractBriverHiringFragmentLiveInBoolean.setVisibility(View.VISIBLE);
                } else {
                    cbContractBriverHiringFragmentLiveInBoolean.setVisibility(View.GONE);
                    cbContractBriverHiringFragmentLiveInBoolean.setChecked(false);
                    etContractBriverHiringFragmentWorkingHoursPerDay.setText("");
                    llContractBriverHiringFragmentWorkingHoursPerDay.setVisibility(View.VISIBLE);
                }
            }
        });

        cbContractBriverHiringFragmentLiveInBoolean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llContractBriverHiringFragmentWorkingHoursPerDay.setVisibility(View.GONE);
                } else {
                    etContractBriverHiringFragmentWorkingHoursPerDay.setText("");
                    llContractBriverHiringFragmentWorkingHoursPerDay.setVisibility(View.VISIBLE);
                }
            }
        });

        return baseViewContractBriverHiringFragment;
    }


    public boolean validateContractInfo() {
        boolean valid = true;

        if (name.trim().length() < 3) {
            etContractBriverHiringFragmentName.setError("At least 3 characters");
            valid = false;
        } else {
            etContractBriverHiringFragmentName.setError(null);
        }

        if (address.trim().isEmpty()) {
            etContractBriverHiringFragmentAddress.setError("Empty");
            valid = false;
        } else {
            etContractBriverHiringFragmentAddress.setError(null);
        }

        if (!cbContractBriverHiringFragmentLiveInBoolean.isChecked() && workingHoursPerDay.trim().isEmpty()) {
            etContractBriverHiringFragmentWorkingHoursPerDay.setError("Empty");
            valid = false;
        } else {
            etContractBriverHiringFragmentWorkingHoursPerDay.setError(null);
        }

        if (workingDaysInAWeek.trim().isEmpty()) {
            etContractBriverHiringFragmentWorkingDaysInAWeek.setError("Empty");
            valid = false;
        } else {
            etContractBriverHiringFragmentWorkingDaysInAWeek.setError(null);
        }

        if (salaryRange.trim().isEmpty()) {
            etContractBriverHiringFragmentSalaryRange.setError("Empty");
            valid = false;
        } else {
            etContractBriverHiringFragmentSalaryRange.setError(null);
        }

        if (leavesInAMonth.trim().isEmpty()) {
            etContractBriverHiringFragmentLeavesInAMonth.setError("Empty");
            valid = false;
        } else {
            etContractBriverHiringFragmentLeavesInAMonth.setError(null);
        }

        if (specialPreferences.trim().isEmpty()) {
            etContractBriverHiringFragmentSpecialPreferences.setError("Empty");
            valid = false;
        } else {
            etContractBriverHiringFragmentSpecialPreferences.setError(null);
        }

        return valid;
    }


}