package com.byteshaft.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.briver.R;

/**
 * Created by fi8er1 on 18/05/2016.
 */
public class NearbyDriversFragment extends android.support.v4.app.Fragment implements {


    View baseViewNearbyDriversFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewNearbyDriversFragment = inflater.inflate(R.layout.fragment_contact_us, container, false);

        return baseViewNearbyDriversFragment;
    }

    @Override
    public void onClick(View v) {

    }

}
