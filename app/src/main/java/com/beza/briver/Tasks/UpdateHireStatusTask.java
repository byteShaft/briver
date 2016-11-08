package com.beza.briver.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.beza.briver.MainActivity;
import com.beza.briver.fragments.HomeFragment;
import com.beza.briver.utils.AppGlobals;
import com.beza.briver.utils.EndPoints;
import com.beza.briver.utils.Helpers;
import com.beza.briver.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by fi8er1 on 05/07/2016.
 */
public class UpdateHireStatusTask extends AsyncTask<String, String  , String> {

    HttpURLConnection connection;
    public static int responseCode;
    boolean reviewAsWell;
    String[] paramsForRetry;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.isHiringTaskRunning = true;
        Helpers.showProgressDialog(AppGlobals.getRunningActivityInstance(), "Updating Status");
    }

    @Override
    protected String doInBackground(String[] params) {
        try {
            paramsForRetry = params;
            connection = WebServiceHelpers.openConnectionForUrl(EndPoints.BASE_URL_HIRE + params[0] + "/update", "PUT", true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(getStatusChangeString(params[1]));
            out.flush();
            out.close();
            responseCode = connection.getResponseCode();

            reviewAsWell = params[1].equalsIgnoreCase("5") && AppGlobals.getUserType() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params[0];
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        MainActivity.isHiringTaskRunning = false;
        Helpers.dismissProgressDialog();
        Log.i("UpdateHiringResponse", "" + responseCode);
        try {
            Log.i("UpdateHiringMessage", "" + connection.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (responseCode == 200) {
            Toast.makeText(AppGlobals.getContext(), "Status successfully updated", Toast.LENGTH_LONG).show();
            if (!reviewAsWell) {
            if (HomeFragment.isHomeFragmentOpen) {
                HomeFragment.refreshHomeHiresList.run();
            } else if (MainActivity.isMainActivityRunning) {
                MainActivity.getInstance().onBackPressed();
                }
            } else {
                Helpers.customRatingDialog(AppGlobals.getRunningActivityInstance(), Helpers.nameForRatingsDialog, s);
            }
        } else {
            Helpers.AlertDialogWithPositiveFunctionNegativeButton(AppGlobals.getRunningActivityInstance(),
                    "TaskFailed", "UpdateHireStatus request failed.", "Retry", "Cancel", retryTask);
            Toast.makeText(AppGlobals.getContext(), "Failed to update status", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        MainActivity.isHiringTaskRunning = false;
        super.onCancelled();
        Helpers.dismissProgressDialog();
    }


    public static String getStatusChangeString (String status) {
        JSONObject json = new JSONObject();
        try {
            json.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    final Runnable initiateReviewTask = new Runnable() {
        public void run() {
            new ReviewHireTask().execute();
        }
    };

    final Runnable retryTask = new Runnable() {
        public void run() {
            if (paramsForRetry != null) {
                new UpdateHireStatusTask().execute(paramsForRetry);
            }
        }
    };

}
