package com.craft.silicon.centemobile.data.repository.calls;

import android.content.Context;

import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import org.json.JSONObject;

import io.reactivex.Single;

public interface AppDataSource {
    default Single<DynamicResponse> pinForgotATM(JSONObject jsonObject,
                                                 JSONObject encrypted,
                                                 Context context) {
        return null;
    }

    default Single<DynamicResponse> customerExist(JSONObject jsonObject,
                                                  JSONObject encrypted,
                                                  Context context) {
        return null;
    }

    default Single<DynamicResponse> createOTP(JSONObject jsonObject,
                                              Context context) {
        return null;
    }

    default Single<DynamicResponse> validateOTP(JSONObject jsonObject,
                                                JSONObject encrypted,
                                                Context context) {
        return null;
    }

    default Single<DynamicResponse> validateCard(JSONObject jsonObject,
                                                 JSONObject encrypted,
                                                 Context context) {
        return null;
    }

    default Single<DynamicResponse> saveUserDataSelf(JSONObject jsonObject,
                                                     Context context) {
        return null;
    }

    default Single<DynamicResponse> dbCall(PayloadData data) {
        return null;
    }

    default Single<DynamicResponse> validateCall(PayloadData data) {
        return null;
    }

    default Single<DynamicResponse> payBillCall(PayloadData data) {
        return null;
    }


    default Single<DynamicResponse> accountCall(PayloadData data) {
        return null;
    }

    default Single<DynamicResponse> authCall(PayloadData data) {
        return null;
    }

    default Single<DynamicResponse> dynamicCall(
            ActionControls action,
            JSONObject data,
            JSONObject encrypted,
            Modules modules,
            Context context) {
        return null;
    }
}