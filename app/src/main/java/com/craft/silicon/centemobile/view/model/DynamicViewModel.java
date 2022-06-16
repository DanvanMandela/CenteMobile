package com.craft.silicon.centemobile.view.model;

import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.DeviceData;
import com.craft.silicon.centemobile.data.repository.dynamic.DynamicDataSource;
import com.craft.silicon.centemobile.data.repository.dynamic.DynamicRepository;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.RequestData;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseData;

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

    @Override
    public void saveDeviceData(DeviceData data) {
        storageDataSource.setDeviceData(data);
    }
}
