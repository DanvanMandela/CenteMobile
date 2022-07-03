package com.craft.silicon.centemobile.data.repository.account;

import android.content.Context;

import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import org.json.JSONObject;

import io.reactivex.Single;

public interface AccountDataSource {

    default Single<DynamicResponse> accountRequestT(String token, PayloadData data, String path) {
        return null;
    }

    default Single<DynamicResponse> checkBalance(JSONObject data, Context context, String moduleID) {
        return null;
    }
}
