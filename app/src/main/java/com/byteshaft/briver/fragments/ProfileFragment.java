package com.byteshaft.briver.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.briver.R;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    View baseViewProfileFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewProfileFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        return baseViewProfileFragment;
    }

    @Override
    public void onClick(View v) {

    }
}