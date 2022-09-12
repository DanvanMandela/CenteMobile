package com.craft.silicon.centemobile.data.source.remote.forms;

import static com.craft.silicon.centemobile.data.source.remote.helper.DynamicURLKt.getSTATIC_BASE_URL;

import androidx.lifecycle.LiveData;

import com.craft.silicon.centemobile.data.model.DeviceData;
import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.util.AppLogger;
import com.craft.silicon.centemobile.view.binding.BindingAdapterKt;
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



        String base = new SpiltURL(storage.getDeviceData().getValue() == null ?
                getSTATIC_BASE_URL() : Objects.requireNonNull(storage
                .getDeviceData().getValue().getStaticData())).getBase();

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

        return retrofit.create(FormsApiService.class);
    }

}
