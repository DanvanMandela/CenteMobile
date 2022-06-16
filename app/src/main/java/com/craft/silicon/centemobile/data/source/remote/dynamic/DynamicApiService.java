package com.craft.silicon.centemobile.data.source.remote.dynamic;

import com.craft.silicon.centemobile.data.repository.dynamic.DynamicDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.RequestData;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseData;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DynamicApiService extends DynamicDataSource {

    @Override
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("api/auth/apps")
    Single<ResponseData> requestBase(@Body RequestData data);
}
