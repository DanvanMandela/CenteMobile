package com.elmacentemobile.data.receiver


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.elmacentemobile.R
import com.elmacentemobile.data.model.converter.DateConverter
import com.elmacentemobile.data.model.converter.NotificationConverter
import com.elmacentemobile.data.model.converter.NotificationDataConverter
import com.elmacentemobile.data.receiver.NotificationService.NotificationConstant.CHANNEL
import com.elmacentemobile.data.receiver.NotificationService.NotificationConstant.DESCRIPTION
import com.elmacentemobile.data.receiver.NotificationService.NotificationConstant.NOTIFICATION_ID
import com.elmacentemobile.data.repository.dynamic.widgets.worker.NotificationWorker
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.worker.WorkMangerDataSource
import com.elmacentemobile.data.worker.WorkerCommons
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.view.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var storageDataSource: StorageDataSource

    @Inject
    lateinit var worker: WorkMangerDataSource


    override fun onMessageReceived(p: RemoteMessage) {
        super.onMessageReceived(p)
        try {

            val data = NotificationConverter().to(Gson().toJson(p.data))
            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.sendNotification(
                data?.message,
                data?.title, this
            )

            AppLogger.instance.appLog(
                TAG,
                "Message Notification: " + Gson().toJson(p.data)
            )
            storageDataSource.setOtp(extractOTP(data?.message))
            notData(
                NotificationData(
                    title = data?.title,
                    body = data?.message,
                    image = data?.image,
                    time = Date()

                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        AppLogger.instance.appLog(TAG, "Refreshed token: $token")

    }


    private fun notData(data: NotificationData) {
        val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
            .addTag(WorkerCommons.TAG_OUTPUT)
        notificationWorker.setInputData(
            Data.Builder()
                .putString(
                    WorkerCommons.TAG_NOTIFICATION_DATA,
                    NotificationDataConverter().from(data)
                )
                .build()
        )
        val continuation = worker.getWorkManger()
            .beginUniqueWork(
                WorkerCommons.TAG_NOTIFICATION_WORKER +
                        BaseClass.generateAlphaNumericString(2),
                ExistingWorkPolicy.REPLACE,
                notificationWorker.build()
            )
        continuation.enqueue()
    }


    companion object {
        private var TAG = NotificationService::class.java.javaClass.simpleName
    }


    private fun NotificationManager.sendNotification(
        messageBody: String?,
        title: String?,
        applicationContext: Context
    ) {
        val builder: NotificationCompat.Builder


        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val contentPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.main_navigation_graph)
            .setDestination(R.id.landingPageFragment)
            .createPendingIntent()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL, DESCRIPTION,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = NotificationCompat.Builder(applicationContext, CHANNEL)
                .setSmallIcon(R.drawable.cente)
                .setContentTitle(applicationContext.getString(R.string.app_name))
                .setContentText(messageBody)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)


        } else {
            builder = NotificationCompat.Builder(
                applicationContext,
                applicationContext.getString(R.string.app_name)
            )
                .setSmallIcon(R.drawable.cente)
                .setContentTitle(applicationContext.getString(R.string.app_name))
                .setContentText(messageBody)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
        }

        notify(NOTIFICATION_ID, builder.build())
    }

    object NotificationConstant {
        const val NOTIFICATION_ID = 4400
        const val CHANNEL = "otp_channel"
        const val DESCRIPTION = "To show in app otp"
    }


}


fun extractOTP(str: String?): String {
    val mPattern: Pattern = Pattern.compile("(|^)\\d{6}")
    return if (str != null) {
        val mMatcher: Matcher = mPattern.matcher(str)
        return if (mMatcher.find()) {
            val otp: String = mMatcher.group(0)!!
            otp
        } else {
            ""
        }
    } else ""
}

@Entity(tableName = "notifications")
@Parcelize
data class NotificationData(


    @field:SerializedName("title")
    @field:Expose
    @field:ColumnInfo(name = "title")
    val title: String?,

    @field:SerializedName("body")
    @field:Expose
    @field:ColumnInfo(name = "body")
    val body: String?,

    @field:SerializedName("image")
    @field:Expose
    @field:ColumnInfo(name = "image")
    val image: String?,

    @field:SerializedName("time")
    @field:Expose
    @field:ColumnInfo(name = "time")
    @field:TypeConverters(DateConverter::class)
    val time: Date?,
) : Parcelable {

    @IgnoredOnParcel
    @field:SerializedName("id")
    @field:PrimaryKey(autoGenerate = true)
    @field:Expose
    @field:ColumnInfo(name = "id")
    var id: Int = 0
}

@Parcelize
data class Notification(
    @field:SerializedName("Title")
    @field:Expose
    val title: String?,

    @field:SerializedName("Message")
    @field:Expose
    val message: String?,

    @field:SerializedName("ImageURL")
    @field:Expose
    val image: String?,
) : Parcelable