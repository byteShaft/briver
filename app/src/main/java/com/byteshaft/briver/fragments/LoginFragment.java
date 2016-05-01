package com.byteshaft.briver.fragments;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

import com.byteshaft.briver.MainActivity;
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
    View baseViewLoginFragment;

    Animation animMainLogoFading;
    Animation animMainLogoTransitionUp;
    Animation animMainLogoTransitionDown;
    Animation animMainLogoFadeIn;
    Animation animMainLogoFadeOut;

    boolean isSoftKeyboardOpen;
    boolean launchingMainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseViewLoginFragment = inflater.inflate(R.layout.fragment_login, container, false);

        ivWelcomeLogoMain = (ImageView) baseViewLoginFragment.findViewById(R.id.iv_welcome_logo_main);
        llWelcomeLogin = (LinearLayout) baseViewLoginFragment.findViewById(R.id.ll_welcome_login);
        etLoginEmail = (EditText) baseViewLoginFragment.findViewById(R.id.et_login_email);
        etLoginPassword = (EditText) baseViewLoginFragment.findViewById(R.id.et_login_password);
        btnLogin = (Button) baseViewLoginFragment.findViewById(R.id.btn_login_login);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) baseViewLoginFragment.findViewById(R.id.btn_login_register);
        btnRegister.setOnClickListener(this);
        tvForgotPassword = (TextView) baseViewLoginFragment.findViewById(R.id.tv_login_forgot_password);
        tvForgotPassword.setOnClickListener(this);
        llWelcomeLogin.setVisibility(View.GONE);

        animMainLogoFading = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_partial_fading);
        animMainLogoTransitionUp = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_transition_up);
        animMainLogoTransitionDown = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_transition_down);
        animMainLogoFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_fade_in);
        animMainLogoFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_welcome_logo_fade_out);

        RelativeLayout mainLayout = (RelativeLayout) baseViewLoginFragment.findViewById(R.id.layout_fragment_login);
        InputMethodManager im = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);

        if (AppGlobals.isLoggedIn()) {
            ivWelcomeLogoMain.startAnimation(animMainLogoFading);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivWelcomeLogoMain.startAnimation(animMainLogoTransitionUp);
                }
            }, 350);
        }

        softKeyboard = new SoftKeyboard(mainLayout, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                isSoftKeyboardOpen = false;
                if (!launchingMainActivity) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivWelcomeLogoMain.startAnimation(animMainLogoFadeIn);
                        }
                    });
                }
            }

            @Override
            public void onSoftKeyboardShow() {
                isSoftKeyboardOpen = true;
                if (!launchingMainActivity) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivWelcomeLogoMain.startAnimation(animMainLogoFadeOut);
                        }
                    });
                }
            }
        });

        animMainLogoFading.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(baseViewLoginFragment, 0,
                        0, baseViewLoginFragment.getWidth(), baseViewLoginFragment.getHeight());
                startActivity(intent, options.toBundle());
                AppGlobals.setLoggedIn(true);
                getActivity().finish();

//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(),
//                        R.animator.anim_transition_fragment_slide_right_exit, R.animator.anim_transition_fragment_slide_left_enter).toBundle();
//                ActivityCompat.startActivity(getActivity(), intent, bundle);

//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(), R.animator.anim_transition_fragment_slide_right_enter, R.animator.anim_transition_fragment_slide_left_exit).toBundle();
//                getActivity().startActivity(intent, bundle);


//                startActivity(new Intent(getActivity(), MainActivity.class));
//                getActivity().overridePendingTransition(R.animator.anim_transition_fragment_slide_right_enter,
//                        R.animator.anim_hold);
//                getActivity().finish();
//                getActivity().overridePendingTransition(R.animator.anim_hold, R.animator.anim_transition_fragment_slide_left_exit);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animMainLogoTransitionUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llWelcomeLogin.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animMainLogoTransitionDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivWelcomeLogoMain.startAnimation(animMainLogoFading);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return baseViewLoginFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login:
                sLoginEmail = etLoginEmail.getText().toString();
                sLoginPassword = etLoginPassword.getText().toString();
                if (validateLoginInput()) {
                    launchingMainActivity = true;
                    if (isSoftKeyboardOpen) {
                        softKeyboard.closeSoftKeyboard();
                    }
                    llWelcomeLogin.setVisibility(View.GONE);
                    ivWelcomeLogoMain.startAnimation(animMainLogoTransitionDown);
                }
                break;
            case R.id.btn_login_register:
                if (isSoftKeyboardOpen) {
                    softKeyboard.closeSoftKeyboard();
                }
                loadRegisterFragment(new RegisterFragment());
                break;
            case R.id.tv_login_forgot_password:
                if (isSoftKeyboardOpen) {
                    softKeyboard.closeSoftKeyboard();
                }
                loadPasswordRecoverFragment(new ForgotPasswordFragment());
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

        if (sLoginPassword.trim().isEmpty() || sLoginPassword.length() < 6) {
            etLoginPassword.setError("Minimum 6 Characters");
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

    public void loadRegisterFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.animator.anim_transition_fragment_slide_right_enter, R.animator.anim_transition_fragment_slide_left_exit,
                R.animator.anim_transition_fragment_slide_left_enter, R.animator.anim_transition_fragment_slide_right_exit);
        tx.replace(R.id.container, fragment).addToBackStack("Register");
        tx.commit();
    }

    public void loadPasswordRecoverFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.animator.anim_transition_fragment_slide_left_enter, R.animator.anim_transition_fragment_slide_right_exit,
                R.animator.anim_transition_fragment_slide_right_enter, R.animator.anim_transition_fragment_slide_left_exit);
        tx.replace(R.id.container, fragment).addToBackStack("Recover");
        tx.commit();
    }
}
