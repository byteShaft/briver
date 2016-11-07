package com.beza.briver.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.beza.briver.fragments.CodeConfirmationFragment;

/**
 * Created by fi8er1 on 07/11/2016.
 */

public class OTPReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj .length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[])                                                                                                    pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage .getDisplayMessageBody();
                    try {
                        if (phoneNumber.equals("HP-BRIVER")) {
                            CodeConfirmationFragment.otp = message.substring(message.length() - 5);
                        }
                    }
                    catch(Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}
    }
}
