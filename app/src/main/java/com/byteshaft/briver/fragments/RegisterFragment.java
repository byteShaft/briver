package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.byteshaft.briver.R;

/**
 * Created by fi8er1 on 28/04/2016.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    EditText registerUserFullName;
    EditText registerUserEmail;
    EditText registerUserPassword;
    EditText registerUserConfirmPassword;

    LinearLayout llRegisterElements;
    LinearLayout llRegisterElementsDriver;
    LinearLayout llRegisterElementsCustomer;

    RadioGroup rgRegisterSelectUserType;
    RadioGroup rgRegisterSelectVehicleType;
    RadioButton rbRegisterCustomer;
    RadioButton rbRegisterDriver;

    Button btnCreateUser;

    View baseView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.fragment_register, container, false);

        registerUserFullName = (EditText) baseView.findViewById(R.id.et_register_full_name);
        registerUserEmail = (EditText) baseView.findViewById(R.id.et_register_email);
        registerUserPassword = (EditText) baseView.findViewById(R.id.et_register_password);
        registerUserConfirmPassword = (EditText) baseView.findViewById(R.id.et_register_confirm_password);

        baseView.findViewById(R.id.ll_register).requestFocus();

        rgRegisterSelectUserType = (RadioGroup) baseView.findViewById(R.id.rg_register_select_user_type);

        rbRegisterCustomer = (RadioButton) baseView.findViewById(R.id.rb_register_customer);
        rbRegisterDriver = (RadioButton) baseView.findViewById(R.id.rb_register_driver);


        btnCreateUser = (Button) baseView.findViewById(R.id.btn_register_create_account);
        btnCreateUser.setOnClickListener(this);

        llRegisterElements = (LinearLayout) baseView.findViewById(R.id.layout_elements_register);
        llRegisterElementsCustomer = (LinearLayout) baseView.findViewById(R.id.layout_elements_register_customer);
        llRegisterElementsDriver = (LinearLayout) baseView.findViewById(R.id.layout_elements_register_driver);


        rgRegisterSelectUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_register_customer) {
                    rbRegisterDriver.setVisibility(View.INVISIBLE);
                    llRegisterElements.setVisibility(View.VISIBLE);
                    llRegisterElementsCustomer.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.rb_register_driver) {
                    rbRegisterCustomer.setVisibility(View.INVISIBLE);
                    llRegisterElements.setVisibility(View.VISIBLE);
                    llRegisterElementsDriver.setVisibility(View.VISIBLE);
                }
            }
        });

        return baseView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_create_account:
                break;
        }
    }

}
