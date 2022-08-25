package com.craft.silicon.centemobile.data.repository.card;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.card.CardApiService;
import com.craft.silicon.centemobile.data.source.remote.card.CardRemoteDataSource;

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
