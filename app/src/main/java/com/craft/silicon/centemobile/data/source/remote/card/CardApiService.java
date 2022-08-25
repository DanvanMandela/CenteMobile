package com.craft.silicon.centemobile.data.source.remote.card;

import com.craft.silicon.centemobile.data.repository.card.CardDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CardApiService extends CardDataSource {

    @Override
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("{path}")
    Single<DynamicResponse> cardRequest(@Header("T") String token,
                                        @Body PayloadData data,
                                        @Path(value = "path") String path);
}
