package com.craft.silicon.centemobile.data.service

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import javax.inject.Inject


class CurrentActivity @Inject constructor(
    application: Application,
    private val storageDataSource: StorageDataSource
) :
    CurrentActivityDataSource, Application.ActivityLifecycleCallbacks {

    private var current: Activity? = null
    private var num: Int = 0

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun currentActivity(): Activity? {
        return current
    }

    override fun currentLive(): LiveData<Activity?> {
        return MutableLiveData(current)
    }

    override fun saveCurrentActivity() {

    }

    override fun removeCurrentActivity() {
        storageDataSource.activity("")
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        storageDataSource.activity(activity.componentName.className)
        num++
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        storageDataSource.activity(activity.componentName.className)
        current = activity
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        num--
        if (num == 0) {
            removeCurrentActivity()
            current = null
        }
    }
}