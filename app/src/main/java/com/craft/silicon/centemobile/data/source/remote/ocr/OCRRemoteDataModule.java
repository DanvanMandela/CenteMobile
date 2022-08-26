package com.craft.silicon.centemobile.data.source.remote.ocr;

import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.AuthorizationInterceptor;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class OCRRemoteDataModule {

    @Provides
    public OCRApiService provideApiService(Gson gson) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constants.BaseUrl.IMAGE_PROCESSING_URL)
                .client(new OkHttpClient.Builder()
                        .connectTimeout(Constants.Timeout.connection_ocr, TimeUnit.SECONDS)
                        .writeTimeout(Constants.Timeout.write, TimeUnit.SECONDS)
                        .readTimeout(Constants.Timeout.read, TimeUnit.SECONDS)
                        .addInterceptor(chain -> {
                            HttpUrl url = chain.request().url().newBuilder().build();
                            return chain.proceed(chain.request()
                                    .newBuilder().url(url)
                                    .build());
                        }).addInterceptor(httpLoggingInterceptor)
                        .addInterceptor(new AuthorizationInterceptor("APIKey",
                                Constants.Data.API_KEY)).build()
                ).build();
        return retrofit.create(OCRApiService.class);
    }
}

