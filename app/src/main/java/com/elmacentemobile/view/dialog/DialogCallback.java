package com.elmacentemobile.view.dialog;

import com.elmacentemobile.view.fragment.go.steps.OCRData;

public interface DialogCallback {

    default void onDialog() {

    }

    default void onOCR(OCRData ocrData) {
    }

    default void onSuccess() {

    }
}
