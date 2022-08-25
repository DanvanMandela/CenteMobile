package com.craft.silicon.centemobile.data.repository.card;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class CardRepository implements CardDataSource {
    private final CardDataSource remoteData;

    @Inject
    public CardRepository(@Remote CardDataSource remote) {
        this.remoteData = remote;
    }

    @Override
    public Single<DynamicResponse> cardRequest(String token, PayloadData data, String path) {
        return remoteData.cardRequest(token, data, path);
    }
}
