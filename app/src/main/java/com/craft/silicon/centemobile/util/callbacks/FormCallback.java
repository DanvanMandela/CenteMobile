package com.craft.silicon.centemobile.util.callbacks;

import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.input.InputData;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public interface FormCallback {


    default void setFormNavigation(List<FormControl> forms, Modules modules) {
    }

    default void onRadioCheck(FormControl formControl, RadioButton view) {

    }

    default void onBalance(TextView textView, Accounts accounts) {
    }

    default void onToggleButton(FormControl formControl) {

    }

    default void onContactSelect(AutoCompleteTextView view) {

    }

    default void onDateSelect(AutoCompleteTextView view, FormControl formControl) {

    }

    default void setText(String str) {
    }

    default void setContact(String contact) {
    }


    default void userInput(InputData inputData) {

    }

    default void clearInputData() {

    }

    default void onServerValue(FormControl formControl,
                               TextInputEditText view) {

    }

    default void onOTP(FormControl formControl, PinView otpView) {

    }

    default void onDynamicDropDown(AutoCompleteTextView view,
                                   FormControl formControl,
                                   List<TextInputEditText> editTextList) {

    }

    default void onImageSelect(ImageView imageView, FormControl data) {

    }


}
