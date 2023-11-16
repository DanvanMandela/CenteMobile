package com.elmacentemobile.data.repository.ip

import com.elmacentemobile.data.model.ip.IpStackModel
import com.elmacentemobile.data.scope.Remote
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IpStackRepository @Inject constructor(
    @param:Remote private val remote: IpStackDatasource
) : IpStackDatasource {
    override fun ipStack(ip: String): Single<IpStackModel?> {
        return remote.ipStack(ip)
    }

    override fun publicIp(): Single<HashMap<String, String>?> {
        return remote.publicIp()
    }
}