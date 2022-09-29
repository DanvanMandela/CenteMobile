package com.elmacentemobile.data.repository.payment;

import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;
import com.elmacentemobile.util.AppLogger;
import com.google.gson.Gson;

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
        AppLogger.Companion.getInstance().appLog("MAIN:DATA", new Gson().toJson(data));
        return remoteData.paymentRequest(token, data, path);
    }
}
