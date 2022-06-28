package com.craft.silicon.centemobile.data.repository.payment;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.payment.PaymentApiService;
import com.craft.silicon.centemobile.data.source.remote.payment.PaymentRemoteDataSource;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class PaymentRepositoryModule {

    @Provides
    @Remote
    public PaymentDataSource remoteData(PaymentApiService paymentApiService) {
        return new PaymentRemoteDataSource(paymentApiService);
    }
}
