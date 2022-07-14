package com.craft.silicon.centemobile.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Locale;
import java.util.Random;

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
            sb.append("#");
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




}
