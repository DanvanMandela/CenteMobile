package com.craft.silicon.centemobile.view.model;

import static com.craft.silicon.centemobile.data.source.constants.Constants.getIMEIDeviceId;

import android.app.Activity;

import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.DeviceData;
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum;
import com.craft.silicon.centemobile.data.repository.dynamic.DynamicDataSource;
import com.craft.silicon.centemobile.data.repository.dynamic.DynamicRepository;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.RequestData;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseData;
import com.craft.silicon.centemobile.util.BaseClass;

import org.json.JSONObject;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Single;

@HiltViewModel
public class DynamicViewModel extends ViewModel implements DynamicDataSource {
    private final DynamicRepository dynamicRepository;
    private final StorageDataSource storageDataSource;

    @Inject
    public DynamicViewModel(DynamicRepository dynamicRepository, StorageDataSource storageDataSource) {
        this.dynamicRepository = dynamicRepository;
        this.storageDataSource = storageDataSource;
    }

    @Override
    public Single<ResponseData> requestBase(RequestData data) {
        return dynamicRepository.requestBase(data);
    }

    public Single<ResponseData> getBaseData(Activity activity) {
        String uniqueID = Constants.getUniqueID();
        JSONObject jsonObject = new JSONObject();
        Constants.commonJSON(jsonObject,
                activity,
                uniqueID,
                ActionTypeEnum.REQUEST_BASE.getType(),
                "",
                false,
                storageDataSource);
        return dynamicRepository.requestBase(
                new RequestData(
                        uniqueID,
                        Constants.Data.CUSTOMER_ID,
                        getIMEIDeviceId(activity),
                        Constants.Data.CODE_BASE,
                        "0.00",
                        "0.00",
                        BaseClass.hashLatest(jsonObject.toString()),
                        Constants.Data.APP_NAME
                )
        );
    }

    @Override
    public void saveDeviceData(DeviceData data) {
        storageDataSource.setDeviceData(data);
    }
}
