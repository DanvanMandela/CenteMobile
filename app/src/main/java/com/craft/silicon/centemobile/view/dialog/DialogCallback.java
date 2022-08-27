package com.craft.silicon.centemobile.view.dialog;

import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;

public interface DialogCallback {

    default void onDialog() {

    }

    default void onOCR(OCRData ocrData) {
    }
}
