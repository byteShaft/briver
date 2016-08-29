package com.beza.briver.Tasks;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.beza.briver.MainActivity;
import com.beza.briver.R;
import com.beza.briver.fragments.HireFragment;
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
 * Created by fi8er1 on 26/05/2016.
 */
public class HiringTask extends AsyncTask<String, String , String> {

    HttpURLConnection connection;
    public static int responseCode;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.isHiringTaskRunning = true;
        Helpers.showProgressDialog(AppGlobals.getRunningActivityInstance(), "Placing hiring request");
    }

    @Override
    protected String doInBackground(String[] params) {
        try {
            String url = EndPoints.HIRE_REQUEST;
            connection = WebServiceHelpers.openConnectionForUrl(url, "POST", true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(getPutHiringRequestString(params[0], params[1], params[2], params[3]));
            Log.i("data", getPutHiringRequestString(params[0], params[1], params[2], params[3]));
            out.flush();
            out.close();
            responseCode = connection.getResponseCode();
            Log.i("responseMessage", "" + connection.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        MainActivity.isHiringTaskRunning = false;
        Helpers.dismissProgressDialog();
        Log.i("HiringTaskResponseCode", "" + responseCode);
        if (responseCode == 200) {
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().findViewById(R.id.container_main),
                    "Hiring request successfully sent", Snackbar.LENGTH_LONG, "#A4C639");
            if (MainActivity.isMainActivityRunning) {
                MainActivity.getInstance().onBackPressed();
            }
        } else {
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().findViewById(R.id.container_main),
                    "Failed to send hiring request", Snackbar.LENGTH_LONG, "#f44336");
        }
    }

    @Override
    protected void onCancelled() {
        MainActivity.isHiringTaskRunning = false;
        super.onCancelled();
        Helpers.dismissProgressDialog();
    }

    public static String getPutHiringRequestString(
            String id, String start_time, String time_span, String location) {
        JSONObject json = new JSONObject();
        try {
            json.put("driver", id);
            if (!HireFragment.isQuickHire) {
                json.put("start_time", start_time);
            }
            json.put("time_span", time_span);
            json.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
