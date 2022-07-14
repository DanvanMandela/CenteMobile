package com.craft.silicon.centemobile.data.repository.payment;

import android.content.Context;

import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.repository.calls.AppDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.google.gson.JsonObject;

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
