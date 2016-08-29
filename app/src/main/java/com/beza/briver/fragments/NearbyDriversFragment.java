package com.beza.briver.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.RatingBar;
import android.widget.TextView;

import com.beza.briver.R;
import com.beza.briver.Tasks.HiringTask;
import com.beza.briver.utils.AppGlobals;
import com.beza.briver.utils.EndPoints;
import com.beza.briver.utils.Helpers;
import com.beza.briver.utils.WebServiceHelpers;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fi8er1 on 18/05/2016.
 */
public class NearbyDriversFragment extends android.support.v4.app.Fragment {

    View baseViewNearbyDriversFragment;
    HashMap<Integer, String> hashMapDriverAddresses;
    ArrayList<Integer> driversId;
    String driverName;
    int indexContextMenu;
    private static String[] stringArrayForDriverHiring;
    public static HiringTask taskHiringDriver;
    private String driverIdForHiring;
    private String driverTimeSpanForHiring;
    private String driverTimeOfHiring;


    public static ArrayList<String> arrayListPricingDetails;

    GetPricingTask taskGetPricing;

    ListView nearbyDriversList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewNearbyDriversFragment = inflater.inflate(R.layout.fragment_nearby_drivers_list, container, false);
        nearbyDriversList = (ListView) baseViewNearbyDriversFragment.findViewById(R.id.lv_list_nearby_drivers);
        driversId = new ArrayList<>();
        hashMapDriverAddresses = new HashMap<>();

