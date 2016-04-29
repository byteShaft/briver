package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byteshaft.briver.R;

/**
 * Created by fi8er1 on 28/04/2016.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    EditText etRegisterUserFullName;
    EditText etRegisterUserEmail;
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
    RadioGroup rgRegisterSelectVehicleType;
    RadioButton rbRegisterCustomer;
    RadioButton rbRegisterDriver;

    String userRegisterFullName;
    static String userRegisterEmail;
    String userRegisterPassword;
    String userRegisterConfirmPassword;
    String userRegisterContactNumber;
    String userRegisterDrivingExperience;
    String userRegisterBasicBio;
    String userRegisterVehicleMake;
    String userRegisterVehicleModel;
    int userRegisterUserType = -1;
    int userRegisterVehicleType = -1;

    Button btnCreateUser;

    View baseViewRegisterFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        baseViewRegisterFragment = inflater.inflate(R.layout.fragment_register, container, false);

        etRegisterUserFullName = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_full_name);
        etRegisterUserEmail = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_email);
        etRegisterUserPassword = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_password);
        etRegisterUserConfirmPassword = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_confirm_password);
        etRegisterUserContactNumber = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_phone_number);
        etRegisterUserDrivingExperience = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_driving_experience);
        etRegisterUserBasicBio = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_bio);
        etRegisterUserVehicleMake = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_vehicle_make);
        etRegisterUserVehicleModel = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_vehicle_model);

        baseViewRegisterFragment.findViewById(R.id.ll_register).requestFocus();

        rgRegisterSelectUserType = (RadioGroup) baseViewRegisterFragment.findViewById(R.id.rg_register_select_user_type);
        rgRegisterSelectVehicleType = (RadioGroup) baseViewRegisterFragment.findViewById(R.id.rg_register_select_vehicle_type);

        rbRegisterCustomer = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_customer);
        rbRegisterDriver = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_driver);

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
                    userRegisterUserType = 0;
                } else if (checkedId == R.id.rb_register_driver) {
                    rbRegisterCustomer.setVisibility(View.INVISIBLE);
                    llRegisterElements.setVisibility(View.VISIBLE);
                    llRegisterElementsDriver.setVisibility(View.VISIBLE);
                    userRegisterUserType = 1;
                }
            }
        });

        rgRegisterSelectVehicleType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_register_customer_vehicle_type_economy) {
                    userRegisterVehicleType = 0;
                } else if (checkedId == R.id.rb_register_customer_vehicle_type_luxury) {
                    userRegisterVehicleType = 1;
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
                userRegisterPassword = etRegisterUserPassword.getText().toString();
                userRegisterConfirmPassword = etRegisterUserConfirmPassword.getText().toString();
                userRegisterContactNumber = etRegisterUserContactNumber.getText().toString();
                userRegisterDrivingExperience = etRegisterUserDrivingExperience.getText().toString();
                userRegisterBasicBio = etRegisterUserBasicBio.getText().toString();
                userRegisterVehicleMake = etRegisterUserVehicleMake.getText().toString();
                userRegisterVehicleModel = etRegisterUserVehicleModel.getText().toString();
//
//                if (validateRegisterInfo()) {
//                    loadFragment(new CodeConfirmationFragment());
//                }

                loadFragment(new CodeConfirmationFragment());
                break;
        }
    }

    public void loadFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.animator.anim_transition_fragment_slide_in_left, R.animator.anim_transition_fragment_slide_out_left,
                R.animator.anim_transition_fragment_slide_out_right, R.animator.anim_transition_fragment_slide_in_right);
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

        if (userRegisterUserType == 0) {
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
        
        if (userRegisterUserType == 1) {
            if (userRegisterDrivingExperience.trim().isEmpty()) {
                etRegisterUserDrivingExperience.setError("Empty");
                valid = false;
            } else {
                etRegisterUserDrivingExperience.setError(null);
            }
        }

        if (valid && userRegisterUserType == 0 && userRegisterVehicleType == -1) {
            Toast.makeText(getActivity(), "Select Vehicle Type", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

}
