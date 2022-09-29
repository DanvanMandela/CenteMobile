package com.elmacentemobile.data.repository.payment;

import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.remote.payment.PaymentApiService;
import com.elmacentemobile.data.source.remote.payment.PaymentRemoteDataSource;

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
