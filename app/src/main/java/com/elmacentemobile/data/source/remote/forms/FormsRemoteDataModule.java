package com.elmacentemobile.data.source.remote.forms;


import androidx.lifecycle.LiveData;

import com.elmacentemobile.data.model.DeviceData;
import com.elmacentemobile.data.model.SpiltURL;
import com.elmacentemobile.data.source.constants.Constants;
import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.data.source.remote.helper.DynamicURLKt;
import com.elmacentemobile.util.AppLogger;
import com.elmacentemobile.view.binding.BindingAdapterKt;
import com.google.gson.Gson;

import java.util.Objects;
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
public class FormsRemoteDataModule {


    @Provides
    public FormsApiService provideApiService(Gson gson,
                                             StorageDataSource storage) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        LiveData<DeviceData> deviceLive = BindingAdapterKt.deviceLive(storage.getDeviceData());

        String base = storage.getDeviceData().getValue().getStaticData();
        String forms = DynamicURLKt.liveTest();
        if (base != null && !base.isBlank()) {
            forms = new SpiltURL(Objects.requireNonNull(storage
                    .getDeviceData().getValue().getStaticData())).getBase();
        }

        new AppLogger().appLog("Live:URL", new Gson().toJson(deviceLive.getValue()));
        new AppLogger().appLog("Old:URL", forms);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(forms)
                .client(new OkHttpClient.Builder()
                        .connectTimeout(Constants.Timeout.connection_ocr, TimeUnit.SECONDS)
                        .writeTimeout(Constants.Timeout.write, TimeUnit.SECONDS)
                        .readTimeout(Constants.Timeout.read, TimeUnit.SECONDS)
                        .addInterceptor(httpLoggingInterceptor)
                        .addInterceptor(chain -> {
                            HttpUrl url = chain.request().url().newBuilder().build();
                            return chain.proceed(chain.request()
                                    .newBuilder().url(url)
                                    .build());
                        }).build()
                ).build();

        return retrofit.create(FormsApiService.class);
    }

}
