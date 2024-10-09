package com.elmacentemobile.data.repository.card;

import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.remote.card.CardApiService;
import com.elmacentemobile.data.source.remote.card.CardRemoteDataSource;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class CardRepositoryModule {

    @Remote
    @Provides
    public CardDataSource provideRemote(CardApiService cardApiService) {
        return new CardRemoteDataSource(cardApiService);
    }
}
