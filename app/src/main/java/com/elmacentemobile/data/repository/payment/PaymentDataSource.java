package com.elmacentemobile.data.repository.payment;

import android.content.Context;

import com.elmacentemobile.data.model.action.ActionControls;
import com.elmacentemobile.data.model.module.Modules;
import com.elmacentemobile.data.repository.calls.AppDataSource;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;

import org.json.JSONObject;

import io.reactivex.Single;

public interface PaymentDataSource extends AppDataSource {


    default Single<DynamicResponse> paymentRequest(
            String token,
            PayloadData data, String path) {
        return null;
    }

    default Single<DynamicResponse> pay(JSONObject data, JSONObject encrypted, Context context, String moduleID) {
        return null;
    }

    default Single<DynamicResponse> pay(
            ActionControls action,
            JSONObject data,
            JSONObject encrypted,
            Modules modules,
            Context context) {
        return null;
    }
}
