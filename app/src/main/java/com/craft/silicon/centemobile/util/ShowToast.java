package com.craft.silicon.centemobile.util;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

public class ShowToast {
    public ShowToast(Context context, String message) {
        if (message != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void vibration(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);
    }

    public ShowToast(Context context, String message, boolean vibration) {
        if (vibration) vibration(context);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
