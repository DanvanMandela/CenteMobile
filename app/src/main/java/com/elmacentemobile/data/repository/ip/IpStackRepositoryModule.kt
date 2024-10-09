package com.elmacentemobile.data.repository.ip

import com.elmacentemobile.data.scope.Remote
import com.elmacentemobile.data.source.remote.ip.IpStackAPIService
import com.elmacentemobile.data.source.remote.ip.IpStackRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class IpStackRepositoryModule : IpStackDatasource {

    @Provides
    @Remote
    override fun remoteSource(apiService: IpStackAPIService): IpStackDatasource {
        return IpStackRemoteDataSource(apiService,apiService)
    }
}