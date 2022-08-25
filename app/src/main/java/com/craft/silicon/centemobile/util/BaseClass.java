package com.craft.silicon.centemobile.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class BaseClass {
    public static final String kv = "csXDRzpcEPm_jMny";
    public static String iv = "84jfkfndl3ybdfkf";
    private static final String LogKeyValue = "KBSB&er3bflx9%";

    public static String encrypt(String value) {
        String escapedString;
        try {
            byte[] key = kv.getBytes(StandardCharsets.UTF_8);
            byte[] ivs = iv.getBytes(StandardCharsets.UTF_8);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivs);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec);
            escapedString = Base64.encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)), Base64.DEFAULT).trim();
            return escapedString;
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }
    }

    public static String newEncrypt(String text) {
        String data = "";
        try {
            CryptLib _crypt = new CryptLib();
            String key = CryptLib.SHA256(LogKeyValue, 32);
            data = _crypt.encrypt(text, key, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = data.replace("\n", "");
        return data;
    }

    public static String[] starboyEncrypt(String data) {
        Random rnd = new Random();
        final String alphabet_from = "ABCDEFGH";//keep it the first few letters
        final String alphabet_to = "IJKLMNOPQRSTUVW";//keep it the last few letters
        String[] string = new String[3];
        char char1 = alphabet_from.charAt(rnd.nextInt(alphabet_from.length()));
        char char2 = alphabet_to.charAt(rnd.nextInt(alphabet_to.length()));
        int from = getDecimalFromASCII(char1);
        int to = getDecimalFromASCII(char2);

        //wow oh my oh my make sure first letter is not Z.. to will do a while forever
        while (to > data.length() || to <= from) {
            char2 = alphabet_to.charAt(rnd.nextInt(alphabet_to.length()));
            to = getDecimalFromASCII(char2);
        }
        String server_data = removeNtoNCharacter(data, from, to);
        String version = Character.toString(char1) + Character.toString(char2);
        String keyID = data.substring(from - 1, to);
        string[0] = keyID.trim().replace("\n", "");
        string[1] = version.trim().replace("\n", "");
        string[2] = server_data;
        return string;
    }

    public static int getDecimalFromASCII(char c) {
        //log("getDecimalFromASCII",":char:"+character+":dec:"+dec);
        return (int) c;
    }

    public static String removeNtoNCharacter(String data, int removeFrom, int removeTo) {
        int from = removeFrom, to = removeTo;
        if (from > to) {//cannot remove 80th to 70th
            removeFrom = from;
            removeTo = to;
        }
        return data.substring(0, removeFrom - 1) + data.substring(removeTo);
    }

    public static void addCommonTags(JSONObject obj, Activity context) {
        try {
            obj.put("MobileNumber", "254720117033");
            obj.put("PhoneModel", "");
            obj.put("IMEI", "11aa9fa9255370c4");
            obj.put("KeyID", "");
            obj.put("CodeBase", "ANDROID");
            obj.put("LL", "");//get from gps
            obj.put("SoftwareVersion", "450");

            obj.put("RiderLL", "");
            obj.put("LatLong", "");
            obj.put("CarrierName", "SAF FOR YOU");
            obj.put("NetworkCountry", "ke");
            obj.put("Country", "KENYA");
            obj.put("LanguageID", "en");
            obj.put("DeviceName", "HUAWEI AQM-LX1");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String decrypt(String text) {
        String decryptString = "";
        try {
            /* Decrypt the message, given derived encContentValues and initialization vector. */
            byte[] key = kv.getBytes(StandardCharsets.UTF_8);
            byte[] ivs = iv.getBytes(StandardCharsets.UTF_8);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivs);
            //cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, paramSpec);
            //decryptString = new String(cipher.doFinal(cipherText.getBytes()), "UTF-8");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, paramSpec);
            byte[] decodedValue = Base64.decode(text.getBytes(), Base64.DEFAULT);
            byte[] decryptedVal = cipher.doFinal(decodedValue);
            return new String(decryptedVal);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptString;
    }

    public static String decryptLatest(String encryptedStr, String keyVal, boolean doBase64Decrypt, String v) {

        String data = "";
        String mv = TextUtils.isEmpty(v) ? iv : v;
        try {
            CryptLib crypt = new CryptLib();
            String key = CryptLib.SHA256(keyVal, 32);
            data = crypt.decrypt(encryptedStr, key, mv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //added decryption by base 64
        if (doBase64Decrypt) {
            data = base64Decrypt(data);
        }
        return data;
    }


    public static String base64Decrypt(String str) {
        // Receiving side
        byte[] data = Base64.decode(str, Base64.DEFAULT);
        String text = "";
        try {
            text = new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            text = "";
        }
        return text;
    }


    public static byte[] getBitmapBytes(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 30, bytes);
        return bytes.toByteArray();
    }


    public static double calculatePercentage(double value, double total) {
        return Math.abs((value / total) * 100);
    }

    public static String generateAlphaNumericString(int n) {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";


        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {


            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static String wordFormat(String value) {

        String[] words = value.split("(?=\\p{Upper})");
        StringBuilder stringBuilder = new StringBuilder();
        int l = words.length - 1;
        for (int i = 0; i <= l; i++) {
            String s = words[i];
            if (i != l)
                stringBuilder.append(s).append(" ");
            else stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

    public String daes(String text) {
        String data = "";
        try {
            CryptLib _crypt = new CryptLib();
            String keyValue = "KbPmng&1977dsfds%";
            String key = CryptLib.SHA256(keyValue, 32);
            String iv = "84jfkfndl3ybdfkf";
            data = _crypt.decrypt(text, key, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String hashLatest(String request) {
        String latest_hash = "ElmaHash";
        String input = request + latest_hash;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] encodedHash;
        assert digest != null;
        encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();

    }

    public static String encryptString(String decryptedString, String keyvaltest, String serverIV) {
        String data = "";

        try {
            CryptLib crypt = new CryptLib();
            String key = CryptLib.SHA256(keyvaltest, 32);
            data = crypt.encrypt(decryptedString, key, serverIV);
            data = data.replaceAll("\\r\\n|\\r|\\n", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String decryptString(String encryptedString, String device, String serverIV) {
        String data = "";
        try {
            CryptLib crypt = new CryptLib();
            String key = CryptLib.SHA256(device, 32);
            data = crypt.decrypt(encryptedString, key, serverIV);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    public static String maskCardNumber(String cardNumber) {
        final int START_LENGTH = 1;
        final int END_LENGTH = 4;
        int maskedLength = cardNumber.length() - (START_LENGTH + END_LENGTH);
        System.out.println(maskedLength);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maskedLength; i++) {
            sb.append("X");
        }

        return cardNumber.substring(0, START_LENGTH) + sb + cardNumber.substring(cardNumber.length() - END_LENGTH);
    }

    public static String mask(String input) {

        int length = input.length() - input.length() / 4;
        String s = input.substring(0, length);

        return s.replaceAll("[A-Za-z0-9]", "X") + input.substring(length);
    }

    public static String decode64(String s) {
        byte[] decoded = Base64.decode(s, Base64.DEFAULT);
        String decodedStr = new String(decoded, StandardCharsets.UTF_8);
        return decodedStr;
    }

    public static String decodeBase64(String coded) {
        byte[] valueDecoded;
        valueDecoded = Base64.decode(coded.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

    public static String nonCaps(String s) {
        if (s != null) {
            return s.toLowerCase(Locale.getDefault());
        }
        return "";
    }

    public static boolean verifyEmail(String email) {
        email = email.trim();
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$";

        if (email.equals(""))
            return false;

        return email.matches(regexPattern);
    }


    public static String formatMobile(String mobile) {
        Pattern pattern = Pattern.compile(" ");
        String[] number = pattern.split(mobile);
        return number[1].replace(" ", "");
    }


    public static void createPdf(Activity activity, Bitmap bitmap) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = bitmap.getHeight();
        float width = bitmap.getWidth();

        int convertHeight = (int) height, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth,
                convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);


        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        AppLogger.Companion.getInstance().writePDF(document,
                "transaction" + BaseClass.generateAlphaNumericString(5), activity);

    }

    public static void shareBitmap(Activity activity, Bitmap bmpMain) {
        String title = "it works";


        String bitmapPath = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bmpMain, "title", null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        intent.putExtra(Intent.EXTRA_TEXT, title);
        activity.startActivity(Intent.createChooser(intent, "Share"));
    }


    @Nullable
    public static Bitmap drawToBitmap(final View viewToDrawFrom, int width, int height) {

        boolean wasDrawingCacheEnabled = viewToDrawFrom.isDrawingCacheEnabled();
        if (!wasDrawingCacheEnabled)
            viewToDrawFrom.setDrawingCacheEnabled(true);
        if (width <= 0 || height <= 0) {
            if (viewToDrawFrom.getWidth() <= 0 || viewToDrawFrom.getHeight() <= 0) {
                viewToDrawFrom.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                width = viewToDrawFrom.getMeasuredWidth();
                height = viewToDrawFrom.getMeasuredHeight();
            }
            if (width <= 0 || height <= 0) {
                final Bitmap bmp = viewToDrawFrom.getDrawingCache();
                final Bitmap result = bmp == null ? null : Bitmap.createBitmap(bmp);
                if (!wasDrawingCacheEnabled)
                    viewToDrawFrom.setDrawingCacheEnabled(false);
                return result;
            }
            viewToDrawFrom.layout(0, 0, width, height);
        } else {
            viewToDrawFrom.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
            viewToDrawFrom.layout(0, 0, viewToDrawFrom.getMeasuredWidth(), viewToDrawFrom.getMeasuredHeight());
        }
        final Bitmap drawingCache = viewToDrawFrom.getDrawingCache();
        final Bitmap bmp = ThumbnailUtils.extractThumbnail(drawingCache, width, height);
        final Bitmap result = bmp == null || bmp != drawingCache ? bmp : Bitmap.createBitmap(bmp);
        if (!wasDrawingCacheEnabled)
            viewToDrawFrom.setDrawingCacheEnabled(false);
        return result;
    }

    public static Bitmap createLayoutBitmap(Context ctx, View view) {
        view.setLayoutParams(new
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        view.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels,
                        View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels,
                        View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap createBitmapFromLayout(View tv) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tv.measure(spec, spec);
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(tv.getMeasuredWidth(), tv.getMeasuredWidth(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate((-tv.getScrollX()), (-tv.getScrollY()));
        tv.draw(c);
        return b;
    }

    public static Bitmap loadLayoutImage(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }


    public static Bitmap takeScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }


    public static void addCommonOCR(JSONObject obj, Activity context) {
        try {
            obj.put("MobileNumber", "254720117033");
            obj.put("PhoneModel", "");
            obj.put("IMEI", "11aa9fa9255370c4");
            obj.put("KeyID", "");
            obj.put("CodeBase", "ANDROID");
            obj.put("LL", "");//get from gps
            obj.put("SoftwareVersion", "450");

            obj.put("RiderLL", "");
            obj.put("LatLong", "");
            obj.put("CarrierName", "SAF FOR YOU");
            obj.put("NetworkCountry", "ug");
            obj.put("Country", "UGANDA");
            obj.put("LanguageID", "en");
            obj.put("DeviceName", "HUAWEI AQM-LX1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void stopTimer(CountDownTimer logout) {
        if (logout != null) {
            logout.cancel();
        }
    }

    public static void setMaxLength(TextInputEditText view, int length) {
        InputFilter[] curFilters;
        InputFilter.LengthFilter lengthFilter;
        int idx;

        lengthFilter = new InputFilter.LengthFilter(length);

        curFilters = view.getFilters();
        if (curFilters != null) {
            for (idx = 0; idx < curFilters.length; idx++) {
                if (curFilters[idx] instanceof InputFilter.LengthFilter) {
                    curFilters[idx] = lengthFilter;
                    return;
                }
            }


            InputFilter[] newFilters = new InputFilter[curFilters.length + 1];
            System.arraycopy(curFilters, 0, newFilters, 0, curFilters.length);
            newFilters[curFilters.length] = lengthFilter;
            view.setFilters(newFilters);
        } else {
            view.setFilters(new InputFilter[]{lengthFilter});
        }
    }


    public static void emailCustomerCare(Activity activity, String title, String body, String email_address) {
        String[]email = new String[]{email_address};
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, email);
        i.putExtra(Intent.EXTRA_SUBJECT, title);
        i.putExtra(Intent.EXTRA_TEXT, body);
        try {
            activity.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "Sorry no email clients found on your phone", Toast.LENGTH_SHORT).show();
        }
    }

    public static void callPhone(Activity activity, String mobilenumber) {
        if (mobilenumber != null) {
            if (!(TextUtils.isEmpty(mobilenumber)) || mobilenumber.length() < 4) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + "+" + mobilenumber));
                activity.startActivity(callIntent);
            } else {
                Toast.makeText(activity, "The phone number provided is not valid", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "The phone number provided is not valid", Toast.LENGTH_SHORT).show();
        }
    }

}
