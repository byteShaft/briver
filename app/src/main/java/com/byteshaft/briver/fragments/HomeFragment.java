package com.byteshaft.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    LinearLayout llHiresList;
    ListView lvConfirmedHires;
    ListView lvPendingHires;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewHomeFragment = inflater.inflate(R.layout.fragment_home, container, false);
        tvShowUserType = (TextView) baseViewHomeFragment.findViewById(R.id.tv_home_fragment_user_type);

        llHiresList = (LinearLayout) baseViewHomeFragment.findViewById(R.id.ll_home_hires_list);
        lvConfirmedHires = (ListView) baseViewHomeFragment.findViewById(R.id.lv_confirmed_hires);
        lvPendingHires = (ListView) baseViewHomeFragment.findViewById(R.id.lv_pending_hires);

        if (AppGlobals.getUserType() == 0) {
            tvShowUserType.setText("CUSTOMER LOGGED IN");
        } else {
            tvShowUserType.setText("DRIVER LOGGED IN");
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_current_location:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}