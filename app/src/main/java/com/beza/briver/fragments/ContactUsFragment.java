package com.beza.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beza.briver.R;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class ContactUsFragment extends android.support.v4.app.Fragment {

    View baseViewContactUsFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewContactUsFragment = inflater.inflate(R.layout.fragment_contact_us, container, false);

        return baseViewContactUsFragment;
    }

}
