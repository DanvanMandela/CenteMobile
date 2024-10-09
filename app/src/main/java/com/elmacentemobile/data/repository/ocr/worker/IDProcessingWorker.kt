package com.elmacentemobile.data.repository.ocr.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.elmacentemobile.data.model.ocr.DocumentRequestDataTypeConverter
import com.elmacentemobile.data.repository.ocr.OCRRepository
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.worker.WorkerCommons
import com.elmacentemobile.view.fragment.go.steps.OCRConverter
import com.elmacentemobile.view.fragment.go.steps.OCRData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@HiltWorker
class IDProcessingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val requestRepository: OCRRepository,
    private val storage: StorageDataSource
) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {


        return try {
            var ocrData: OCRData? = null
            val data =
                DocumentRequestDataTypeConverter().to(inputData.getString(WorkerCommons.ID_DATA))
            return requestRepository.processID(Constants.Data.API_KEY, data)
                .doOnError {
                    constructResponse(Result.failure())
                }
                .map { ocr ->


                    if (ocr.status == "000") {
                        if (!ocr.fields.isNullOrEmpty()) {
                            ocr.fields.forEach { fields ->
                                if (!fields.cardNumber.isNullOrEmpty()) {
                                    ocrData = OCRData(
                                        names = fields.name?.get(0)?.text!!,
                                        surname = fields.surname?.get(0)?.text!!,
                                        idNo = fields.nin!![0].text!!,
                                        gender = fields.sex?.get(0)?.text,
                                        otherName = "",
                                        dob = fields.dob?.get(0)?.text!!,
                                    )

                                } else constructResponse(
                                    Result.success(
                                        Data.Builder().putString(
                                            WorkerCommons.IS_WORK_ERROR,
                                            "unable to fetch details try later"
                                        ).build()
                                    )
                                )
                            }
                        } else constructResponse(
                            Result.success(
                                Data.Builder().putString(
                                    WorkerCommons.IS_WORK_ERROR,
                                    "unable to fetch details try later"
                                ).build()
                            )
                        )
                    }
                    constructResponse(
                        Result.success(
                            Data.Builder().putString(
                                WorkerCommons.IS_OCR_DONE,
                                OCRConverter().from(ocrData)
                            ).build()
                        )
                    )
                }
                .onErrorReturn { constructResponse(Result.retry()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
        } catch (e: Exception) {
            e.printStackTrace()
            Single.just(
                constructResponse(
                    Result.success(
                        Data.Builder().putString(
                            WorkerCommons.IS_WORK_ERROR,
                            "unable to fetch details try later"
                        ).build()
                    )
                )
            )
        }

    }

    private fun constructResponse(result: Result): Result {
        return result
    }
}