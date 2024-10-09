package com.elmacentemobile.data.repository.ocr;

import com.elmacentemobile.data.model.ocr.DocumentRequestData;
import com.elmacentemobile.data.model.ocr.DocumentResponseData;
import com.elmacentemobile.data.model.ocr.ImageRequestData;
import com.elmacentemobile.data.model.ocr.ImageRequestResponseData;
import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.view.fragment.go.steps.OCRData;
import com.elmacentemobile.view.fragment.go.steps.OCRRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class OCRRepository implements OCRDataSource {
    private final OCRDataSource remoteData;

    @Inject
    public OCRRepository(@Remote OCRDataSource remoteData) {
        this.remoteData = remoteData;
    }

    @Override
    public Single<OCRData> ocrRequest(OCRRequest request) {
        return remoteData.ocrRequest(request);
    }


    @Override
    public Single<DocumentResponseData> processID(String key, DocumentRequestData requestData) {
        return remoteData.processID(key, requestData);
    }

    @Override
    public Single<ImageRequestResponseData> processImage(String key, ImageRequestData requestData) {
        return remoteData.processImage(key, requestData);
    }
}
