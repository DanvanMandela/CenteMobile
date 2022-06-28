package com.craft.silicon.centemobile.data.repository.dynamic.widgets;

import android.util.Log;

import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails;
import com.craft.silicon.centemobile.data.scope.Local;
import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;

@Singleton
public class WidgetRepository implements WidgetDataSource {

    private final WidgetDataSource remoteData;
    private final WidgetDataSource localData;


    @Inject
    public WidgetRepository(@Remote WidgetDataSource remoteData, @Local WidgetDataSource localData) {
        this.remoteData = remoteData;
        this.localData = localData;
    }


    @Override
    public void saveFormControl(List<FormControl> data) {
        Log.e("DATA", new Gson().toJson(data));
        localData.saveFormControl(data);
    }

    @Override
    public Observable<List<FormControl>> getFormControl(String moduleID, String seq) {
        return localData.getFormControl(moduleID, seq);
    }

    @Override
    public void saveModule(List<Modules> data) {
        localData.saveModule(data);
    }

    @Override
    public Observable<List<Modules>> getModules(String moduleID) {
        return localData.getModules(moduleID);
    }


    @Override
    public void requestWidget(List<ActionControls> data) {
        localData.requestWidget(data);
    }

    @Override
    public Observable<List<ActionControls>> getActionControl(String moduleID) {
        return localData.getActionControl(moduleID);
    }

    @Override
    public Observable<List<ActionControls>> getActionControlByFM(String moduleID, String formID) {
        return localData.getActionControlByFM(moduleID, formID);
    }

    @Override
    public void saveAction(List<ActionControls> data) {
        localData.saveAction(data);
    }

    @Override
    public Single<DynamicResponse> requestWidget(PayloadData data, String path) {
        return remoteData.requestWidget(data, path);
    }

    @Override
    public void saveStaticData(List<StaticDataDetails> data) {
        localData.saveStaticData(data);
    }

    @Override
    public Observable<List<StaticDataDetails>> getStaticData() {
        return localData.getStaticData();
    }
}
