package com.craft.silicon.centemobile.util.callbacks;

import android.widget.LinearLayout;

import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.craft.silicon.centemobile.data.model.user.FrequentModules;
import com.craft.silicon.centemobile.view.ep.data.DynamicData;
import com.craft.silicon.centemobile.view.ep.data.LandingPageItem;

import java.util.HashMap;
import java.util.List;

import kotlinx.parcelize.RawValue;

public interface ModuleCallback {

    default void onModule(Modules modules) {

    }

    default void openUrl(String url) {
    }


    default void onFrequent(FrequentModules modules) {

    }

    default void onForm(FormControl formControl, Modules modules) {

    }

    default void onMenuItem() {

    }


    default void activityMove(DynamicData dynamicData) {

    }

    default void onChildren(LinearLayout linearLayout) {

    }

    default void inputData(HashMap<String, String> map) {
    }

    default void onLanding(LandingPageItem data) {
    }

    default void onModuleData() {
    }

    default void onRecent(FormControl formControl) {

    }

    default void onDisplay(FormControl formControl) {

    }

    default void currentAccount(Accounts accounts) {
    }

}
