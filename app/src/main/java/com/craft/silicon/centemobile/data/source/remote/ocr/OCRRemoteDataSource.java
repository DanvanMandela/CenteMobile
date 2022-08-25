package com.craft.silicon.centemobile.data.source.remote.ocr;

import com.craft.silicon.centemobile.data.model.ocr.DocumentRequestData;
import com.craft.silicon.centemobile.data.model.ocr.DocumentResponseData;
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestData;
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestResponseData;
import com.craft.silicon.centemobile.data.repository.ocr.OCRDataSource;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRRequest;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class OCRRemoteDataSource implements OCRDataSource {

    private final OCRApiService ocrApiService;

    @Inject
    public OCRRemoteDataSource(OCRApiService ocrApiService) {
        this.ocrApiService = ocrApiService;
    }

    @Override
    public Single<OCRData> ocrRequest(OCRRequest request) {
        return ocrApiService.ocrRequestEncoded(request.getIDType(),
                request.getIDNumber(),
                request.getIDFront(),
                request.getSelfie(),
                request.getIDBack(),
                request.getUserID(),
                request.getPassword());
    }

    @Override
    public Single<ImageRequestResponseData> processImage(String key, ImageRequestData requestData) {
        return ocrApiService.processImage(key, requestData);
    }

    @Override
    public Single<DocumentResponseData> processID(String key, DocumentRequestData requestData) {
        return ocrApiService.processID(key, requestData);
    }
}
