package com.craft.silicon.centemobile.util;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

public class ShowToast {
    public ShowToast(Context context, String message) {
        if (message != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public ShowToast(Context context, String message, boolean vibration) {
        if (vibration) new VibrationHelper().vibrate(context);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
