package com.elmacentemobile.util.callbacks;

import com.elmacentemobile.view.ep.data.NavigationData;
import com.elmacentemobile.view.navigation.NavigationDataSource;

public interface NavigationCallback {

    default void onNavigation(NavigationData data) {
    }

    default NavigationDataSource navigation() {
        return null;
    }

    default void onDestination() {
    }

    default void toBranch() {
    }

    default void toLogin() {
    }

    default void toOnline() {
    }

    default void toSelf() {
    }

    default void onTheGo() {
    }
}
