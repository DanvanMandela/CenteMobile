package com.craft.silicon.centemobile.data.repository.ocr;

import com.craft.silicon.centemobile.data.model.ocr.DocumentRequestData;
import com.craft.silicon.centemobile.data.model.ocr.DocumentResponseData;
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestData;
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestResponseData;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRRequest;

import java.util.List;

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


    default Single<DocumentResponseData>
    processID(String key, DocumentRequestData requestData) {
        return null;
    }

    default Single<ImageRequestResponseData>
    processImage(String key, ImageRequestData requestData) {
        return null;
    }

}


