package com.craft.silicon.centemobile.data.source.remote.auth;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.craft.silicon.centemobile.data.model.DeviceData;
import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.scope.BaseUrl;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.AuthorizationInterceptor;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import kotlinx.coroutines.flow.StateFlow;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class AuthRemoteDataModule {

    @Provides
    public AuthApiService provideRequestDynamicApiService(Gson gson, StorageDataSource storage) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        String base = new SpiltURL(storage.getDeviceData().getValue() == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(storage.getDeviceData().getValue().getAuth())).getBase();
        String token = storage.getDeviceData().getValue() == null ? "" : storage.getDeviceData().getValue().getToken();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(base)
                .client(new OkHttpClient.Builder()
                        .connectTimeout(Constants.Timeout.connection, TimeUnit.SECONDS)
                        .writeTimeout(Constants.Timeout.write, TimeUnit.SECONDS)
                        .readTimeout(Constants.Timeout.read, TimeUnit.SECONDS)
                        .addInterceptor(chain -> {
                            HttpUrl url = chain.request().url().newBuilder().build();
                            return chain.proceed(chain.request()
                                    .newBuilder().url(url)
                                    .build());
                        }).addInterceptor(httpLoggingInterceptor).addInterceptor(new AuthorizationInterceptor("T", token)).build()
                ).build();

        return retrofit.create(AuthApiService.class);
    }
}
