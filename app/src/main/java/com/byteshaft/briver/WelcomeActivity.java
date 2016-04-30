package com.byteshaft.briver;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.byteshaft.briver.fragments.LoginFragment;
import com.byteshaft.briver.utils.Helpers;

public class WelcomeActivity extends Activity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setBackgroundDrawableResource(R.mipmap.img_background_welcome);
        fragmentManager = getFragmentManager();
        loadFragment(new LoginFragment());
    }

    public void loadFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            if (fragmentManager.getBackStackEntryCount() == 2) {
                Helpers.AlertDialogWithPositiveFunctionNegativeButton(
                        WelcomeActivity.this, "Confirmation Pending", "Really want to leave?",
                        "Yes", "No", moveFromConfirmationFragmentToLoginFragment);
            } else {
                fragmentManager.popBackStack();
            }
        } else {
            super.onBackPressed();
        }
    }

    final Runnable moveFromConfirmationFragmentToLoginFragment = new Runnable() {
        public void run() {
            int count = fragmentManager.getBackStackEntryCount();
            for(int i = 0; i < count; ++i) {
                fragmentManager.popBackStackImmediate();
            }
        }
    };
}
