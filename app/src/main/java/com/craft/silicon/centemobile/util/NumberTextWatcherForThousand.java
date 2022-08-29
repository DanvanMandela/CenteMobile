package com.craft.silicon.centemobile.util;

import android.text.Editable;
import android.text.TextWatcher;

import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.input.InputData;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.binding.BindingAdapterKt;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.StringTokenizer;

public class NumberTextWatcherForThousand implements TextWatcher {

    TextInputEditText editText;
    private final AppCallbacks appCallbacks;
    private final FormControl formControl;


    public NumberTextWatcherForThousand(TextInputEditText editText,
                                        AppCallbacks appCallbacks, FormControl formControl) {
        this.editText = editText;
        this.appCallbacks = appCallbacks;
        this.formControl = formControl;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s,
                              int start,
                              int before,
                              int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            editText.removeTextChangedListener(this);
            String value = Objects.requireNonNull(editText.getText()).toString();
            if (!value.equals("")) {
                if (value.startsWith(".")) {
                    BindingAdapterKt.updateText(editText, "0.");
                }
                if (value.startsWith("0") && !value.startsWith("0.")) {
                    BindingAdapterKt.updateText(editText, "");
                }
                String str = editText.getText().toString().replaceAll(",", "");
                editText.removeTextChangedListener(this);
                BindingAdapterKt.updateText(editText, getDecimalFormattedString(str));
                editText.addTextChangedListener(this);
                editText.setSelection(editText.getText().toString().length());
            }

            editText.addTextChangedListener(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            editText.addTextChangedListener(this);
        }
    }

    public static String getDecimalFormattedString(String value) {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        StringBuilder str3 = new StringBuilder();
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt(-1 + str1.length()) == '.') {
            j--;
            str3 = new StringBuilder(".");
        }
        for (int k = j; ; k--) {
            if (k < 0) {
                if (str2.length() > 0)
                    str3.append(".").append(str2);
                return str3.toString();
            }
            if (i == 3) {
                str3.insert(0, ",");
                i = 0;
            }
            str3.insert(0, str1.charAt(k));
            i++;
        }

    }

    public static String trimCommaOfString(String string) {
        if (string.contains(",")) {
            return string.replace(",", "");
        } else {
            return string;
        }

    }
}