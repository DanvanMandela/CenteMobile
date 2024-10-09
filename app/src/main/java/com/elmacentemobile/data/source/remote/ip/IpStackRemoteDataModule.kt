package com.elmacentemobile.data.source.remote.ip

import android.util.Log
import com.elmacentemobile.data.repository.ip.IpStackDatasource
import com.elmacentemobile.data.scope.Token
import com.elmacentemobile.data.source.constants.Constants
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@InstallIn(SingletonComponent::class)
@Module
class IpStackRemoteDataModule : IpStackDatasource {


    @Provides
    override fun provideAPIService(gson: Gson): IpStackAPIService {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Log.e(
                IpStackRemoteDataModule::class.simpleName,
                message
            )
        }
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(Constants.BaseUrl.IP_STACK_URL)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(Constants.Timeout.connection, TimeUnit.SECONDS)
                    .writeTimeout(Constants.Timeout.write, TimeUnit.SECONDS)
                    .readTimeout(Constants.Timeout.read, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val url = chain
                            .request()
                            .url
                            .newBuilder()
                            .build()
                        chain.proceed(chain.request().newBuilder().url(url).build())
                    }
                    .addInterceptor(httpLoggingInterceptor)
                    .build()
            ).build()

        return retrofit.create(IpStackAPIService::class.java)
    }

    @Provides
    @Token
    override fun providePublicAPIService(gson: Gson): IpStackAPIService {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Log.e(
                IpStackRemoteDataModule::class.simpleName,
                message
            )
        }
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(Constants.BaseUrl.IP_PUBLIC_URL)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(Constants.Timeout.connection, TimeUnit.SECONDS)
                    .writeTimeout(Constants.Timeout.write, TimeUnit.SECONDS)
                    .readTimeout(Constants.Timeout.read, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val url = chain
                            .request()
                            .url
                            .newBuilder()
                            .build()
                        chain.proceed(chain.request().newBuilder().url(url).build())
                    }
                    .addInterceptor(httpLoggingInterceptor)
                    .build()
            ).build()

        return retrofit.create(IpStackAPIService::class.java)
    }
}