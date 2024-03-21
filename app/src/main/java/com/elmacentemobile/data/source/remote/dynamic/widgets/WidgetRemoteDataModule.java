package com.elmacentemobile.data.source.remote.dynamic.widgets;

import static com.elmacentemobile.data.source.remote.helper.DynamicURLKt.liveTest;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.elmacentemobile.data.model.DeviceData;
import com.elmacentemobile.data.model.SpiltURL;
import com.elmacentemobile.data.source.constants.Constants;
import com.elmacentemobile.data.source.pref.StorageDataSource;
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
public class WidgetRemoteDataModule {

    @Provides
    public WidgetApiService provideApiService(Gson gson,
                                              StorageDataSource storage,
                                              Context context) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        LiveData<DeviceData> deviceLive = BindingAdapterKt.deviceLive(storage.getDeviceData());
        String base = liveTest();

        if (storage.getDeviceData().getValue() != null && storage.getDeviceData()
                .getValue().getOther() != null) {
            base = new SpiltURL(Objects.requireNonNull(storage.getDeviceData()
                    .getValue().getOther())).getBase();
        }


        new AppLogger().appLog("Live:URL", new Gson().toJson(deviceLive.getValue()));
        new AppLogger().appLog("Old:URL", base);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(base)
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

        return retrofit.create(WidgetApiService.class);
    }


}
