package com.byteshaft.briver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    ImageView ivWelcomeLogoMain;
    LinearLayout llWelcomeLogin;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ivWelcomeLogoMain = (ImageView) findViewById(R.id.iv_welcome_logo_main);
        llWelcomeLogin = (LinearLayout) findViewById(R.id.ll_welcome_login);
        registerButton = (Button) findViewById(R.id.btn_register);
        registerButton.setOnClickListener(this);
        llWelcomeLogin.setVisibility(View.GONE);

        final Animation animMainLogoFading = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.anim_welcome_logo_fading);
        final Animation animMainLogoTransition = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.anim_welcome_logo_transition);
        final Animation animLayoutLoginFadeIn = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.anim_login_layout);

        ivWelcomeLogoMain.startAnimation(animMainLogoFading);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                Intent i = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.anim_enter_register_activity
                        , R.anim.anim_exit_login_activity);
        }
    }
}
