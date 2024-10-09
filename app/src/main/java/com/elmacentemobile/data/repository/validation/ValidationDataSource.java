package com.elmacentemobile.data.repository.validation;

import android.content.Context;

import com.elmacentemobile.data.model.action.ActionControls;
import com.elmacentemobile.data.model.module.Modules;
import com.elmacentemobile.data.repository.calls.AppDataSource;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;

import org.json.JSONObject;

import io.reactivex.Single;

public interface ValidationDataSource extends AppDataSource {


    default Single<DynamicResponse> validation(
            ActionControls action,
            JSONObject data,
            JSONObject encrypted,
            Modules modules,
            Context context) {
        return null;
    }


    default Single<DynamicResponse> validationRequest(String token,
                                                      PayloadData data, String path) {
        return null;
    }


}
