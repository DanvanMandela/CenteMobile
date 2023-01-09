package com.elmacentemobile.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AutoWake : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "wake-up") {
//            val resultIntent = Intent(context, ScanActivity::class.java)
//            resultIntent.data = Uri.parse(
//                "content://"
//                        + Calendar.getInstance().timeInMillis
//            )
//            resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            context?.startActivity(resultIntent)


            val startIntent = context!!.packageManager
                .getLaunchIntentForPackage(context.packageName)

            startIntent!!.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            context.startActivity(startIntent)
        }
    }
}