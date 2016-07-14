package com.byteshaft.briver.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.briver.R;
import com.byteshaft.briver.Tasks.UpdateHireStatusTask;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.WebServiceHelpers;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fi8er1 on 01/05/2016.
 */

public class TimelineFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    public static ViewPager mViewPager;
    public static int tabCount;
    public static int responseCode;
    public static ListView lvConfirmedHires;
    public static ListView lvPendingHires;
    public static ListView lvHistoryHires;
    public static ArrayList<Integer> hiresIdsList;
    public static ArrayList<Integer> hiresIdsListConfirmed;
    public static ArrayList<Integer> hiresIdsListPending;
    public static ArrayList<Integer> hiresIdsListHistory;
    public static ArrayList<String> arrayListViewUserDetails;
    public static HashMap<Integer, ArrayList<String>> hashMapTimelineData;
    View baseViewTimelineFragment;
    boolean isTimelineDataTaskRunning;
    boolean isViewUserDetailsTaskRunning;
    GetTimelineData taskTimelineData;
    ViewUserDetailsTask taskViewUserData;
    int tabSelected;
    HttpURLConnection connection;
    TextView tvHiresConfirmedEmpty;
    TextView tvHiresPendingEmpty;
    TextView tvHiresHistoryEmpty;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewTimelineFragment = inflater.inflate(R.layout.fragment_timeline, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) baseViewTimelineFragment.findViewById(R.id.container_timeline_tabs);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("PageSelected", "" + position);
                if (position == 0) {
                    tabSelected = 0;
                    if (hiresIdsListConfirmed.size() == 0) {
                        tvHiresConfirmedEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvHiresConfirmedEmpty.setVisibility(View.GONE);
                        CustomHiresListAdapter customHiresListAdapter1 = new CustomHiresListAdapter(getActivity(), R.layout.hires_list_row, hiresIdsListConfirmed);
                        lvConfirmedHires.setAdapter(customHiresListAdapter1);
                        registerForContextMenu(lvConfirmedHires);
                    }
                } else if (position == 1) {
                    tabSelected = 1;
                    tvHiresPendingEmpty.setVisibility(View.VISIBLE);
                    if (hiresIdsListPending.size() == 0) {
                        tvHiresPendingEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvHiresPendingEmpty.setVisibility(View.GONE);
                        CustomHiresListAdapter customHiresListAdapter2 = new CustomHiresListAdapter(getActivity(), R.layout.hires_list_row, hiresIdsListPending);
                        lvPendingHires.setAdapter(customHiresListAdapter2);
                        registerForContextMenu(lvPendingHires);
                    }
                } else if (position == 2) {
                    tabSelected = 2;
                    tvHiresHistoryEmpty.setVisibility(View.VISIBLE);
                    if (hiresIdsListHistory.size() == 0) {
                        tvHiresHistoryEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvHiresHistoryEmpty.setVisibility(View.GONE);
                        CustomHiresListAdapter customHiresListAdapter3 = new CustomHiresListAdapter(getActivity(), R.layout.hires_list_row, hiresIdsListHistory);
                        lvHistoryHires.setAdapter(customHiresListAdapter3);
                        lvHistoryHires.setEnabled(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) baseViewTimelineFragment.findViewById(R.id.tabs_timeline);
        tabLayout.setupWithViewPager(mViewPager);

        hiresIdsList = new ArrayList<>();
        hiresIdsListConfirmed = new ArrayList<>();
        hiresIdsListPending = new ArrayList<>();
        hiresIdsListHistory = new ArrayList<>();
        arrayListViewUserDetails = new ArrayList<>();
        hashMapTimelineData = new HashMap<>();

        taskTimelineData = (GetTimelineData) new GetTimelineData().execute();

        return baseViewTimelineFragment;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (isTimelineDataTaskRunning) {
            taskTimelineData.cancel(true);
        }

        if (isViewUserDetailsTaskRunning) {
            taskViewUserData.cancel(true);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Log.i("info.Position", "" + info.position);
        String userName = null;
        MenuItem finish = menu.findItem(R.id.item_context_hires_list_finish);
        MenuInflater inflater = this.getActivity().getMenuInflater();
        if (AppGlobals.getUserType() == 0 && tabSelected != 2) {
            inflater.inflate(R.menu.context_menu_hires_list_customer, menu);
        } else {
            inflater.inflate(R.menu.context_menu_hires_list_driver, menu);
        }
        if (tabSelected == 0) {
            userName = hashMapTimelineData.get(returnProperID(info.position)).get(2);
            menu.removeItem(R.id.item_context_hires_list_accept);
            menu.removeItem(R.id.item_context_hires_list_decline);
            menu.removeItem(R.id.item_context_hires_list_navigate);
        } else if (tabSelected == 1) {
            userName = hashMapTimelineData.get(returnProperID(info.position)).get(2);
            menu.removeItem(R.id.item_context_hires_list_finish);
        }
        menu.setHeaderTitle(userName);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.item_context_hires_list_accept:
                String[] dataAccept = new String[]{"" + returnProperID(info.position), "2"};
                new UpdateHireStatusTask().execute(dataAccept);
                return true;
            case R.id.item_context_hires_list_decline:
                String[] dataDecline = new String[]{"" + returnProperID(info.position), "3"};
                new UpdateHireStatusTask().execute(dataDecline);
                return true;
            case R.id.item_context_hires_list_navigate:
                String[] stringToLatLng = hashMapTimelineData.get(returnProperID(info.position)).get(7).split(",");
                double latitude = Double.parseDouble(stringToLatLng[0]);
                double longitude = Double.parseDouble(stringToLatLng[1]);
                Helpers.latLngForNavigation = new LatLng(latitude, longitude);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container_main, new NavigateFragment());
                transaction.addToBackStack("NavigateFragment");
                transaction.commit();
                return true;
            case R.id.item_context_hires_list_call:
                Helpers.initiateCallIntent(getActivity(), hashMapTimelineData.get(returnProperID(info.position)).get(3));
                return true;
            case R.id.item_context_hires_list_finish:
                String[] dataFinish = new String[]{"" + returnProperID(info.position), "5"};
                new UpdateHireStatusTask().execute(dataFinish);
                return true;
            case R.id.item_context_hires_list_view_user_details:
                if (AppGlobals.getUserType() == 0) {
                    taskViewUserData = (ViewUserDetailsTask) new ViewUserDetailsTask().execute(
                            Integer.valueOf(hashMapTimelineData.get(returnProperID(info.position)).get(1)));
                } else {
                    taskViewUserData = (ViewUserDetailsTask) new ViewUserDetailsTask().execute(
                            Integer.valueOf(hashMapTimelineData.get(returnProperID(info.position)).get(1)));
                }
                return true;
        }
        return true;
    }

    private int returnProperID(int id) {
        int properID = -1;
        if (tabSelected == 0) {
            properID = hiresIdsListConfirmed.get(id);
        } else if (tabSelected == 1) {
            properID = hiresIdsListPending.get(id);
        } else if (tabSelected == 2) {
            properID = hiresIdsListHistory.get(id);
        }
        return properID;
    }

    static class ViewHolder {
        TextView tvHiresPrice;
        TextView tvHiresStatus;
        TextView tvHiresDriverName;
        TextView tvHiresStartTime;
        TextView tvHiresEndTime;
        TextView tvHiresTimeSpan;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PlaceholderFragment placeholderFragment = new PlaceholderFragment();
            return placeholderFragment.newInstance(position + 1);
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

    public class PlaceholderFragment extends Fragment implements AdapterView.OnItemSelectedListener {
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        public PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            tabCount = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;
            if (tabCount == 1) {
                rootView = inflater.inflate(R.layout.timeline_tab_fragment_confirmed_hires, container, false);
                lvConfirmedHires = (ListView) rootView.findViewById(R.id.lv_confirmed_hires);
                registerForContextMenu(lvConfirmedHires);
                tvHiresConfirmedEmpty = (TextView) rootView.findViewById(R.id.tv_timeline_confirmed_empty);
            } else if (tabCount == 2) {
                rootView = inflater.inflate(R.layout.timeline_tab_fragement_pending_hires, container, false);
                lvPendingHires = (ListView) rootView.findViewById(R.id.lv_pending_hires);
                tvHiresPendingEmpty = (TextView) rootView.findViewById(R.id.tv_timeline_pending_empty);
            } else if (tabCount == 3) {
                rootView = inflater.inflate(R.layout.timeline_tab_fragement_history_hires, container, false);
                lvHistoryHires = (ListView) rootView.findViewById(R.id.lv_history_hires);
                registerForContextMenu(lvHistoryHires);
                tvHiresHistoryEmpty = (TextView) rootView.findViewById(R.id.tv_timeline_history_empty);
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

    private class GetTimelineData extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isTimelineDataTaskRunning = true;
            Helpers.showProgressDialog(getActivity(), "Retrieving Timeline Data");
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url;
            url = EndPoints.HIRING_REQUESTS;
            try {
                connection = WebServiceHelpers.openConnectionForUrl(url, "GET", true);
                JSONArray jsonArray = new JSONArray(WebServiceHelpers.readResponse(connection));
                Log.i("IncomingData", " HiringRequests: " + jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!hiresIdsList.contains(jsonObject.getInt("id"))) {
                        hiresIdsList.add(jsonObject.getInt("id"));
                        if (jsonObject.getInt("status") == 2 ||
                                Integer.parseInt(jsonObject.getString("status")) == 4) {
                            hiresIdsListConfirmed.add(jsonObject.getInt("id"));
                        } else if (jsonObject.getInt("status") == 1) {
                            hiresIdsListPending.add(jsonObject.getInt("id"));
                        } else if (jsonObject.getInt("status") == 3 ||
                                Integer.parseInt(jsonObject.getString("status")) == 5 ||
                                Integer.parseInt(jsonObject.getString("status")) == 6) {
                            hiresIdsListHistory.add(jsonObject.getInt("id"));
                        }
                        ArrayList<String> arrayListString = new ArrayList<>();
                        arrayListString.add(jsonObject.getString("status"));
                        if (AppGlobals.getUserType() == 0) {
                            arrayListString.add(jsonObject.getString("driver"));
                            arrayListString.add(jsonObject.getString("driver_name"));
                            arrayListString.add(jsonObject.getString("driver_phone_number"));
                        } else {
                            arrayListString.add(jsonObject.getString("customer"));
                            arrayListString.add(jsonObject.getString("customer_name"));
                            arrayListString.add(jsonObject.getString("customer_phone_number"));
                        }
                        arrayListString.add(jsonObject.getString("start_time"));
                        arrayListString.add(jsonObject.getString("end_time"));
                        arrayListString.add(jsonObject.getString("time_span"));
                        arrayListString.add(jsonObject.getString("location"));
                        hashMapTimelineData.put(jsonObject.getInt("id"), arrayListString);
                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isTimelineDataTaskRunning = false;
            Helpers.dismissProgressDialog();
            if (hiresIdsListConfirmed.size() == 0) {
                tvHiresConfirmedEmpty.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isTimelineDataTaskRunning = false;
        }
    }

    class CustomHiresListAdapter extends ArrayAdapter<String> {

        TimelineFragment tmFrag;

        ArrayList<Integer> arrayListIntIds;

        public CustomHiresListAdapter(Context context, int resource, ArrayList<Integer> arrayList) {
            super(context, resource);

            tmFrag = new TimelineFragment();
            arrayListIntIds = arrayList;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.hires_list_row, parent, false);
                viewHolder.tvHiresPrice = (TextView) convertView.findViewById(R.id.tv_hires_list_price);
                viewHolder.tvHiresStatus = (TextView) convertView.findViewById(R.id.tv_hires_list_status);
                viewHolder.tvHiresDriverName = (TextView) convertView.findViewById(R.id.tv_hires_driver_name);
                viewHolder.tvHiresStartTime = (TextView) convertView.findViewById(R.id.tv_hires_start_time);
                viewHolder.tvHiresEndTime = (TextView) convertView.findViewById(R.id.tv_hires_end_time);
                viewHolder.tvHiresTimeSpan = (TextView) convertView.findViewById(R.id.tv_hires_time_span);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvHiresPrice.setText("₹234");
            if (AppGlobals.getUserType() == 0) {
                viewHolder.tvHiresPrice.setTextColor(Color.parseColor("#F44336"));
            } else {
                viewHolder.tvHiresPrice.setTextColor(Color.parseColor("#A4C639"));
            }

            int hireStatus = Integer.parseInt(hashMapTimelineData.get(arrayListIntIds.get(position)).get(0));
            if (hireStatus == 1) {
                viewHolder.tvHiresStatus.setText("Status: Pending");
            } else if (hireStatus == 2) {
                viewHolder.tvHiresStatus.setText("Status: Accepted");
            } else if (hireStatus == 3) {
                viewHolder.tvHiresStatus.setText("Status: Declined");
            } else if (hireStatus == 4) {
                viewHolder.tvHiresStatus.setText("Status: InProgress");
            } else if (hireStatus == 5) {
                viewHolder.tvHiresStatus.setText("Status: Done");
            } else if (hireStatus == 6) {
                viewHolder.tvHiresStatus.setText("Status: Conflict");
            }

            if (AppGlobals.getUserType() == 0) {
                viewHolder.tvHiresDriverName.setText("Driver Name: " + hashMapTimelineData.get(arrayListIntIds.get(position)).get(2));
            } else {
                viewHolder.tvHiresDriverName.setText("Customer Name: " + hashMapTimelineData.get(arrayListIntIds.get(position)).get(2));
            }
            viewHolder.tvHiresStartTime.setText("Start Time: " + Helpers.formatTimeToDisplay(hashMapTimelineData.get(arrayListIntIds.get(position)).get(4)));
            viewHolder.tvHiresEndTime.setText("End Time: " + Helpers.formatTimeToDisplay(hashMapTimelineData.get(arrayListIntIds.get(position)).get(5)));
            viewHolder.tvHiresTimeSpan.setText("Time Span: " + hashMapTimelineData.get(arrayListIntIds.get(position)).get(6) + " Hours");

            return convertView;
        }

        @Override
        public int getCount() {
            return arrayListIntIds.size();
        }
    }

    private class ViewUserDetailsTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isViewUserDetailsTaskRunning = true;
            Helpers.showProgressDialog(getActivity(), "Retrieving Timeline Data");
        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                connection = WebServiceHelpers.openConnectionForUrl(EndPoints.BASE_URL_USER + params[0] + "/public-profile", "GET", true);
                JSONObject jsonObject = new JSONObject(WebServiceHelpers.readResponse(connection));
                arrayListViewUserDetails.add(jsonObject.getString("full_name"));
                arrayListViewUserDetails.add(jsonObject.getString("email"));
                arrayListViewUserDetails.add(jsonObject.getString("phone_number"));
                arrayListViewUserDetails.add(jsonObject.getString("number_of_hires"));
                arrayListViewUserDetails.add(jsonObject.getString("review_count"));
                arrayListViewUserDetails.add(jsonObject.getString("review_stars"));
                if (AppGlobals.getUserType() == 0) {
                    arrayListViewUserDetails.add(jsonObject.getString("transmission_type"));

                    String[] latLngToString = jsonObject.getString("location").split(",");
                    double latitude = Double.parseDouble(latLngToString[0]);
                    double longitude = Double.parseDouble(latLngToString[1]);
                    String addressString = Helpers.getAddress(getActivity(), new LatLng(latitude, longitude));
                    arrayListViewUserDetails.add(addressString);

                    arrayListViewUserDetails.add(jsonObject.getString("location_last_updated"));
                    arrayListViewUserDetails.add(jsonObject.getString("driving_experience"));
                    arrayListViewUserDetails.add(jsonObject.getString("bio"));
                    arrayListViewUserDetails.add(jsonObject.getString("status"));
                    arrayListViewUserDetails.add(jsonObject.getString("doc1"));
                    arrayListViewUserDetails.add(jsonObject.getString("doc2"));
                    arrayListViewUserDetails.add(jsonObject.getString("doc3"));
                } else {
                    arrayListViewUserDetails.add(jsonObject.getString("transmission_type"));
                    arrayListViewUserDetails.add(jsonObject.getString("vehicle_type"));
                    arrayListViewUserDetails.add(jsonObject.getString("vehicle_make"));
                    arrayListViewUserDetails.add(jsonObject.getString("vehicle_model"));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isViewUserDetailsTaskRunning = false;
            Helpers.dismissProgressDialog();
            Helpers.isCustomUserDetailsDialogOpenedFromMap = false;
            if (AppGlobals.getUserType() == 0) {
                Helpers.customDialogWithPositiveFunctionNegativeButtonForOnMapMarkerClickHiring(getActivity(),
                        arrayListViewUserDetails.get(0), arrayListViewUserDetails.get(1), arrayListViewUserDetails.get(2),
                        arrayListViewUserDetails.get(7), arrayListViewUserDetails.get(8), arrayListViewUserDetails.get(9),
                        arrayListViewUserDetails.get(3), arrayListViewUserDetails.get(10), arrayListViewUserDetails.get(11),
                        arrayListViewUserDetails.get(4), arrayListViewUserDetails.get(5), null, arrayListViewUserDetails.get(12),
                        arrayListViewUserDetails.get(13), arrayListViewUserDetails.get(14));
            } else {
                Helpers.customDialogWithPositiveFunctionNegativeButtonForOnMapMarkerClickHiring(getActivity(),
                        arrayListViewUserDetails.get(0), arrayListViewUserDetails.get(1), arrayListViewUserDetails.get(2),
                        null, null, null, arrayListViewUserDetails.get(3), null, null, arrayListViewUserDetails.get(4),
                        arrayListViewUserDetails.get(5), null, arrayListViewUserDetails.get(6), arrayListViewUserDetails.get(7),
                        arrayListViewUserDetails.get(8) + " " + arrayListViewUserDetails.get(9));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isViewUserDetailsTaskRunning = false;
        }
    }

}
