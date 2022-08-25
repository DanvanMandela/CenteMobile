package com.craft.silicon.centemobile.data.source.remote.ocr;

import com.craft.silicon.centemobile.data.model.ocr.DocumentRequestData;
import com.craft.silicon.centemobile.data.model.ocr.DocumentResponseData;
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestData;
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestResponseData;
import com.craft.silicon.centemobile.data.repository.ocr.OCRDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OCRApiService extends OCRDataSource {


    @Override
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("CraftSiliconAI/SubmitDocument_V1")
    Single<ImageRequestResponseData>
    processImage(@Header("APIKey") String key, @Body ImageRequestData requestData);

    @Override
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("CraftSiliconAI/GetDocumentDetails")
    Single<DocumentResponseData> processID(@Header("APIKey") String key,
                                           @Body DocumentRequestData requestData);


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
