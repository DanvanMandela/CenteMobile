package com.elmacentemobile.data.repository.forms;


import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;

import io.reactivex.Single;

public interface FormsDataSource {
    default Single<DynamicResponse> requestWidget(PayloadData data, String path) {
        return null;
    }

    default Single<DynamicResponse> widgetRequestT(String token,
                                                   PayloadData data,
                                                   String path) {
        return null;
    }
}
