package com.craft.silicon.centemobile.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.util.AppLogger;
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
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.

                    new AppLogger().appLog(BroadcastSMSReceiver.class.getSimpleName()
                            + "SMS" + "OTP", message);
                    source.setOtp(NotificationServiceKt.extractOTP(message));


                    //<#> Your Little verification code is: 1182 . 6DQ6EaAsnAx
                    /*
                    message = message.replace(G5.sms_hash,"");
                    G1.log("smsreceiver-smsmessage-1",""+message);
                    message = message.replace("<#> Your Little verification code is:","");
                    G1.log("smsreceiver-smsmessage-2",""+message);
                    message = message.replaceAll(" ","");
                    G1.log("smsreceiver-smsmessage-3",""+message);


                    try{
                        //read anything before fullstop
                        if(message.contains(".")){
                            //<#> Your Little verification code is: 2222. Thanks Craft Silicon Software Services Pvt Ltd =6DUYD5D5=
                            //<#> Your Little verification code is: 3765 . IV7Gi3RiEJ3
                            String messages[] = message.split("\\.");
                            message = messages[0];
                            G1.log("smsreceiver-3a-splitted",""+message);
                            message = message.trim();
                            G1.log("smsreceiver-3b-trimmed",""+message);
                            message = message.replaceAll(" ","");
                            G1.log("smsreceiver-3c-spaceremoved",""+message);
                            message = G1.getNumberOnlyFromString(message);
                            G1.log("smsreceiver-3d-nondigitsremoved",""+message);
                        }

                        message = G13.eaes2(message);
                        Intent myintent = new Intent();
                        myintent.setAction("com.craft.little.code");
                        myintent.putExtra("method", "65749030");
                        myintent.putExtra("data1", message);
                        myintent.putExtra("data2", G13.eaes2(Long.toString(System.currentTimeMillis())));
                        myintent.putExtra("data3", "rftlyk454k7l585ikt");
                        context.sendBroadcast(myintent);
                    }catch (Exception e){

                    }*/
                    //////////////////////

                    Pattern pattern = Pattern.compile("(\\d{6})");
                    //   \d is for a digit
                    //   {} is the number of digits here 4.
                    Matcher matcher = pattern.matcher(message);
                    if (matcher.find()) {
                        message = matcher.group(0);  // 4 digit number


                        new AppLogger().appLog("smsbroadcast:otpcode", message);
                        //message = G13.eaes2(message);
                        Intent myintent = new Intent();
                        myintent.setAction("com.craft.little.code");
                        myintent.putExtra("method", "65749030");
                        myintent.putExtra("data1", message);
                        // myintent.putExtra("data2", G13.eaes2(Long.toString(System.currentTimeMillis())));
                        myintent.putExtra("data3", "rftlyk454k7l585ikt");
                        context.sendBroadcast(myintent);
                    }
                    ///////////////////
                    break;

                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    // ..
                    break;
            }
        }
    }
}
