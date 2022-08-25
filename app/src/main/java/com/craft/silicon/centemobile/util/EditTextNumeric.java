package com.craft.silicon.centemobile.util;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;

import com.google.android.material.textfield.TextInputEditText;

public class EditTextNumeric extends TextInputEditText {
    protected int max_value = Integer.MAX_VALUE;
    protected int min_value = Integer.MIN_VALUE;


    public EditTextNumeric(Context context) {
        super(context);
        this.setInputType(InputType.TYPE_CLASS_NUMBER);
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        if (max_value != Integer.MAX_VALUE) {
            try {
                if (Integer.parseInt(this.getText().toString()) > max_value) {

                    int selection = this.getSelectionStart();
                    this.setText(String.valueOf(max_value));
                    if (selection >= this.getText().toString().length()) {
                        selection = this.getText().toString().length();
                    }
                    this.setSelection(selection);
                }
            } catch (NumberFormatException exception) {
                super.onTextChanged(text, start, before, after);
            }
        }
        if (min_value != Integer.MIN_VALUE) {
            try {
                if (Integer.parseInt(this.getText().toString()) < min_value) {

                    int selection = this.getSelectionStart();
                    this.setText(String.valueOf(min_value));
                    if (selection >= this.getText().toString().length()) {
                        selection = this.getText().toString().length();
                    }
                    this.setSelection(selection);
                }
            } catch (NumberFormatException exception) {
                super.onTextChanged(text, start, before, after);
            }
        }
        super.onTextChanged(text, start, before, after);
    }


    public void setMaxLength(int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        this.setFilters(FilterArray);
    }


    public void setMaxValue(int value) {
        max_value = value;
    }

    public void setMinValue(int value) {
        min_value = value;
    }

    public int getValue() {
        try {
            return Integer.parseInt(this.getText().toString());
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}