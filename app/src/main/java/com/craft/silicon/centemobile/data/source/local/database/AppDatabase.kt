package com.craft.silicon.centemobile.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.Accounts
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.craft.silicon.centemobile.data.model.user.FrequentModules
import com.craft.silicon.centemobile.data.source.local.module.auth.AuthDao
import com.craft.silicon.centemobile.data.source.local.module.dynamic.widgets.WidgetDao

@Database(
    entities = [FrequentModules::class,
        Accounts::class,
        Beneficiary::class,
        FormControl::class,
        Modules::class,
        ActionControls::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun authDao(): AuthDao

    abstract fun widgetDao(): WidgetDao

}