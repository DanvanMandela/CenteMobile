package com.elmacentemobile.data.repository.dynamic;


import com.elmacentemobile.data.model.DeviceData;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.RequestData;
import com.elmacentemobile.data.source.remote.callback.ResponseData;

import io.reactivex.Single;

public interface DynamicDataSource {
    default Single<ResponseData> requestBase(RequestData data) {
        return null;
    }

    default void saveDeviceData(DeviceData data) {

    }

    default Single<DynamicResponse> requestStaticData() {
        return null;
    }



    default Single<DynamicResponse> carouselData() {
        return null;
    }


}
