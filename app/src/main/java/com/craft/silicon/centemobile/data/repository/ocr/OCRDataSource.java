package com.craft.silicon.centemobile.data.repository.ocr;

import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRRequest;

import io.reactivex.Single;

public interface OCRDataSource {

    default Single<OCRData> ocrRequest(
            OCRRequest request) {
        return null;
    }

    default Single<OCRData> ocrRequestEncoded(
            String iDType,
            String iDNumber,
            String iDFront,
            String selfie,
            String iDBack,
            String userID,
            String password) {
        return null;
    }

}


