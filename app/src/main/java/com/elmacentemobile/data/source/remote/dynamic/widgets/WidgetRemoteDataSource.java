package com.elmacentemobile.data.source.remote.dynamic.widgets;

import com.elmacentemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class WidgetRemoteDataSource implements WidgetDataSource {
    private final WidgetApiService apiService;
    private final StorageDataSource storageDataSource;

    @Inject
    public WidgetRemoteDataSource(WidgetApiService apiService, StorageDataSource storageDataSource) {
        this.apiService = apiService;
        this.storageDataSource = storageDataSource;
    }

    @Override
    public Single<DynamicResponse> requestWidget(PayloadData data, String path) {
        return apiService.widgetRequestT(storageDataSource.getDeviceData().getValue().getToken(), data, path);
    }
}
