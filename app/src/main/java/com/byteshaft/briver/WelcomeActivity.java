package com.byteshaft.briver;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.byteshaft.briver.fragments.LoginFragment;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.Helpers;


public class WelcomeActivity extends Activity {

    static FragmentManager fragmentManager;
    public static boolean isWelcomeActivityRunning;
    private static WelcomeActivity sInstance;


    public static WelcomeActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sInstance = this;
        getWindow().setBackgroundDrawableResource(R.mipmap.img_background_welcome);
        fragmentManager = getFragmentManager();

        loadLoginFragment(new LoginFragment());
    }

    public void loadLoginFragment(Fragment fragment) {
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

    @Override
    protected void onResume() {
        super.onResume();
        isWelcomeActivityRunning = true;
        if (!Helpers.isNetworkAvailable(AppGlobals.getRunningActivityInstance())) {
            Helpers.AlertDialogWithPositiveNegativeFunctions(AppGlobals.getRunningActivityInstance(),
                    "Network Unavailable", "Device is disconnected from the network. Check Network Connectivity.",
                    "Exit App", "ReCheck", exitApp, recheckNetworkConnectivity);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isWelcomeActivityRunning = false;
    }

    final Runnable exitApp = new Runnable() {
        public void run() {
                if (MainActivity.isMainActivityRunning) {
                    MainActivity.getInstance().finish();
                } else if (WelcomeActivity.isWelcomeActivityRunning) {
                    WelcomeActivity.getInstance().finish();
                }
                System.exit(0);
        }
    };

    final Runnable recheckNetworkConnectivity = new Runnable() {
        public void run() {
            if (!Helpers.isNetworkAvailable(AppGlobals.getRunningActivityInstance())) {
                Helpers.AlertDialogWithPositiveNegativeFunctions(AppGlobals.getRunningActivityInstance(),
                        "Network Unavailable", "Device is disconnected from the network. Check Network Connectivity.",
                        "Exit App", "ReCheck", exitApp, recheckNetworkConnectivity);
            }
        }
    };
}
