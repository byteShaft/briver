package com.byteshaft.briver.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.fragments.TimelineFragment;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.WebServiceHelpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by fi8er1 on 05/07/2016.
 */

public class ReviewHireTask extends AsyncTask<String, String , String> {

    HttpURLConnection connection;
    public static int responseCode;
    boolean isGetReviewTask;
    int reviewStatus;
    String userName;
    String hireId;

    protected void onPreExecute() {
        super.onPreExecute();
        Helpers.showProgressDialog(AppGlobals.getRunningActivityInstance(), "Reviewing Hire");
    }

    @Override
    protected String doInBackground(String... params) {
        hireId = params[0];
        Log.i("ReviewHireID", "" + params[0]);
        Log.i("ReviewGetOrPost", "" + params[1]);
        try {
            if (params[1].equalsIgnoreCase("0")) {
                isGetReviewTask = true;
                connection = WebServiceHelpers.openConnectionForUrl(EndPoints.BASE_URL_HIRE + params[0] + "/review", "GET", true);
                JSONObject jsonObject = new JSONObject(WebServiceHelpers.readResponse(connection));
                reviewStatus = jsonObject.getInt("status");
                if (AppGlobals.getUserType() == 0) {
                    userName = jsonObject.getString("driver_name");
                } else {
                    userName = jsonObject.getString("customer_name");
                }
                Log.i("reviewGETResponse", "" + jsonObject);
            } else {
                isGetReviewTask = false;
                connection = WebServiceHelpers.openConnectionForUrl(EndPoints.BASE_URL_HIRE + params[0] + "/review", "PUT", true);

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(getPutReviewString(params[2]));
                out.flush();
                out.close();
            }
            responseCode = connection.getResponseCode();
            Log.i("reviewTaskResponseCode", "" + responseCode);
            Log.i("reviewTaskResponseMSG", "" + connection.getResponseMessage());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        Helpers.dismissProgressDialog();
        Log.i("ReviewTask", "" + responseCode);
        if (responseCode == 200) {
            if (isGetReviewTask) {
                if (reviewStatus == 0) {
                    Helpers.customRatingDialog(AppGlobals.getRunningActivityInstance(), userName, hireId);
                } else if (reviewStatus == 1) {
                    if (AppGlobals.getUserType() == 1) {
                        Helpers.AlertDialogMessage(AppGlobals.getRunningActivityInstance(), "ReviewTask", "This hire has already been reviewed", "Ok");
                    } else {
                        Helpers.customRatingDialog(AppGlobals.getRunningActivityInstance(), userName, hireId);
                    }
                } else if (reviewStatus == 2) {
                    if (AppGlobals.getUserType() == 0) {
                        Helpers.AlertDialogMessage(AppGlobals.getRunningActivityInstance(), "ReviewTask", "This hire has already been reviewed", "Ok");
                    } else {
                        Helpers.customRatingDialog(AppGlobals.getRunningActivityInstance(), userName, hireId);
                    }
                } else if (reviewStatus == 3) {
                    Helpers.AlertDialogMessage(AppGlobals.getRunningActivityInstance(), "ReviewTask", "This hire has already been reviewed", "Ok");
                }
            } else {
                Toast.makeText(AppGlobals.getContext(), "ReviewTask Success", Toast.LENGTH_SHORT).show();
                if (TimelineFragment.isTimelineFragemntOpen) {
                    MainActivity.getInstance().onBackPressed();
                }
                Helpers.dismissCustomRatingDialog();
            }
        } else {
            Toast.makeText(AppGlobals.getContext(), "ReviewTask failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Helpers.dismissProgressDialog();
    }

    public static String getPutReviewString (String rating) {
        JSONObject json = new JSONObject();
        try {
            if (AppGlobals.getUserType() == 0) {
                json.put("customer_review", rating);
            } else {
                json.put("driver_review", rating);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
