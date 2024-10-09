package com.elmacentemobile.view.fragment.go.ocr

import android.os.Bundle
import android.util.Log
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

//            val fullName: String = ocrData.getOtherNames() + " " + ocrData.getSurname()
//            Log.e("GetData", "State: $fullName")
//            preferenceHelper.putOCRFullName(fullName)
//            preferenceHelper.putOCRDateOfBirth(ocrData.getDateOfBirth())
//            preferenceHelper.putOCRDateOfExpiry(ocrData.getDateOfExpiry())
            //            preferenceHelper.putOCRDateOfIssue(ocrData.getDateOfIssue())
//            preferenceHelper.putOCRPassportNo(ocrData.getPassportNo())
//            preferenceHelper.putOCRPassportType(ocrData.getPassportType())
//            preferenceHelper.putOCRCountryCode(ocrData.getCountryCode())
//            preferenceHelper.putOCRCountryName(ocrData.getCountryName())
//            preferenceHelper.putOCRGender(ocrData.getSex())
//            preferenceHelper.putEncodedImagePassport(ocrData.getEncodedImagePassport())

            val ocrData = EventBus.getDefault()
                .getStickyEvent(com.iceberg.ocr.utils.OCRData::class.java)

            if (ocrData != null) {
                Log.e("GetOCRData", "OCR Data: ${Gson().toJson(ocrData)}")
                Log.e("GetOCRData", "Action Type: ${ocrData.actionType}")


                if (ocrData.actionType.equals("id")) {
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
                                docId = ocrData?.cardNumber,
                                        actionType = ocrData.actionType,
                            ), id = ImageData(image = ocrData?.encodedImageFront!!)
                        )
                    )
                } else if (ocrData.actionType.equals("passport")) {
                    baseModel.dataSource.setIDDetails(
                        IDDetails(
                            data = OCRData(
                                otherName = ocrData.otherNames,
                                dob = ocrData.dateOfBirth,
                                idNo = ocrData.passportNo,
                                gender = ocrData.sex,
                                surname = ocrData.surname,
                                expires = ocrData.dateOfExpiry,
                                passportType = ocrData?.passportType,
                                dateOfIssue = ocrData?.dateOfIssue,
                                countryName = ocrData.countryName,
                                actionType = ocrData.actionType,
                            ), passport = ImageData(image = ocrData?.encodedImagePassport!!)
                        )
                    )
                } else if (ocrData.actionType.equals("selfie")) {
                    baseModel.dataSource.setIDDetails(
                        IDDetails(
                            selfie = ImageData(image = ocrData?.encodedSelfie!!)
                        )
                    )
                }

            }


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
