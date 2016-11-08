package com.beza.briver.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beza.briver.R;
import com.beza.briver.Tasks.UpdateHireStatusTask;
import com.beza.briver.utils.AppGlobals;
import com.beza.briver.utils.EndPoints;
import com.beza.briver.utils.Helpers;
import com.beza.briver.utils.Paytm;
import com.beza.briver.utils.WebServiceHelpers;
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

public class HomeFragment extends android.support.v4.app.Fragment {

    View baseViewHomeFragment;
    TextView tvShowUserType;
    static TextView tvLoadingStatus;
    public static boolean isHomeFragmentOpen;
    static LinearLayout llHiresList;
    static LinearLayout llHiresLoading;
    public static MenuItem actionRefresh;
    static ProgressBar pbHome;
    int listViewSelected;
    public static int responseCode;
    public static ListView lvConfirmedHires;
    public static ListView lvPendingHires;
    public static ArrayList<String> arrayListViewUserDetails;
    public static ArrayList<Integer> hiresIdsList;
    public static ArrayList<Integer> hiresIdsListConfirmed;
    public static ArrayList<Integer> hiresIdsListPending;

    int selectedContextMenuId;
    public static GetHiresData taskGetHiresData;
    ViewUserDetailsTask taskViewUserData;

    static HttpURLConnection connection;
    public static HashMap<Integer, ArrayList<String>> hashMapHiresData;

    static boolean isHiresDataTaskRunning;
    String finalPricingForPayment;
    boolean isViewUserDetailsTaskRunning;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewHomeFragment = inflater.inflate(R.layout.fragment_home, container, false);
        tvShowUserType = (TextView) baseViewHomeFragment.findViewById(R.id.tv_home_fragment_user_type);
        tvLoadingStatus = (TextView) baseViewHomeFragment.findViewById(R.id.tv_home_loading_status);
        llHiresList = (LinearLayout) baseViewHomeFragment.findViewById(R.id.ll_home_hires_list);
        llHiresLoading = (LinearLayout) baseViewHomeFragment.findViewById(R.id.ll_home_loading);
        lvConfirmedHires = (ListView) baseViewHomeFragment.findViewById(R.id.lv_home_confirmed_hires);
        lvPendingHires = (ListView) baseViewHomeFragment.findViewById(R.id.lv_home_pending_hires);
        pbHome = (ProgressBar) baseViewHomeFragment.findViewById(R.id.pb_home);

        if (AppGlobals.getUserType() == 0) {
            tvShowUserType.setText("CUSTOMER LOGGED IN");
        } else {
            tvShowUserType.setText("DRIVER LOGGED IN");
        }

        arrayListViewUserDetails = new ArrayList<>();

        registerForContextMenu(lvConfirmedHires);
        registerForContextMenu(lvPendingHires);

        lvConfirmedHires.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewSelected = 0;
                view.showContextMenu();
            }
        });

        lvPendingHires.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewSelected = 1;
                view.showContextMenu();
            }
        });

        taskGetHiresData = (GetHiresData) new GetHiresData().execute();

        return baseViewHomeFragment;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch(v.getId()) {
            case R.id.lv_home_confirmed_hires: {
                listViewSelected = 0;
                break;
            }
            case R.id.lv_home_pending_hires: {
                listViewSelected = 1;
                break;
            }
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Log.i("info.Position", "" + info.position);
        String userName = null;
        MenuInflater inflater = this.getActivity().getMenuInflater();
        if (AppGlobals.getUserType() == 0) {
            inflater.inflate(R.menu.context_menu_hires_list_customer, menu);
        } else {
            inflater.inflate(R.menu.context_menu_hires_list_driver, menu);
        }
        if (listViewSelected == 0) {
            userName = hashMapHiresData.get(returnProperID(info.position)).get(2);
            menu.removeItem(R.id.item_context_hires_list_accept);
            menu.removeItem(R.id.item_context_hires_list_decline);
            menu.removeItem(R.id.item_context_hires_list_review);
        } else if (listViewSelected == 1) {
            userName = hashMapHiresData.get(returnProperID(info.position)).get(2);
            menu.removeItem(R.id.item_context_hires_list_finish);
            menu.removeItem(R.id.item_context_hires_list_review);
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
                String[] stringToLatLng = hashMapHiresData.get(returnProperID(info.position)).get(7).split(",");
                double latitude = Double.parseDouble(stringToLatLng[0]);
                double longitude = Double.parseDouble(stringToLatLng[1]);
                Helpers.latLngForNavigation = new LatLng(latitude, longitude);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container_main, new NavigateFragment());
                transaction.addToBackStack("NavigateFragment");
                transaction.commit();
                return true;
            case R.id.item_context_hires_list_call:
                Helpers.initiateCallIntent(getActivity(), hashMapHiresData.get(returnProperID(info.position)).get(3));
                return true;
            case R.id.item_context_hires_list_finish:
                selectedContextMenuId = info.position;
                Helpers.nameForRatingsDialog = hashMapHiresData.get(returnProperID(info.position)).get(2);
                finalPricingForPayment = hashMapHiresData.get(returnProperID(info.position)).get(9);
                Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(), "Are you sure?", "Want to complete this Hire?\n\n" +
                        "DriverFee: " + hashMapHiresData.get(returnProperID(info.position)).get(8) + "\n" +
                        "TotalCharges: " + hashMapHiresData.get(returnProperID(info.position)).get(9) + "\n\n" +
                        "Note: You'll have to pay the TotalCharges to complete this Hire", "Yes", "Cancel", finishHireDialog);
                return true;
            case R.id.item_context_hires_list_review:
