package com.byteshaft.briver;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.byteshaft.briver.fragments.LoginFragment;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setBackgroundDrawableResource(R.mipmap.img_background_welcome);
        loadFragment(new LoginFragment());
    }

    public void loadFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
