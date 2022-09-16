package com.craft.silicon.centemobile.data.service

import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.util.AppLogger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class UserInteractionWatcher @Inject constructor(
    private val dataSource: StorageDataSource
) : InteractionDataSource {

    private var timerDisposable: Disposable? = null
    private val timeout = dataSource.timeout.value

    override fun onUserInteracted() {
        setTimer()
    }

    override fun setTimer() {
        AppLogger.instance.appLog("Interaction", "YES")


        dataSource.setInactivity(false)
        timerDisposable?.dispose()
        timerDisposable = Observable.timer(timeout ?: startTime, MAX_USER_INACTIVITY_TIME_UNIT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dataSource.setInactivity(true)
            }, { it.printStackTrace() })
    }


    companion object {
        private var startTime = (24 * 1000).toLong()
        private val MAX_USER_INACTIVITY_TIME_UNIT = TimeUnit.MILLISECONDS
    }
}