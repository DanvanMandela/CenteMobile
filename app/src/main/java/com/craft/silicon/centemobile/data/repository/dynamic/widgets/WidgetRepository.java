package com.craft.silicon.centemobile.data.repository.dynamic.widgets;

import android.util.Log;

import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class WidgetRepository implements WidgetDataSource {

    private final WidgetDataSource remoteData;

    @Inject
    public WidgetRepository(@Remote WidgetDataSource remoteData) {
        this.remoteData = remoteData;
    }


    @Override
    public void saveFormControl(List<FormControl> data) {
        WidgetDataSource.super.saveFormControl(data);
    }


    @Override
    public Single<DynamicResponse> requestWidget(PayloadData data, String path) {
        Log.e("PATH",new Gson().toJson(data));

        return remoteData.requestWidget(data, path);
    }
}
