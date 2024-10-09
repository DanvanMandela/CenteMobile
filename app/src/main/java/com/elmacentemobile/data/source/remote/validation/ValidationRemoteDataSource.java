package com.elmacentemobile.data.source.remote.validation;

import com.elmacentemobile.data.repository.validation.ValidationDataSource;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class ValidationRemoteDataSource implements ValidationDataSource {
    private final ValidationApiService apiService;

    @Inject
    public ValidationRemoteDataSource(ValidationApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<DynamicResponse> validationRequest(String token, PayloadData data, String path) {
        return apiService.validationRequest(token, data, path);
    }
}
