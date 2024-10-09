package com.elmacentemobile.util.callbacks;

import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyRecyclerView;
import com.chaos.view.PinView;
import com.elmacentemobile.data.model.StandingOrder;
import com.elmacentemobile.data.model.control.FormControl;
import com.elmacentemobile.data.model.dynamic.TransactionData;
import com.elmacentemobile.data.model.dynamic.TransactionDynamicList;
import com.elmacentemobile.data.model.input.InputData;
import com.elmacentemobile.data.model.module.Modules;
import com.elmacentemobile.data.model.user.Accounts;
import com.elmacentemobile.data.model.user.AlertServices;
import com.elmacentemobile.data.model.user.Beneficiary;
import com.elmacentemobile.data.model.user.PendingTransaction;
import com.elmacentemobile.data.receiver.NotificationData;
import com.elmacentemobile.databinding.BlockCardReaderLayoutBinding;
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

    default void onServerValue(FormControl formControl,
                               TextView view) {

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

    default void onTransactionDetails(TransactionDynamicList data) {

    }

    default void listDataServer(EpoxyRecyclerView controller,
                                FormControl formControl,
                                Modules modules) {

    }

    default void deleteStandingOrder(FormControl formControl,
                                     Modules modules,
                                     StandingOrder standingOrder) {
    }

    default void viewStandingOrder(StandingOrder standingOrder) {
    }


    default void approveTransaction(PendingTransaction data) {
    }

    default void rejectTransaction(PendingTransaction data) {
    }

    default void deleteBeneficiary(Modules modules,
                                   Beneficiary beneficiary) {
    }

}
