package com.byteshaft.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class HomeFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    View baseViewHomeFragment;
    TextView tvShowUserType;
    public static boolean isHomeFragmentOpen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewHomeFragment = inflater.inflate(R.layout.fragment_home, container, false);
        tvShowUserType = (TextView) baseViewHomeFragment.findViewById(R.id.tv_home_fragment_user_type);

        if (AppGlobals.getUserType() == 0) {
            tvShowUserType.setText("CUSTOMER");
        } else {
            tvShowUserType.setText("DRIVER");
        }

        return baseViewHomeFragment;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        isHomeFragmentOpen = true;
        Log.i("resume", "home");
    }

    @Override
    public void onPause() {
        super.onPause();
        isHomeFragmentOpen = false;
        Log.i("pause", "home");
    }
}