package com.craft.silicon.centemobile.util.callbacks;

import android.graphics.Bitmap;

import com.craft.silicon.centemobile.view.fragment.go.steps.ImageSelector;

import org.jetbrains.annotations.Nullable;

import kotlin.Unit;

public interface BaseView {
    default void setBinding() {
    }

    default void setViewModel() {
    }

    default void setOnClick() {
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

    default void onBiometric(){}

    default void imageSelector(ImageSelector selector, int x, int y) {
    }

    default void logOut() {
    }

    default void onOTP(String s){}

    default void cleanLevel(){}

    default void twitter(){}
    default void facebook(){}
    default void telephone(){}
    default void email(){}
    default void chat(){}



    default void rate_very_poor(){}
    default void rate_poor(){}
    default void rate_average(){}
    default void rate_good(){}
    default void rate_excellent(){}
    default void rating_submit(){}
    default void rating_dismiss(){}
}
