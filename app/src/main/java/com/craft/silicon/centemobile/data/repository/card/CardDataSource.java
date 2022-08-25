package com.craft.silicon.centemobile.data.repository.card;

import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import io.reactivex.Single;

public interface CardDataSource {

    default Single<DynamicResponse> cardRequest(
            String token,
            PayloadData data, String path) {
        return null;
    }

}
