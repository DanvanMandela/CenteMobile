package com.elmacentemobile.view.fragment.go.ocr

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
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
            val currentData = baseModel.dataSource.onIDDetails.asLiveData().value
            val ocrData = EventBus.getDefault()
                .getStickyEvent(com.iceberg.ocr.utils.OCRData::class.java)

            Log.e("YUP", Gson().toJson(ocrData))

            if (ocrData != null && !ocrData.actionType.isNullOrBlank()) {
                if (ocrData.actionType.equals("id")) {
                    val names = ocrData.givenName.split(" ")
                    val firstName = if (names.size > 1) names[0] else ocrData.givenName
                    val secondName = if (names.size > 1) names[1] else String()
                    baseModel.dataSource.setIDDetails(
                        IDDetails(
                            data = OCRData(
                                names = firstName,
                                otherName = secondName,
                                dob = ocrData.dateOfBirth ?: currentData?.data?.dob,
                                idNo = ocrData.nin ?: currentData?.data?.idNo,
                                gender = ocrData.sex ?: currentData?.data?.gender,
                                surname = ocrData.surname ?: currentData?.data?.surname,
                                expires = ocrData.dateOfExpiry ?: currentData?.data?.expires,
                                docId = ocrData.cardNumber ?: currentData?.data?.docId,
                                actionType = ocrData.actionType,
                            ),
                            id = (ocrData.encodedImageFront ?: currentData?.id?.image)?.let {
                                ImageData(image = it)
                            }
                        )
                    )
                } else if (ocrData.actionType.equals("passport")) {
                    baseModel.dataSource.setIDDetails(
                        IDDetails(
                            data = OCRData(
                                otherName = ocrData.otherNames ?: currentData?.data?.otherName,
                                dob = ocrData.dateOfBirth ?: currentData?.data?.dob,
                                idNo = ocrData.passportNo ?: currentData?.data?.idNo,
                                gender = ocrData.sex ?: currentData?.data?.gender,
                                surname = ocrData.surname ?: currentData?.data?.surname,
                                expires = ocrData.dateOfExpiry ?: currentData?.data?.expires,
                                passportType = ocrData.passportType
                                    ?: currentData?.data?.passportType,
                                dateOfIssue = ocrData.dateOfIssue ?: currentData?.data?.dateOfIssue,
                                countryName = ocrData.countryName ?: currentData?.data?.countryName,
                                actionType = ocrData.actionType,
                            ),
                            passport = (ocrData.encodedImagePassport
                                ?: currentData?.passport?.image)?.let { ImageData(image = it) }
                        )
                    )
                } else if (ocrData.actionType.equals("selfie")) {
                    currentData?.apply {
                        data?.apply {
                            actionType = ocrData.actionType
                        }
                        selfie = ImageData(image = ocrData.encodedSelfie)
                    }
                    baseModel.dataSource.setIDDetails(currentData)

                }
            } else Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()

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
        EventBus.getDefault().removeStickyEvent(com.iceberg.ocr.utils.OCRData::class.java)
        EventBus.getDefault().unregister(this)
    }

    override fun onStop() {
        killEvent()
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: com.iceberg.ocr.utils.OCRData) {
        AppLogger.instance.appLog(OCRResultActivity::class.java.simpleName, Gson().toJson(event))
    }
}
