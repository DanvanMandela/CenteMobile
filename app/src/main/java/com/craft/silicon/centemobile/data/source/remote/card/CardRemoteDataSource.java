package com.craft.silicon.centemobile.data.source.remote.card;

import com.craft.silicon.centemobile.data.repository.card.CardDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;

import io.reactivex.Single;

public class CardRemoteDataSource implements CardDataSource {
    private final CardApiService cardApiService;

    @Inject
    public CardRemoteDataSource(CardApiService cardApiService) {
        this.cardApiService = cardApiService;
    }

    @Override
    public Single<DynamicResponse> cardRequest(String token, PayloadData data, String path) {
        return cardApiService.cardRequest(token, data, path);
    }
}
