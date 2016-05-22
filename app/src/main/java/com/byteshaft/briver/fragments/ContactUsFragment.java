package com.byteshaft.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.Helpers;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class ContactUsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    final Runnable callByteShaft = new Runnable() {
        public void run() {
            Helpers.initiateCallIntent(getActivity(), "+923457221181");
        }
    };
    final Runnable mailByteShaft = new Runnable() {
        public void run() {
            Helpers.initiateEmailIntent(getActivity(), "byteshaft@gmail.com", "Mail regarding Briver", null);
        }
    };
    View baseViewContactUsFragment;
    ImageButton btnContactUsCall;
    ImageButton btnContactUsMail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewContactUsFragment = inflater.inflate(R.layout.fragment_contact_us, container, false);

        btnContactUsCall = (ImageButton) baseViewContactUsFragment.findViewById(R.id.btn_contact_us_call);
        btnContactUsMail = (ImageButton) baseViewContactUsFragment.findViewById(R.id.btn_contact_us_mail);

        return baseViewContactUsFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_contact_us_call:
                Log.i("btn", "call");
                Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(), "Are you sure?",
                        "Want to call Briver developers?", "Yes", "Cancel", callByteShaft);
                break;
            case R.id.btn_contact_us_mail:
                Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(), "Are you sure",
                        "Want to mail Briver developers?", "Yes", "Cancel", mailByteShaft);
                break;
        }
    }
}
