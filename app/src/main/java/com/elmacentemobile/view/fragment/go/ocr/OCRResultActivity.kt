package com.elmacentemobile.view.fragment.go.ocr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.view.fragment.go.steps.IDDetails
import com.elmacentemobile.view.fragment.go.steps.ImageData
import com.elmacentemobile.view.fragment.go.steps.OCRData
import com.elmacentemobile.view.model.BaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class OCRResultActivity : ComponentActivity() {
    private val baseModel: BaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val ocrData = EventBus.getDefault()
                .getStickyEvent(com.example.icebergocr.utils.OCRData::class.java)

            val names = ocrData.givenName.split(" ")
            val firstName = if (names.size > 1) names[0] else ocrData.givenName
            val secondName = if (names.size > 1) names[1] else String()

            baseModel.dataSource.setIDDetails(
                IDDetails(
                    data = OCRData(
                        names = firstName,
                        otherName = secondName,
                        dob = ocrData.dateOfBirth,
                        idNo = ocrData.nin,
                        gender = ocrData.sex,
                        surname = ocrData.surname,
                        expires = ocrData.dateOfExpiry,
                        docId = ocrData?.cardNumber
                    ), id = ImageData(image = ocrData?.encodedImageFront!!)
                )
            )
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        startEvent()
    }

    private fun startEvent() {
        EventBus.getDefault().register(this)
    }

    private fun killEvent() {
        EventBus.getDefault().removeStickyEvent(com.example.icebergocr.utils.OCRData::class.java)
        EventBus.getDefault().unregister(this)
    }

    override fun onStop() {
        killEvent()
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: com.example.icebergocr.utils.OCRData) {
        AppLogger.instance.appLog(OCRResultActivity::class.java.simpleName, Gson().toJson(event))
    }
}
