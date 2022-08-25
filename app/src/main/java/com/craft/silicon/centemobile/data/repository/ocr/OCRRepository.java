package com.craft.silicon.centemobile.data.repository.ocr;

import com.craft.silicon.centemobile.data.model.ocr.DocumentRequestData;
import com.craft.silicon.centemobile.data.model.ocr.DocumentResponseData;
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestData;
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestResponseData;
import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRRequest;

import java.util.List;

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
