package com.elmacentemobile.data.source.remote.dynamic;

import com.elmacentemobile.data.repository.dynamic.DynamicDataSource;
import com.elmacentemobile.data.source.remote.callback.RequestData;
import com.elmacentemobile.data.source.remote.callback.ResponseData;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DynamicApiService extends DynamicDataSource {

    @Override
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("ElmaAuthDynamic/api/auth/apps")
    Single<ResponseData> requestBase(@Body RequestData data);
}
