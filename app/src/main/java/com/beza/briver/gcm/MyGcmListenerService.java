package com.beza.briver.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.beza.briver.MainActivity;
import com.beza.briver.R;
import com.beza.briver.utils.AppGlobals;
import com.beza.briver.utils.Helpers;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
//    String notificationStatus;
//    JSONObject jsonObject;
//    int value;
//    public boolean routeStartedNotification;
//    public static boolean studentStatusChanged;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i("messageReceived", "From:" + from + " " + data);

        String hireStatus ="HireStatus: " + returnHireStatusText(Integer.parseInt(data.getString("status")));
        String name;
        if (AppGlobals.getUserType() == 0) {
            name = returnUserType() + data.getString("driver_name");
        } else {
            name = returnUserType() + data.getString("customer_name");
        }

        String startTime = "StartTime: " + Helpers.formatTimeToDisplay(data.getString("start_time"));
        String endTime = "EndTime: " + Helpers.formatTimeToDisplay(data.getString("end_time"));
        String timeSpan = "TimeSpan: " + data.get("time_span") + " Hours";

        String[] notificationMessage = new String[] {hireStatus, name, startTime, endTime, timeSpan};

        showNotification(notificationMessage);
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void showNotification(String[] message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle("Briver")
                .setContentText(message[0])
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Briver");
        for (int i = 0; i < message.length; i++) {
            inboxStyle.addLine(message[i]);
        }
        notificationBuilder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private String returnHireStatusText (int status) {
        String text = null;
        if (status == 1) {
            text = "Pending";
        } else if (status == 2 || status == 4) {
            text = "Confirmed";
        } else if (status == 5) {
            text = "Finished";
        } else if (status == 3) {
            text = "Declined";
        } else if (status == 6) {
            text = "Conflict";
        }
        return text;
    }

    private String returnUserType() {
        String text = null;
        if (AppGlobals.getUserType() == 0) {
            text = "DriverName: ";
        } else if (AppGlobals.getUserType() == 1) {
            text = "CustomerName: ";
        }
        return text;
    }
}
