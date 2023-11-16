package com.elmacentemobile.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.util.AppLogger;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BroadcastSMSReceiver extends BroadcastReceiver {

    @Inject
    public StorageDataSource source;

    @Override
    public void onReceive(Context context, Intent intent) {
        new AppLogger().appLog(BroadcastSMSReceiver.class.getSimpleName()
                + "SMS" + "OTP", "INIT");
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:

                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                    new AppLogger().appLog(BroadcastSMSReceiver.class.getSimpleName()
                            + "SMS" + "OTP", "" + message);
                    source.setOtp(NotificationServiceKt.extractOTP(message));
                    break;

                case CommonStatusCodes.TIMEOUT:
                    new AppLogger().appLog(BroadcastSMSReceiver.class.getSimpleName()
                            + "SMS" + "OTP", "TIME OUT");
                    break;
            }
        }
    }
}
