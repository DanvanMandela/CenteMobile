package com.craft.silicon.centemobile.data.source.remote.dynamic.widgets;

import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

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
