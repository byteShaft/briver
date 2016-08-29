package com.beza.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beza.briver.R;
import com.beza.briver.utils.Helpers;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class ContactUsFragment extends android.support.v4.app.Fragment {

    final Runnable callByteShaft = new Runnable() {
        public void run() {
            Helpers.initiateCallIntent(getActivity(), "+923457221181");
        }
    };
    final Runnable mailByteShaft = new Runnable() {
        public void run() {
            Helpers.initiateEmailIntent(getActivity(), "byteshaft@gmail.com", "Briver", null);
        }
    };
    View baseViewContactUsFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewContactUsFragment = inflater.inflate(R.layout.fragment_contact_us, container, false);

        return baseViewContactUsFragment;
    }

}
