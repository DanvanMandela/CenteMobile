package com.craft.silicon.centemobile.view.model;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepository;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.data.source.remote.callback.WidgetRequest;
import com.craft.silicon.centemobile.util.BaseClass;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class WidgetViewModel extends ViewModel implements WidgetDataSource {

    private final WidgetRepository widgetRepository;
    private final StorageDataSource storageDataSource;

    @Inject
    public WidgetViewModel(WidgetRepository widgetRepository, StorageDataSource storageDataSource) {
        this.widgetRepository = widgetRepository;
        this.storageDataSource = storageDataSource;
    }


    @Override
    public Single<DynamicResponse> widgets(Activity activity, String action) {
        String iv = storageDataSource.getDeviceData().getValue().getRun();
        String device = storageDataSource.getDeviceData().getValue().getDevice();
        String uniqueID = Constants.getUniqueID();
        JSONObject jsonObject = new JSONObject();


        Constants.commonJSON(jsonObject,
                activity,
                uniqueID,
                action,
                "",
                true);


        String newRequest = jsonObject.toString();


        String path = new SpiltURL(storageDataSource.getDeviceData().getValue() == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(storageDataSource.getDeviceData().getValue().getOther())).getPath();
        Log.e("Tat", newRequest);

        return widgetRepository.requestWidget(new PayloadData(
                uniqueID,
                BaseClass.encryptString(newRequest, device, iv)
        ), path).doOnSubscribe(disposable -> {
        });
    }
}
