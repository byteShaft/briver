package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.app.Service;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.SoftKeyboard;

/**
 * Created by fi8er1 on 28/04/2016.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {

    ImageView ivWelcomeLogoMain;
    LinearLayout llWelcomeLogin;
    Button btnLogin;
    Button btnRegister;
    TextView tvForgotPassword;
    EditText etLoginEmail;
    EditText etLoginPassword;
    String sLoginEmail;
    String sLoginPassword;
    SoftKeyboard softKeyboard;
    View baseView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.fragment_login, container, false);

        ivWelcomeLogoMain = (ImageView) baseView.findViewById(R.id.iv_welcome_logo_main);
        llWelcomeLogin = (LinearLayout) baseView.findViewById(R.id.ll_welcome_login);
        etLoginEmail = (EditText) baseView.findViewById(R.id.et_login_email);
        etLoginPassword = (EditText) baseView.findViewById(R.id.et_login_password);
        btnLogin = (Button) baseView.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) baseView.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        tvForgotPassword = (TextView) baseView.findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setOnClickListener(this);
        llWelcomeLogin.setVisibility(View.GONE);

        final Animation animMainLogoFading = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_fading);
        final Animation animMainLogoTransition = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_transition_up);
        final Animation animLayoutLoginFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_login_layout);
        final Animation animMainLogoFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_fade_in);
        final Animation animMainLogoFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_fade_out);

        ivWelcomeLogoMain.startAnimation(animMainLogoFading);

        RelativeLayout mainLayout = (RelativeLayout) baseView.findViewById(R.id.layout_fragment_login);
        InputMethodManager im = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);

        softKeyboard = new SoftKeyboard(mainLayout, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivWelcomeLogoMain.startAnimation(animMainLogoFadeIn);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivWelcomeLogoMain.startAnimation(animMainLogoFadeOut);
                    }
                });
            }
        });

        animMainLogoTransition.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llWelcomeLogin.setVisibility(View.VISIBLE);
                llWelcomeLogin.startAnimation(animLayoutLoginFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animMainLogoFading.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivWelcomeLogoMain.startAnimation(animMainLogoTransition);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return baseView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                sLoginEmail = etLoginEmail.getText().toString();
                sLoginPassword = etLoginPassword.getText().toString();
                if (validateLoginInput()) {
                    Log.i("Validate: ", "Yes");
                } else {
                    Log.i("Validate: ", "No");
                }
                break;

            case R.id.btn_register:
                loadFragment(new RegisterFragment());
                break;
            case R.id.tv_forgot_password:

                break;

            default:

                break;
        }
    }

    public boolean validateLoginInput() {
        boolean valid = true;
        if (sLoginEmail.trim().isEmpty()) {
            etLoginEmail.setError("Empty");
            valid = false;
        } else if (!sLoginEmail.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(sLoginEmail).matches()) {
            etLoginEmail.setError("Invalid E-Mail");
            valid = false;
        } else {
            etLoginEmail.setError(null);
        }

        if (sLoginPassword.trim().isEmpty() || sLoginPassword.length() < 4) {
            etLoginPassword.setError("Minimum 4 Characters");
            valid = false;
        } else {
            etLoginPassword.setError(null);
        }
        return valid;
    }

    public void onLoginSuccess() {
        AppGlobals.setLoggedIn(true);
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void onLoginFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }

    public void loadFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();

        tx.setCustomAnimations(R.animator.anim_transition_fragment_left, R.animator.anim_transition_fragment_right);
        tx.replace(R.id.container, fragment);
        tx.commit();
    }
}
