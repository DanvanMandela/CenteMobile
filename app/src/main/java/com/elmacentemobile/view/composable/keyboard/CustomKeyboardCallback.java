package com.elmacentemobile.view.composable.keyboard;

import com.google.android.material.textfield.TextInputEditText;

public interface CustomKeyboardCallback {
    default void onType(CustomKeyData data) {

    }

    default void onCustom(TextInputEditText pin, int max) {

    }
}
