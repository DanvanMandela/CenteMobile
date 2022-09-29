package com.elmacentemobile.data.source.remote.dynamic.widgets;

import com.elmacentemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WidgetApiService extends WidgetDataSource {

    @Override
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("{path}")
    Single<DynamicResponse> widgetRequestT(
            @Header("T") String token,
            @Body PayloadData data,
            @Path(value = "path") String path);
}
