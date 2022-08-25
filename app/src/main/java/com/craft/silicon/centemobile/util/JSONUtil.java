package com.craft.silicon.centemobile.util;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.craft.silicon.centemobile.data.model.DataResponse;
import com.craft.silicon.centemobile.data.model.address.ExpressAddressResponse;
import com.craft.silicon.centemobile.data.model.converter.AddressResponseTypeConverter;
import com.craft.silicon.centemobile.data.model.converter.DataResponseTypeConverter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONUtil {

    @SuppressLint("NewApi")
    public ExpressAddressResponse loadJSONAddress(Activity activity) {
        String json;
        ExpressAddressResponse dataResponse;
        try {
            InputStream is = activity.getAssets().open("AddressDb.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            dataResponse = new AddressResponseTypeConverter().to(json);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return dataResponse;
    }

    @SuppressLint("NewApi")
    public String loadJSONPlay(Activity activity) {
        String json;
        try {
            InputStream is = activity.getAssets().open("Try.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public static ArrayList<HashMap<String, String>> cleanData(String s) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        HashMap<String, String> dataMap;
        s = s.replaceAll("[\\(\\)\\[\\]]", ""); //Remove blocks
        Matcher matcher = Pattern.compile("\\{[^}]*\\}").matcher(s);
        while (matcher.find()) {
            dataMap = new HashMap<>();
            String group = matcher.group();
            group = group.replaceAll("[\\(\\)\\{\\}]", ""); //Remove brackets
            group = group.replace("  ", "");// remove white space
            group = group.replace("\"", "");// remove double quotes
            group = group.replace(":", " ");// replace full colon with white space
            String[] str = group.split(",");//Split using comma
            for (String i : str) {
                i = i.replaceAll("[\\\\+\\.\\^:,]", "");//remove any unwanted characters from string
                String[] f = i.split(" ");//split using space
                if (f.length == 2) dataMap.put(f[0], f[1]);
                else if (f.length > 2) dataMap.put(f[0], f[1] + " " + f[2]);
            }
            arrayList.add(dataMap);
        }
        return arrayList;
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

    public static String setAmountFormat(String sibling, String value) {
        String[] subs = {"amount", "size"};
        sibling = sibling.toLowerCase(Locale.getDefault());
        if (isItemInString(sibling, subs)) {
            value = NumberFormat.getNumberInstance(Locale.getDefault()).format(Double.parseDouble(value));
            value = "UGX " + value;
        }
        return value;
    }

    public static boolean isItemInString(String inputString, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputString::contains);
    }


}
