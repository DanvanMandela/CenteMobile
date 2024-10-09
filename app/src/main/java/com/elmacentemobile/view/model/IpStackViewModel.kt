package com.elmacentemobile.view.model

import androidx.lifecycle.ViewModel
import com.elmacentemobile.data.model.ip.IpStackModel
import com.elmacentemobile.data.repository.ip.IpStackDatasource
import com.elmacentemobile.data.repository.ip.IpStackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import javax.inject.Inject

@HiltViewModel
class IpStackViewModel @Inject constructor(
    private val repository: IpStackRepository
) : ViewModel(), IpStackDatasource {

    override fun ipStack(ip: String): Single<IpStackModel?> {
        return repository.ipStack(ip)
    }
}