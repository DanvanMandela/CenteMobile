package com.elmacentemobile.data.source.constants;


import static com.elmacentemobile.data.source.remote.helper.DynamicURLKt.getROUTE_BASE_URL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.elmacentemobile.data.model.user.ActivationData;
import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.util.AppLogger;
import com.elmacentemobile.util.BaseClass;
import com.elmacentemobile.view.fragment.map.MapData;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Constants {
    public static class Timeout {
        public static long connection = 30 * 10000;
        public static long connection_payments = 60 * 10000;//give time for external apis
        public static long connection_ocr = 90 * 10000;//takes long

        public static long read = 60 * 10000;
        public static long write = 60 * 10000;
    }

    public static class BaseUrl {

        @NotNull
        public static final String IMAGE_BASE_URL = "https://imageuploadv1.azurewebsites.net/api/ImageUpload_V1/?JSONData=";
        @NotNull
        public static final String IMAGE_PROCESSING_URL = "https://aicraftsilicon.azurewebsites.net/";


        @NotNull
        public static final String UAT = "https://uat.craftsilicon.com/CentemobileAuthDynamic/";


        @NotNull
        public static final String LIVE = "https://app.craftsilicon.com/CentemobileAuthDynamic/";

        @NotNull
        public static final String OCR = "https://craftsiliconai.azurewebsites.net/";

        @NotNull
        public static final String URL = getROUTE_BASE_URL();

    }

    public static class Contacts {
        @NotNull
        public static final String url_twitter = "https://twitter.com/CentenaryBank";
        @NotNull
        public static final String url_facebook = "https://www.facebook.com/Centenarybank/?ref=br_rs";
        @NotNull
        public static final String url_chat = "https://web.powerva.microsoft.com/environments/Default-41c79b66-e60a-4ca0-896b-f4bfdf6a9678/bots/new_bot_49bd010dd726465cab846eb4cdc61ea6/webchat";
        @NotNull
        public static final String call_center_number = "0800200555";
        @NotNull
        public static final String contact_us_email = "info@centenarybank.co.ug";
    }

    public static class Data {

        public static final boolean TEST = true;
        public static final boolean ACTIVATED = false;

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
        public final static String VERSION = "130";

        @NotNull
        public final static String GO = "cente";
    }

    public enum ImageID {
        NATIONALID, BANKSTATEMENT, BANKCODE
    }


    public static void commonJSON(JSONObject jsonObject,
                                  Context activity,
                                  String uniqueID,
                                  String formID,
                                  String customerID,
                                  boolean customer,
                                  StorageDataSource dataSource) {

        try {
            new AppLogger().appLog("SESSION", dataSource.getUniqueID().getValue());
            jsonObject.put("FormID", formID);
            jsonObject.put("UNIQUEID", uniqueID);
            jsonObject.put("SessionID", dataSource.getUniqueID().getValue());

            MapData mapData = dataSource.getLatLng().getValue();
            LatLng latLng = new LatLng(0.0, 0.0);

            ActivationData aData = dataSource.getActivationData().getValue();


            if (aData != null)
                if (!TextUtils.isEmpty(customerID))
                    customerID = aData.getId();

            if (mapData != null)
                latLng = dataSource.getLatLng().getValue().getLatLng();


            //TODO CUSTOMER ID
            if (customer)
                jsonObject.put("CustomerID",
                        TextUtils.isEmpty(customerID) ? Data.CUSTOMER_ID : customerID);

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
                    latLng.latitude + "," + latLng.longitude
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static class RoomDatabase {
        public static final String DATABASE_NAME = "local-db";
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getIMEIDeviceId(Context context) {
        String deviceId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }

            if (mTelephony != null)
                deviceId = mTelephony.getDeviceId();
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
//        return new UUID(uniquePseudoID.hashCode(), serial.hashCode()).toString();

        return UUID.randomUUID().toString();
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

    public static class MapsConstants {
        public static long interval = 600 * 10000;
        public static long fastestInterval = 5 * 10000;
        public static long maxWaitTime = 5 * 10000;
    }

}
