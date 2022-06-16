package com.craft.silicon.centemobile.data.worker

import androidx.lifecycle.LiveData
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.WorkInfo
import androidx.work.WorkManager

interface WorkMangerDataSource {
    fun getWorkManger(): WorkManager
    fun getConfiguration(): Configuration
    fun getConstraint(): Constraints
    fun outputWorkInfo(): LiveData<List<WorkInfo>>
}