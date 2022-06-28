package com.craft.silicon.centemobile.data.repository.payment;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class PaymentRepository implements PaymentDataSource {
    private final PaymentDataSource remoteData;

    @Inject
    public PaymentRepository(@Remote PaymentDataSource remoteData) {
        this.remoteData = remoteData;
    }

    @Override
    public Single<DynamicResponse> paymentRequest(String token, PayloadData data, String path) {
        return remoteData.paymentRequest(token, data, path);
    }
}
