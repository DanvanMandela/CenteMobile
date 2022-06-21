package com.craft.silicon.centemobile.util.callbacks;

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

}
