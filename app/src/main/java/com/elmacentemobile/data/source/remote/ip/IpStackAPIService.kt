package com.elmacentemobile.data.source.remote.ip

import com.elmacentemobile.data.model.ip.IpStackModel
import com.elmacentemobile.data.repository.ip.IpStackDatasource
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface IpStackAPIService : IpStackDatasource {


    @GET("/{ip}")
    @Headers("Content-Type: application/json; charset=utf-8")
    override fun ipStack(
        @Path("ip") ip: String,
        @Query("access_key") key: String
    ): Single<IpStackModel?>


    @GET("/?format=json")
    @Headers("Content-Type: application/json; charset=utf-8")
    override fun publicIp(): Single<HashMap<String, String>?>


}