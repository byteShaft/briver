package com.byteshaft.briver.Tasks;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.WebServiceHelpers;

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
            String url = EndPoints.BASE_HIRE_REQUEST;
            connection = WebServiceHelpers.openConnectionForUrl(url, "POST", true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(getPutHiringRequestString(params[0], params[1], params[2]));
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
        MainActivity.isHiringTaskRunning = false;
        Helpers.dismissProgressDialog();
        Log.i("HiringTaskResponseCode", "" + responseCode);
        if (responseCode == 200) {
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().findViewById(R.id.container_main),
                    "Hiring request successfully sent", Snackbar.LENGTH_LONG, "#A4C639");
            Log.i("Hiring", "Success");
        } else {
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().findViewById(R.id.container_main),
                    "Failed to send hiring request", Snackbar.LENGTH_LONG, "#f44336");
            Log.i("Hiring", "Success");
        }
    }

    @Override
    protected void onCancelled() {
        MainActivity.isHiringTaskRunning = false;
        super.onCancelled();
        Helpers.dismissProgressDialog();
    }

    public static String getPutHiringRequestString(
            String id, String start_time, String time_span) {
        return
                String.format("driver_id=%s&", id) +
                        String.format("start_time=%s&", start_time) +
                        String.format("time_span=%s", time_span);
    }
}
