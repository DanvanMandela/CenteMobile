package com.craft.silicon.centemobile.util.callbacks;

import com.craft.silicon.centemobile.view.ep.data.NavigationData;
import com.craft.silicon.centemobile.view.navigation.NavigationDataSource;

public interface NavigationCallback {

    default void onNavigation(NavigationData data) {
    }

    default NavigationDataSource navigation(){return null;}

    default void  onDestination(){}
}