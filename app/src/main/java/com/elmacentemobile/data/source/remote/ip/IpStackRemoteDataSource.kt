package com.elmacentemobile.data.source.remote.ip

import com.elmacentemobile.data.model.ip.IpStackModel
import com.elmacentemobile.data.repository.ip.IpStackDatasource
import com.elmacentemobile.data.scope.Token
import com.elmacentemobile.data.source.constants.Keys
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IpStackRemoteDataSource @Inject constructor(
    private val service: IpStackAPIService,
    @param:Token private val publicService: IpStackAPIService
) : IpStackDatasource {

    override fun ipStack(ip: String): Single<IpStackModel?> {
        return service.ipStack(ip = ip, key = Keys().ipStackKey())
    }

    override fun publicIp(): Single<HashMap<String, String>?> {
        return publicService.publicIp()
    }
}
