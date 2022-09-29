package com.elmacentemobile.data.service

import android.os.CountDownTimer
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.view.fragment.go.steps.OTP
import com.elmacentemobile.view.fragment.go.steps.OTPCountDownTimer
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserInteractionWatcher @Inject constructor(
    private val dataSource: StorageDataSource
) : InteractionDataSource, OTP {

    private var timerDisposable: Disposable? = null
    private val timeout = dataSource.timeout.value

    private var countDownTimer: CountDownTimer? = null

    override fun onUserInteracted() {
        setTimer()
    }

    override fun setTimer() {
        AppLogger.instance.appLog("Interaction", "YES")


//        dataSource.setInactivity(false)
//        timerDisposable?.dispose()
//        timerDisposable = Observable.timer(timeout ?: startTime, MAX_USER_INACTIVITY_TIME_UNIT)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                dataSource.setInactivity(true)
//            }, { it.printStackTrace() })
        updateTimeout()
    }


    private fun updateTimeout() {
        if (countDownTimer != null) timerControl(false)
        val timeout = dataSource.timeout.value
        val time = timeout ?: startTime
        countDownTimer = OTPCountDownTimer(startTime = time, interval = interval, this)
        setTime()
    }

    private fun setTime() {
        dataSource.setInactivity(false)
        timerControl(true)
        done(false)
    }

    private fun timerControl(startTimer: Boolean) {
        if (startTimer) {
            countDownTimer!!.start()
        } else {
            countDownTimer!!.cancel()
            countDownTimer = null
        }
    }


    companion object {
        private var startTime = (24 * 1000).toLong()
        private val MAX_USER_INACTIVITY_TIME_UNIT = TimeUnit.MILLISECONDS
        private const val interval = (1 * 1000).toLong()
    }

    override fun timer(str: String) {
        AppLogger.instance.appLog("TIMER", str)
    }

    override fun done(boolean: Boolean) {
        if (boolean) {
            timerControl(false)
            dataSource.setInactivity(true)
        }
    }
}