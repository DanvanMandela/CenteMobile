package com.craft.silicon.centemobile.data.source.constants;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.craft.silicon.centemobile.util.BaseClass;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Constants {
    public static class Timeout {
        public static long connection = 60 * 10000;
        public static long read = 60 * 10000;
        public static long write = 60 * 10000;
    }

    public static class BaseUrl {

        @NotNull
        public static final String UAT = "https://uat.craftsilicon.com/ElmaAuthDynamic/";


        @NotNull
        public static final String LIVE = "https://app.craftsilicon.com/ElmaAuthDynamic/";


        @NotNull
        public static final String URL = Data.TEST ? UAT : LIVE;

    }

    public static class Data {

        public static final boolean TEST = true;

        @NotNull
        public static final String API_KEY = "8CC9432C-B5AD-471C-A77D-28088C695916";

        @NotNull
        public static final String COUNTRY = TEST ? "UGANDATEST" : "UGANDA";

        @NotNull
        public final static String APP_NAME = "CENTEMOBILE";

        @NotNull
        public final static String CODE_BASE = "ANDROID";

        @NotNull
        public final static String SOURCE = "APP";

        @NotNull
        public final static String BANK_ID = "16";

        @NotNull
        public final static String CUSTOMER_ID = TEST ? "25600116" : "25600016";

        @NotNull
        public final static String VERSION = "119";
    }

    public static void commonJSON(JSONObject jsonObject,
                                  Activity activity,
                                  String uniqueID,
                                  String formID,
                                  String customerID, boolean customer) {
        try {
            jsonObject.put("FormID", formID);
            jsonObject.put("UNIQUEID", uniqueID);

            if (customer)
                jsonObject.put("CustomerID", TextUtils.isEmpty(customerID) ? Data.CUSTOMER_ID : customerID);

            jsonObject.put("BankID", Data.BANK_ID);
            jsonObject.put("Country", Data.COUNTRY);
            jsonObject.put("VersionNumber", Data.VERSION);
            jsonObject.put("IMEI", BaseClass.newEncrypt(getIMEIDeviceId(activity)));
            jsonObject.put("IMSI", BaseClass.newEncrypt(getIMEIDeviceId(activity)));
            jsonObject.put("TRXSOURCE", Data.SOURCE);
            jsonObject.put("APPNAME", Data.APP_NAME);
            jsonObject.put("CODEBASE", Data.CODE_BASE);
            jsonObject.put(
                    "LATLON",
                    "0.0" + "," + "0.0"
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static class RoomDatabase {
        public static final String DATABASE_NAME = "local-db";
    }

    @SuppressLint("HardwareIds")
    public static String getIMEIDeviceId(Context context) {
        String deviceId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deviceId = mTelephony.getImei();
                } else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        return deviceId;
    }


    public static String getUniqueID() {
        String uniquePseudoID = Data.VERSION +
                BaseClass.generateAlphaNumericString(3) +
                Build.BRAND.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10 +
                BaseClass.generateAlphaNumericString(4);
        String serial = Build.getRadioVersion();
        return new UUID(uniquePseudoID.hashCode(), serial.hashCode()).toString();
    }

    public static String setMobile(String country, String mobile) {
        StringBuilder builder = new StringBuilder();
        String newMobile = removeLeadingZero(mobile);
        builder.append(country).append(newMobile);
        return builder.toString();
    }

    public static String removeLeadingZero(String value) {
        while (value.indexOf("0") == 0)
            value = value.substring(1);
        return value;
    }
}