//                    new ReviewHireTask().execute();
                return true;
            case R.id.item_context_hires_list_view_user_details:
                taskViewUserData = (ViewUserDetailsTask) new ViewUserDetailsTask().execute(
                        Integer.valueOf(hashMapHiresData.get(returnProperID(info.position)).get(1)));
                return true;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isHomeFragmentOpen = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isHomeFragmentOpen = false;
        if (isViewUserDetailsTaskRunning) {
            taskViewUserData.cancel(true);
        }

        if (isHiresDataTaskRunning) {
            taskGetHiresData.cancel(true);
        }
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
        actionRefresh = menu.findItem(R.id.action_refresh_home);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_home:
                if (isHiresDataTaskRunning) {
                    taskGetHiresData.cancel(true);
                    taskGetHiresData = (GetHiresData) new GetHiresData().execute();
                } else {
                    taskGetHiresData = (GetHiresData) new GetHiresData().execute();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class ViewHolder {
        TextView tvHiresPrice;
        TextView tvHiresStatus;
        TextView tvHiresDriverName;
        TextView tvHiresStartTime;
        TextView tvHiresEndTime;
        TextView tvHiresTimeSpan;
    }

    public static class GetHiresData extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isHiresDataTaskRunning = true;
            llHiresList.setVisibility(View.GONE);
            llHiresLoading.setVisibility(View.VISIBLE);
            pbHome.setVisibility(View.VISIBLE);
            tvLoadingStatus.setText("Retrieving recent hires");

            hiresIdsList = new ArrayList<>();
            hiresIdsListConfirmed = new ArrayList<>();
            hiresIdsListPending = new ArrayList<>();

            hashMapHiresData = new HashMap<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url;
            url = EndPoints.BASE_URL_USER + AppGlobals.getUserID() + "/active-requests";
            try {
                connection = WebServiceHelpers.openConnectionForUrl(url, "GET", true);
                JSONArray jsonArray = new JSONArray(WebServiceHelpers.readResponse(connection));
                Log.i("IncomingData", " HiringRequestsHome: " + jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (!hiresIdsList.contains(jsonObject.getInt("id"))) {
                        hiresIdsList.add(jsonObject.getInt("id"));
                        if (jsonObject.getInt("status") == 2 ||
                                Integer.parseInt(jsonObject.getString("status")) == 4) {
                            hiresIdsListConfirmed.add(jsonObject.getInt("id"));
                        } else if (jsonObject.getInt("status") == 1) {
                            hiresIdsListPending.add(jsonObject.getInt("id"));
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

                        JSONObject jsonObjectPricingData = new JSONObject(jsonObject.getString("price"));
                        arrayListString.add(jsonObjectPricingData.getString("driver_price"));
                        arrayListString.add(jsonObjectPricingData.getString("total_price"));

                        hashMapHiresData.put(jsonObject.getInt("id"), arrayListString);
                    }
                }
                responseCode = connection.getResponseCode();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isHiresDataTaskRunning = false;

            if (responseCode == 200) {
                CustomHiresListHomeAdapter customHiresListAdapter = new CustomHiresListHomeAdapter(AppGlobals.getContext(), R.layout.hires_list_row, hiresIdsListConfirmed);
                lvConfirmedHires.setAdapter(customHiresListAdapter);

                CustomHiresListHomeAdapter customHiresListAdapter1 = new CustomHiresListHomeAdapter(AppGlobals.getContext(), R.layout.hires_list_row, hiresIdsListPending);
                lvPendingHires.setAdapter(customHiresListAdapter1);

                if (hashMapHiresData.size() != 0) {
                    llHiresLoading.setVisibility(View.GONE);
                    llHiresList.setVisibility(View.VISIBLE);
                } else {
                    pbHome.setVisibility(View.GONE);
                    tvLoadingStatus.setText("No recent hires");
                }
            } else {
                pbHome.setVisibility(View.GONE);
                tvLoadingStatus.setText("Task failed");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isHiresDataTaskRunning = false;
        }
    }

        static class CustomHiresListHomeAdapter extends ArrayAdapter<String> {

            TimelineFragment tmFrag;

            ArrayList<Integer> arrayListIntIds;

            public CustomHiresListHomeAdapter(Context context, int resource, ArrayList<Integer> arrayList) {
                super(context, resource);

                tmFrag = new TimelineFragment();
                arrayListIntIds = arrayList;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final ViewHolder viewHolder;
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    LayoutInflater layoutInflater = (LayoutInflater) AppGlobals.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                if (AppGlobals.getUserType() == 0) {
                    viewHolder.tvHiresPrice.setText("₹" + hashMapHiresData.get(arrayListIntIds.get(position)).get(9));
                    viewHolder.tvHiresPrice.setTextColor(Color.parseColor("#F44336"));
                } else {
                    viewHolder.tvHiresPrice.setText("₹" + hashMapHiresData.get(arrayListIntIds.get(position)).get(8));
                    viewHolder.tvHiresPrice.setTextColor(Color.parseColor("#A4C639"));
                }

                int hireStatus = Integer.parseInt(hashMapHiresData.get(arrayListIntIds.get(position)).get(0));
                if (hireStatus == 1) {
                    viewHolder.tvHiresStatus.setText("Status: Pending");
                } else if (hireStatus == 2) {
                    viewHolder.tvHiresStatus.setText("Status: Accepted");
                } else if (hireStatus == 3) {
                    viewHolder.tvHiresStatus.setText("Status: Declined");
                } else if (hireStatus == 4) {
                    viewHolder.tvHiresStatus.setText("Status: InProgress");
                } else if (hireStatus == 5) {
                    viewHolder.tvHiresStatus.setText("Status: Finished");
                } else if (hireStatus == 6) {
                    viewHolder.tvHiresStatus.setText("Status: Conflict");
                }

                if (AppGlobals.getUserType() == 0) {
                    viewHolder.tvHiresDriverName.setText("Driver Name: " + hashMapHiresData.get(arrayListIntIds.get(position)).get(2));
                } else {
                    viewHolder.tvHiresDriverName.setText("Customer Name: " + hashMapHiresData.get(arrayListIntIds.get(position)).get(2));
                }
                viewHolder.tvHiresStartTime.setText("Start Time: " + Helpers.formatTimeToDisplay(hashMapHiresData.get(arrayListIntIds.get(position)).get(4)));
                viewHolder.tvHiresEndTime.setText("End Time: " + Helpers.formatTimeToDisplay(hashMapHiresData.get(arrayListIntIds.get(position)).get(5)));
                viewHolder.tvHiresTimeSpan.setText("Time Span: " + hashMapHiresData.get(arrayListIntIds.get(position)).get(6) + " Hours");
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

    private int returnProperID(int id) {
        int properID = -1;
        if (listViewSelected == 0) {
            properID = hiresIdsListConfirmed.get(id);
        } else if (listViewSelected == 1) {
            properID = hiresIdsListPending.get(id);
        }
        return properID;
    }

    public static final Runnable refreshHomeHiresList = new Runnable() {
        public void run() {
            new GetHiresData().execute();
        }
    };

    final Runnable finishHire = new Runnable() {
        public void run() {
            if (AppGlobals.getUserType() == 0) {
                String[] dataFinish = new String[]{"" + returnProperID(selectedContextMenuId), "5"};
                new UpdateHireStatusTask().execute(dataFinish);
            }
        }
    };

    final Runnable finalPayment = new Runnable() {
        @Override
        public void run() {
            Paytm.onStartTransaction(getActivity(), finalPricingForPayment, finishHire);
        }
    };

    final Runnable finishHireDialog = new Runnable() {
        @Override
        public void run() {
            Helpers.AlertDialogWithPositiveNegativeFunctions(getActivity(), "Payment Method",
                    "How do you wanna pay the service fee?", "Via Paytm", "Paid to driver by hand", finalPayment, finishHire);
        }
    };
}
