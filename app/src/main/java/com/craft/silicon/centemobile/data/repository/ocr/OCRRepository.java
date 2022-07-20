package com.craft.silicon.centemobile.data.repository.ocr;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRRequest;

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

}
