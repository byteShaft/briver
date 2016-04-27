package com.byteshaft.briver;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class RegisterActivity extends Activity implements View.OnClickListener {

    EditText registerUserFullName;
    EditText registerUserEmail;
    EditText registerUserPassword;
    EditText registerUserConfirmPassword;

    RadioButton rbRegisterCustomer;
    RadioButton rbRegisterDriver;

    Button btnCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUserFullName = (EditText) findViewById(R.id.et_register_full_name);
        registerUserEmail = (EditText) findViewById(R.id.et_register_email);
        registerUserPassword = (EditText) findViewById(R.id.et_register_password);
        registerUserConfirmPassword = (EditText) findViewById(R.id.et_register_confirm_password);

        rbRegisterCustomer = (RadioButton) findViewById(R.id.rb_register_customer);
        rbRegisterDriver = (RadioButton) findViewById(R.id.rb_register_driver);

        btnCreateUser = (Button) findViewById(R.id.btn_usr_create);
        btnCreateUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_usr_create:
                break;
        }
    }
}
