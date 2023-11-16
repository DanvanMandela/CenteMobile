package com.elmacentemobile.data.repository.ip

import com.elmacentemobile.data.model.ip.IpStackModel
import com.elmacentemobile.data.source.remote.ip.IpStackAPIService
import com.google.gson.Gson
import io.reactivex.Single

interface IpStackDatasource {

    fun provideAPIService(gson: Gson): IpStackAPIService {
        throw Exception("Not implemented")
    }

    fun providePublicAPIService(gson: Gson): IpStackAPIService {
        throw Exception("Not implemented")
    }

    fun ipStack(ip: String): Single<IpStackModel?> {
        throw Exception("Not implemented")
    }
    fun ipStack(ip: String, key: String): Single<IpStackModel?> {
        throw Exception("Not implemented")
    }

    fun publicIp(): Single<HashMap<String, String>?> {
        throw Exception("Not implemented")
    }

    fun remoteSource(apiService: IpStackAPIService): IpStackDatasource {
        throw Exception("Not implemented")
    }
}