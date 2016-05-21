package com.byteshaft.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.byteshaft.briver.R;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class TimelineFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    View baseViewTimelineFragment;
    public static ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewTimelineFragment = inflater.inflate(R.layout.fragment_timeline, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) baseViewTimelineFragment.findViewById(R.id.container_timeline_tabs);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) baseViewTimelineFragment.findViewById(R.id.tabs_timeline);
        tabLayout.setupWithViewPager(mViewPager);

        return baseViewTimelineFragment;
    }

    @Override
    public void onClick(View v) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Confirmed";
                case 1:
                    return "Pending";
                case 2:
                    return "History";
            }
            return null;
        }

    }

    public static class PlaceholderFragment extends Fragment implements AdapterView.OnItemSelectedListener {
        private static final String ARG_SECTION_NUMBER = "section_number";
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private FragmentManager fm;

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int tabCount = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;
            if (tabCount == 1) {
                rootView = inflater.inflate(R.layout.timeline_tab_fragment_confirmed_hires, container, false);


            } else if (tabCount == 2) {
                rootView = inflater.inflate(R.layout.timeline_tab_fragement_pending_hires, container, false);

            } else if (tabCount == 3) {
                rootView = inflater.inflate(R.layout.timeline_tab_fragement_history_hires, container, false);
            }
            return rootView;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
