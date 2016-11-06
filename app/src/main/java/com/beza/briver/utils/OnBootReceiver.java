package com.beza.briver.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by fi8er1 on 25/05/2016.
 */
public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BootReceiver", "Called");
        if (AppGlobals.getUserType() == 1 && AppGlobals.isLoggedIn() && AppGlobals.getLocationReportingType() == 1) {
            DriverLocationAlarmHelper.cancelAlarm();
            DriverLocationAlarmHelper.setAlarm(AppGlobals.getDriverLocationReportingIntervalTime());
        }
    }
}
