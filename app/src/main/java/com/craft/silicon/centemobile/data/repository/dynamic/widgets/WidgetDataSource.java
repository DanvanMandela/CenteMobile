package com.craft.silicon.centemobile.data.repository.dynamic.widgets;

import android.app.Activity;

import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface WidgetDataSource {

    default Single<DynamicResponse> widgetRequestT(String token, PayloadData data, String path) {
        return null;
    }

    default Single<DynamicResponse> requestWidget(PayloadData data, String path) {
        return null;
    }

    default void saveFormControl(List<FormControl> data) {

    }

    default Observable<List<FormControl>> getFormControl(String moduleID) {
        return null;
    }

    default void saveModule(List<Modules> data) {

    }

    default Observable<List<Modules>> getModules(String moduleID) {
        return null;
    }


    default void requestWidget(List<ActionControls> data) {
    }

    default Observable<List<ActionControls>> getActionControl(String moduleID) {
        return null;
    }


    default Single<DynamicResponse> widgets(Activity activity, String action) {
        return null;
    }
}