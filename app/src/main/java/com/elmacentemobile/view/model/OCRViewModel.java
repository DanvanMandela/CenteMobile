package com.elmacentemobile.view.model;

import androidx.lifecycle.ViewModel;

import com.elmacentemobile.data.model.ocr.DocumentRequestData;
import com.elmacentemobile.data.model.ocr.DocumentResponseData;
import com.elmacentemobile.data.model.ocr.ImageRequestData;
import com.elmacentemobile.data.model.ocr.ImageRequestResponseData;
import com.elmacentemobile.data.repository.ocr.OCRDataSource;
import com.elmacentemobile.data.repository.ocr.OCRRepository;
import com.elmacentemobile.view.fragment.go.steps.OCRData;
import com.elmacentemobile.view.fragment.go.steps.OCRRequest;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Single;

@HiltViewModel
public class OCRViewModel extends ViewModel implements OCRDataSource {
    private final OCRRepository ocrRepository;

    @Inject
    public OCRViewModel(OCRRepository ocrRepository) {
        this.ocrRepository = ocrRepository;
    }

    @Override
    public Single<OCRData> ocrRequest(OCRRequest request) {
        return ocrRepository.ocrRequest(request);
    }

    @Override
    public Single<DocumentResponseData> processID(String key, DocumentRequestData requestData) {
        return ocrRepository.processID(key, requestData);
    }

    @Override
    public Single<ImageRequestResponseData> processImage(String key, ImageRequestData requestData) {
        return ocrRepository.processImage(key, requestData);
    }
}
