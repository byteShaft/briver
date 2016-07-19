package com.byteshaft.briver.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.WebServiceHelpers;

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

    protected void onPreExecute() {
        super.onPreExecute();
        Helpers.showProgressDialog(AppGlobals.getRunningActivityInstance(), "Reviewing Hire");
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            if (params[1].equalsIgnoreCase("0")) {
                isGetReviewTask = true;
                connection = WebServiceHelpers.openConnectionForUrl(EndPoints.BASE_URL_HIRE + params[0] + "/review", "GET", true);
            } else {
                isGetReviewTask = false;
                connection = WebServiceHelpers.openConnectionForUrl(EndPoints.BASE_URL_HIRE + params[0] + "/review", "POST", true);
            }
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.flush();
            out.close();
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
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

            } else {
                if (MainActivity.isMainActivityRunning) {
                    MainActivity.getInstance().onBackPressed();
                }
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

}
