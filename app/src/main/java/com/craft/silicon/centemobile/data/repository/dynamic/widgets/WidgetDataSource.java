package com.craft.silicon.centemobile.data.repository.dynamic.widgets;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails;
import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.craft.silicon.centemobile.data.model.user.Beneficiary;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.util.callbacks.NavigationCallback;
import com.craft.silicon.centemobile.view.ep.data.LayoutData;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface WidgetDataSource extends NavigationCallback {

    default Single<DynamicResponse> widgetRequestT(String token, PayloadData data, String path) {
        return null;
    }

    default Single<DynamicResponse> requestWidget(PayloadData data, String path) {
        return null;
    }

    default void saveFormControl(List<FormControl> data) {

    }

    default Observable<List<FormControl>> getFormControl(String moduleID, String seq) {
        return null;
    }

    default Observable<List<FormControl>> getFormControlNoSq(String moduleID) {
        return null;
    }

    default void deleteFormControl() {

    }


    default void saveModule(List<Modules> data) {

    }

    default Observable<List<Modules>> getModules(String moduleID) {
        return null;
    }


    default Observable<Modules> getModule(String moduleID) {
        return null;
    }


    default void deleteFormModule() {

    }

    default void requestWidget(List<ActionControls> data) {
    }

    default Observable<List<ActionControls>> getActionControl(String moduleID) {
        return null;
    }

    default Observable<List<ActionControls>> getActionControlByFM(String moduleID, String formID) {
        return null;
    }

    default void deleteAction() {

    }

    default Observable<List<ActionControls>> getActionControlCID(String controlID) {
        return null;
    }

    default void saveAction(List<ActionControls> data) {

    }


    default Single<DynamicResponse> widgets(Activity activity, String action) {
        return null;
    }

    default Observable<List<StaticDataDetails>> getStaticData() {
        return null;
    }

    default void saveStaticData(List<StaticDataDetails> data) {
    }

    default LiveData<List<StaticDataDetails>> storageStaticData() {
        return null;
    }


    default LiveData<List<Accounts>> accountsData() {
        return null;
    }

    default LiveData<String> versionData() {
        return null;
    }

    default LiveData<List<Beneficiary>> beneficiaryData() {
        return null;
    }

    default Single<DynamicResponse> recentList(String moduleID, String merchantID, Context context) {
        return null;
    }


    default Single<LayoutData> layoutData() {
        return null;
    }

    default void saveLayoutData(LayoutData data) {
    }

    default void resetForm() {

    }


}
