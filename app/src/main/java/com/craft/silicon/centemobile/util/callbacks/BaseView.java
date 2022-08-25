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

    default void imageSelector(ImageSelector selector) {
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
}
