package com.elmacentemobile.data.repository.ocr.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.elmacentemobile.data.model.ocr.DocumentRequestData
import com.elmacentemobile.data.model.ocr.DocumentRequestDataTypeConverter
import com.elmacentemobile.data.model.ocr.ImageRequestDataTypeConverter
import com.elmacentemobile.data.repository.ocr.OCRRepository
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.worker.WorkerCommons
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@HiltWorker
class ImageProcessingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val requestRepository: OCRRepository
) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {
        return try {
            val image =
                ImageRequestDataTypeConverter().to(inputData.getString(WorkerCommons.ID_DATA))
            requestRepository.processImage(Constants.Data.API_KEY, image)
                .doOnError {
                    constructResponse(Result.failure())
                }
                .map {
                    constructResponse(
                        Result.success(
                            Data.Builder()
                                .putString(
                                    WorkerCommons.ID_DATA,
                                    DocumentRequestDataTypeConverter().from(
                                        DocumentRequestData(
                                            country = image?.country,
                                            processID = it.processID,
                                        )
                                    )
                                ).build()
                        )
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
        } catch (e: Exception) {
            e.printStackTrace()
            Single.just(
                constructResponse(
                    Result.success(
                        Data.Builder()
                            .putString(WorkerCommons.IS_WORK_ERROR, "Unable to complete process")
                            .build()
                    )
                )
            )
        }
    }

    private fun constructResponse(result: Result): Result {
        return result
    }
}