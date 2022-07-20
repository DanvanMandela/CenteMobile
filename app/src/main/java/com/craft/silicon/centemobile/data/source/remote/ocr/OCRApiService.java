package com.craft.silicon.centemobile.data.source.remote.ocr;

import com.craft.silicon.centemobile.data.repository.ocr.OCRDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OCRApiService extends OCRDataSource {




    @Override
    @FormUrlEncoded
    @POST("IdImages.aspx")
    Single<OCRData> ocrRequestEncoded(@Field("IDType") String iDType,
                                      @Field("IDNumber") String iDNumber,
                                      @Field("IDFront") String iDFront,
                                      @Field("Selfie") String selfie,
                                      @Field("IDBack") String iDBack,
                                      @Field("UserID") String userID,
                                      @Field("Password") String password);
}