        nearbyDriversList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.showContextMenu();
            }
        });

        return baseViewNearbyDriversFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetDriversAddresses().execute();

    }

    class CustomRoutesListAdapter extends ArrayAdapter<String> {

        ArrayList<Integer> arrayListIntIds;
        public CustomRoutesListAdapter(Context context, int resource, ArrayList<Integer> arrayList) {
            super(context, resource);
            arrayListIntIds = arrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.nearby_drivers_list_row, parent, false);
                viewHolder.tvNearbyDriversListFullName = (TextView) convertView.findViewById(R.id.tv_nearby_drivers_list_full_name);
                viewHolder.tvNearbyDriversListEmail = (TextView) convertView.findViewById(R.id.tv_nearby_drivers_list_email);
                viewHolder.tvNearbyDriversListContact = (TextView) convertView.findViewById(R.id.tv_nearby_drivers_contact);
                viewHolder.tvNearbyDriversListTotalHires = (TextView) convertView.findViewById(R.id.tv_nearby_drivers_total_hires);
                viewHolder.tvNearbyDriversListAddress = (TextView) convertView.findViewById(R.id.tv_nearby_drivers_address);
                viewHolder.tvNearbyDriversListDrivingExperience = (TextView) convertView.findViewById(R.id.tv_nearby_drivers_driving_experience);
                viewHolder.tvNearbyDriversListLocationLastUpdated = (TextView) convertView.findViewById(R.id.tv_nearby_drivers_location_last_updated);
                viewHolder.tvNearbyDriversListBio = (TextView) convertView.findViewById(R.id.tv_nearby_drivers_bio);
                viewHolder.tvNearbyDriversListStatus = (TextView) convertView.findViewById(R.id.tv_nearby_drivers_status);
                viewHolder.tvNearbyDriversReviewCount = (TextView) convertView.findViewById(R.id.tv_total_rating_nearby_driver_list);
                viewHolder.rBarNearbyDrivers = (RatingBar) convertView.findViewById(R.id.rBar_nearby_drivers_list);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvNearbyDriversListFullName.setText("Driver Name: " + HireFragment.hashMapDriverData.get(arrayListIntIds.get(position)).get(0));
            viewHolder.tvNearbyDriversListEmail.setText("Email: " + Helpers.replaceFirstThreeCharacters(HireFragment.hashMapDriverData.get(arrayListIntIds.get(position)).get(1)));
            viewHolder.tvNearbyDriversListContact.setText("Contact: " + Helpers.replaceLastThreeCharacters(HireFragment.hashMapDriverData.get(arrayListIntIds.get(position)).get(2)));
            viewHolder.tvNearbyDriversListTotalHires.setText("Total Hires: " + HireFragment.hashMapDriverData.get(arrayListIntIds.get(position)).get(6));
            viewHolder.tvNearbyDriversListAddress.setText("Address: " + hashMapDriverAddresses.get(arrayListIntIds.get(position)));
            String drivingExperienceYears = HireFragment.hashMapDriverData.get(arrayListIntIds.get(position)).get(5);
            if (drivingExperienceYears.equalsIgnoreCase("1")) {
                viewHolder.tvNearbyDriversListDrivingExperience.setText("Driving Experience: " + drivingExperienceYears + " Year");
            } else {
                viewHolder.tvNearbyDriversListDrivingExperience.setText("Driving Experience: " + drivingExperienceYears + " Years");
            }
            viewHolder.tvNearbyDriversListLocationLastUpdated.setText("Location Last Updated: " + Helpers.getTimeAgo(Helpers.getTimeInMillis(HireFragment.hashMapDriverData.get(arrayListIntIds.get(position)).get(4))));
            String bio = HireFragment.hashMapDriverData.get(arrayListIntIds.get(position)).get(7);
            if (bio.length() > 2) {
                viewHolder.tvNearbyDriversListBio.setText("Bio: " + bio);
            } else {
                viewHolder.tvNearbyDriversListBio.setVisibility(View.GONE);
            }

            int status = Integer.parseInt(HireFragment.hashMapDriverData.get(HireFragment.driversIdList.get(position)).get(8));
            if (status == 1) {
                viewHolder.tvNearbyDriversListStatus.setText("Status: Available");
            } else if (status == 2) {
                viewHolder.tvNearbyDriversListStatus.setText("Status: Online");
            }
            viewHolder.tvNearbyDriversReviewCount.setText("(" + HireFragment.hashMapDriverData.get(HireFragment.driversIdList.get(position)).get(9) + ")");

            Float ratingStars = Float.parseFloat(HireFragment.hashMapDriverData.get(HireFragment.driversIdList.get(position)).get(10));
            if (ratingStars > 0.0) {
                viewHolder.rBarNearbyDrivers.setRating(ratingStars);
            } else {
                viewHolder.rBarNearbyDrivers.setRating((float) 0.0);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            Log.e("TAG", String.valueOf(arrayListIntIds.size()));
            return arrayListIntIds.size();
        }
    }

    static class ViewHolder {
        TextView tvNearbyDriversListFullName;
        TextView tvNearbyDriversListEmail;
        TextView tvNearbyDriversListContact;
        TextView tvNearbyDriversListTotalHires;
        TextView tvNearbyDriversListAddress;
        TextView tvNearbyDriversListDrivingExperience;
        TextView tvNearbyDriversListLocationLastUpdated;
        TextView tvNearbyDriversListBio;
        TextView tvNearbyDriversListStatus;
        TextView tvNearbyDriversReviewCount;
        RatingBar rBarNearbyDrivers;
    }

    private class GetDriversAddresses extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(getActivity(), "Retrieving drivers info");
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < HireFragment.driversIdList.size(); i++) {
                if (!driversId.contains(HireFragment.driversIdList.get(i))) {
                    driversId.add(HireFragment.driversIdList.get(i));
                    String[] latLngToString = HireFragment.hashMapDriverData.get(HireFragment.driversIdList.get(i)).get(3).split(",");
                    double latitude = Double.parseDouble(latLngToString[0]);
                    double longitude = Double.parseDouble(latLngToString[1]);
                    String addressString = Helpers.getAddress(getActivity(), new LatLng(latitude, longitude));
                    hashMapDriverAddresses.put(HireFragment.driversIdList.get(i), addressString);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Helpers.dismissProgressDialog();
            CustomRoutesListAdapter customRoutesListAdapter = new CustomRoutesListAdapter(getActivity(), R.layout.nearby_drivers_list_row, HireFragment.driversIdList);
            nearbyDriversList.setAdapter(customRoutesListAdapter);
            if (AppGlobals.isNearbyDriversFragmentFirstRun()) {
                Helpers.AlertDialogMessage(getActivity(), "One Time Message", "Tap and hold on a specific driver to hire", "Ok");
                AppGlobals.setNearbyDriversFragmentFirstRun(false);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(nearbyDriversList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        driverName = HireFragment.hashMapDriverData.get(HireFragment.driversIdList.get(info.position)).get(0);
        menu.setHeaderTitle(driverName);
        driverIdForHiring = Integer.toString(HireFragment.driversIdList.get(info.position));
        Log.i("DriverIDForHiring", driverIdForHiring);
        MenuInflater inflater = this.getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_nearby_drivers_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        indexContextMenu = info.position;
        System.out.println(HireFragment.driversIdList.get(indexContextMenu));
        switch (item.getItemId()) {
            case R.id.item_context_nearby_drivers_list_hire:
                driverTimeSpanForHiring = Integer.toString(HireFragment.totalHoursOfService);
                taskGetPricing = (GetPricingTask) new GetPricingTask().execute(driverTimeSpanForHiring);
                return true;
        }
        return true;
    }

    final Runnable hire = new Runnable() {
        public void run() {
            Log.i("ContextMenuItem", "Hire");
            if (!HireFragment.isQuickHire){
                driverTimeOfHiring = HireFragment.serviceStartTime;
            }
            stringArrayForDriverHiring = new String[]{driverIdForHiring, driverTimeOfHiring,
                    driverTimeSpanForHiring, HireFragment.hireMeetUpPoint};
            taskHiringDriver = (HiringTask) new HiringTask().execute(stringArrayForDriverHiring);
        }
    };

    final Runnable retryPricingTask = new Runnable() {
        public void run() {
            taskGetPricing = (GetPricingTask) new GetPricingTask().execute(driverTimeSpanForHiring);
        }
    };

    public class GetPricingTask extends AsyncTask<String, String , String> {

        HttpURLConnection connection;
        public int responseCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(AppGlobals.getRunningActivityInstance(), "Calculating prices");
            arrayListPricingDetails = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String[] params) {
            try {
                String url = EndPoints.BASE_URL_HIRE + "get-price";
                connection = WebServiceHelpers.openConnectionForUrl(url, "POST", true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(getPricingRequestString(params[0], AppGlobals.getVehicleType()));
                Log.i("data", getPricingRequestString(params[0], AppGlobals.getVehicleType()));
                out.flush();
                out.close();
                responseCode = connection.getResponseCode();
                Log.i("responseMessage", "" + connection.getResponseMessage());
                JSONObject jsonObject = new JSONObject(WebServiceHelpers.readResponse(connection));
                arrayListPricingDetails.add(jsonObject.getString("hours"));
                arrayListPricingDetails.add(jsonObject.getString("briver_price"));
                arrayListPricingDetails.add(jsonObject.getString("driver_price"));
                arrayListPricingDetails.add(jsonObject.getString("driver_hourly_rate"));
                arrayListPricingDetails.add(jsonObject.getString("total_price"));

                Log.i("pricingResponse", "" + jsonObject);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            Helpers.dismissProgressDialog();
            Log.i("PricingTaskResponseCode", "" + responseCode);
            if (responseCode == 200) {
                Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(), "Confirmation",
                        "Do you really want to hire " + driverName + "?" + "\n\n" + "Total Hours of Service: " + arrayListPricingDetails.get(0) + " Hours\n" +
                        "Initial Hiring Price: ₹" + arrayListPricingDetails.get(1) + "\n" +
                        "Driver Price: ₹" + arrayListPricingDetails.get(2) + "\n" +
                        "Driver Hourly Rate: ₹" + arrayListPricingDetails.get(3) + "\n" +
                        "Total Price: ₹" + arrayListPricingDetails.get(4) + "\n\n" +
                        "Note: Only the Initial Hiring Price will be paid at this moment.", "Yes", "Cancel", hire);
            } else {
                Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(), "Failed",
                        "Unable to calculate prices", "Retry", "Cancel", retryPricingTask);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Helpers.dismissProgressDialog();
        }

        public String getPricingRequestString(
                String hours, int segment) {
            JSONObject json = new JSONObject();
            try {
                json.put("hours", hours);
                json.put("segment", segment);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json.toString();
        }
    }

}