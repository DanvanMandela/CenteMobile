package com.craft.silicon.centemobile.data.repository.payment;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.util.AppLogger;
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
