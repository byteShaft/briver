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
public class TimelineFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    View baseViewTimelineFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewTimelineFragment = inflater.inflate(R.layout.fragment_timeline, container, false);

        return baseViewTimelineFragment;
    }

    @Override
    public void onClick(View v) {

    }
}
