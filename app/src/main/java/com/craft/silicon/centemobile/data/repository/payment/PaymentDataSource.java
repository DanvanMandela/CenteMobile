package com.craft.silicon.centemobile.data.repository.payment;

import android.content.Context;

import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import io.reactivex.Single;

public interface PaymentDataSource {


    default Single<DynamicResponse> paymentRequest(
            String token,
            PayloadData data, String path) {
        return null;
    }

    default Single<DynamicResponse> recentList(String moduleID, String merchantID, Context context) {
        return null;
    }

}
