package com.craft.silicon.centemobile.util.callbacks;

import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyRecyclerView;
import com.chaos.view.PinView;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.dynamic.TransactionData;
import com.craft.silicon.centemobile.data.model.input.InputData;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.craft.silicon.centemobile.data.model.user.AlertServices;
import com.craft.silicon.centemobile.data.receiver.NotificationData;
import com.craft.silicon.centemobile.databinding.BlockCardReaderLayoutBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public interface FormCallback {


    default void setFormNavigation(List<FormControl> forms, Modules modules) {
    }

    default void onRadioCheck(FormControl formControl, RadioButton view) {

    }

    default void onBalance(TextView textView,
                           Accounts accounts, TextView info,
                           Boolean b) {
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


    default void onAlertService(AlertServices services) {

    }


    default void onDynamicDropDown(AutoCompleteTextView view,
                                   FormControl formControl,
                                   List<TextInputEditText> editTextList) {

    }

    default void onImageSelect(ImageView imageView, FormControl data) {

    }

    default void globalAutoLiking(String value, TextInputEditText editText) {
    }

    default void onQRCode(BlockCardReaderLayoutBinding binding,
                          FormControl formControl, Modules modules) {
    }

    default void onLabelList(EpoxyRecyclerView view, FormControl formControl, Modules modules) {
    }

    default void deleteNotification(NotificationData data) {

    }

    default void bioPayment(TextInputEditText view) {

    }

    default void onTransactionDetails(TransactionData data) {

    }

}
