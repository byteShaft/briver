package com.byteshaft.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.briver.R;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class HomeFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    View baseViewHomeFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewHomeFragment = inflater.inflate(R.layout.fragment_home, container, false);


        return baseViewHomeFragment;
    }

    @Override
    public void onClick(View v) {

    }
}