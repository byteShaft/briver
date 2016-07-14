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
 * Created by fi8er1 on 05/07/2016.
 */

public class ReviewHireTask extends AsyncTask<Float, Void , Void> {

    HttpURLConnection connection;
    public static int responseCode;

    protected void onPreExecute() {
        super.onPreExecute();
        Helpers.showProgressDialog(AppGlobals.getRunningActivityInstance(), "Reviewing Hire");
    }

    @Override
    protected Void doInBackground(Float... params) {
        try {
            connection = WebServiceHelpers.openConnectionForUrl(EndPoints.BASE_URL_HIRE + params[0] + "/review", "POST", true);
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Helpers.dismissProgressDialog();
        Log.i("ReviewTask", "" + responseCode);
        if (responseCode == 200) {
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().findViewById(R.id.container_main),
                    "Job completed. Status: Finished", Snackbar.LENGTH_LONG, "#A4C639");
            if (MainActivity.isMainActivityRunning) {
                MainActivity.getInstance().onBackPressed();
            }
        } else {
            Helpers.showSnackBar(AppGlobals.getRunningActivityInstance().findViewById(R.id.container_main),
                    "Failed to finishing request", Snackbar.LENGTH_LONG, "#f44336");
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Helpers.dismissProgressDialog();
    }

}
