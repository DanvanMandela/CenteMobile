package com.craft.silicon.centemobile.data.repository.payment;

import android.content.Context;

import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import io.reactivex.Single;

public interface PaymentDataSource {


    default Single<DynamicResponse> paymentRequest(
            String token,
            PayloadData data, String path) {
        return null;
    }

    default Single<DynamicResponse> pay(JSONObject data, JSONObject encrypted, Context context, String moduleID) {
        return null;
    }

}
