package com.craft.silicon.centemobile.data.repository.validation;

import android.content.Context;

import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import org.json.JSONObject;

import io.reactivex.Single;

public interface ValidationDataSource {

    default Single<DynamicResponse> validationRequest(String token, PayloadData data, String path) {
        return null;
    }

    default Single<DynamicResponse> validation(String moduleID,
                                               String merchantID,
                                               JSONObject data,
                                               Context context) {
        return null;
    }
}
