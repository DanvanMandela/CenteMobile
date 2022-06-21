package com.craft.silicon.centemobile.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.craft.silicon.centemobile.data.model.user.Accounts
import com.craft.silicon.centemobile.data.model.user.FrequentModules
import com.craft.silicon.centemobile.data.source.local.module.auth.AuthDao

@Database(
    entities = [FrequentModules::class, Accounts::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun authDao(): AuthDao

}