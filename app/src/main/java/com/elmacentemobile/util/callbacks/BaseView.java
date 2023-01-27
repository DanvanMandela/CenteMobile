package com.elmacentemobile.util.callbacks;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.viewpager2.widget.ViewPager2;

import com.elmacentemobile.view.dialog.DayTipData;
import com.elmacentemobile.view.ep.data.BusData;
import com.elmacentemobile.view.fragment.go.steps.ImageSelector;

public interface BaseView {
    default void setBinding() {
    }

    default void openTipDialog(DayTipData data) {
    }

    default void setViewModel() {
    }

    default void setOnClick() {
    }

    default void share() {
    }

    default void share(Intent intent) {
    }


    default boolean validateFields() {
        return false;
    }

    default void setController() {
    }

    default void setBroadcastListener() {

    }

    default void navigateUp() {
    }

    default void timeOut() {
    }

    default void onImage(Bitmap bitmap) {
    }

    default void onBiometric() {
    }

    default void imageSelector(ImageSelector selector, int x, int y) {
    }

    default void logOut() {
    }

    default void onOTP(String s) {
    }

    default void cleanLevel() {
    }

    default void twitter() {
    }

    default void facebook() {
    }

    default void telephone() {
    }

    default void email() {
    }

    default void chat() {
    }

    default void rate_very_poor() {
    }

    default void rate_poor() {
    }

    default void rate_average() {
    }

    default void rate_good() {
    }

    default void rate_excellent() {
    }

    default void rating_dismiss() {
    }

    default void rating_submit() {
    }

    default void setOnIndicator(ViewPager2 viewPager2) {
    }

    default void onEvent(BusData busData) {
    }

    default void auth(String pin) {
    }
}
