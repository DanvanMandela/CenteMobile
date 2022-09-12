package com.craft.silicon.centemobile.data.source.remote.forms;

import com.craft.silicon.centemobile.data.repository.forms.FormsDataSource;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class FormsRemoteDataSource implements FormsDataSource {

    private final FormsApiService formsApiService;
    private final StorageDataSource storageDataSource;

    @Inject
    public FormsRemoteDataSource(FormsApiService formsApiService,
                                 StorageDataSource storageDataSource) {
        this.formsApiService = formsApiService;
        this.storageDataSource = storageDataSource;
    }

    @Override
    public Single<DynamicResponse> requestWidget(PayloadData data, String path) {
        return formsApiService.widgetRequestT(storageDataSource.getDeviceData()
                .getValue().getToken(), data, path);
    }
}
