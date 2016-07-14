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

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by fi8er1 on 05/07/2016.
 */
public class UpdateHireStatusTask extends AsyncTask<String, String  , String> {

    HttpURLConnection connection;
    public static int responseCode;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.isHiringTaskRunning = true;
        Helpers.showProgressDialog(AppGlobals.getRunningActivityInstance(), "Updating Status");
    }

    @Override
    protected String doInBackground(String[] params) {
        try {
            connection = WebServiceHelpers.openConnectionForUrl(EndPoints.BASE_URL_HIRE + params[0] + "/update" + " " + "status:" + params[1], "PUT", true);
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().findViewById(R.id.container_main),
                    "Status updated", Snackbar.LENGTH_LONG, "#A4C639");
            if (MainActivity.isMainActivityRunning) {
                MainActivity.getInstance().onBackPressed();
            }
        } else {
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().findViewById(R.id.container_main),
                    "Failed to update status", Snackbar.LENGTH_LONG, "#f44336");
        }
    }

    @Override
    protected void onCancelled() {
        MainActivity.isHiringTaskRunning = false;
        super.onCancelled();
        Helpers.dismissProgressDialog();
    }


}
