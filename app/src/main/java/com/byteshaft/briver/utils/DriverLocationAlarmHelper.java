package com.byteshaft.briver.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by fi8er1 on 25/05/2016.
 */
public class DriverLocationAlarmHelper {

    static PendingIntent mPendingIntent;
    static AlarmManager mAlarmManager;

    public static void setAlarm(long time) {
        mAlarmManager = getAlarmManager(AppGlobals.getContext());
        Log.i("AlarmSet", time + " Hour");
        Intent intent = new Intent("com.byteshaft.briver");
        mPendingIntent = PendingIntent.getBroadcast(AppGlobals.getContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT < 23) {
            mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
                    + TimeUnit.HOURS.toMillis(time), mPendingIntent);
        } else {
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + TimeUnit.HOURS.toMillis(time), mPendingIntent);
        }
    }

    public static void cancelAlarm() {
        if (mPendingIntent != null) {
        getAlarmManager(AppGlobals.getContext()).cancel(mPendingIntent);
        }
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
}
