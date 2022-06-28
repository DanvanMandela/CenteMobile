package com.craft.silicon.centemobile.data.source.remote.payment;

import com.craft.silicon.centemobile.data.repository.payment.PaymentDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class PaymentRemoteDataSource implements PaymentDataSource {
    private final PaymentApiService apiService;

    @Inject
    public PaymentRemoteDataSource(PaymentApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<DynamicResponse> paymentRequest(String token, PayloadData data, String path) {
        return apiService.paymentRequest(token, data, path);
    }
}
