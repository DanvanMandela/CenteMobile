package com.craft.silicon.centemobile.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.craft.silicon.centemobile.data.model.AtmData
import com.craft.silicon.centemobile.data.model.CarouselData
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.model.converter.GroupFormTypeConverter
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.data.model.user.Accounts
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.craft.silicon.centemobile.data.model.user.FrequentModules
import com.craft.silicon.centemobile.data.source.local.module.auth.AuthDao
import com.craft.silicon.centemobile.data.source.local.module.dynamic.widgets.WidgetDao
import com.craft.silicon.centemobile.view.ep.data.LayoutData

@Database(
    entities = [FrequentModules::class,
        Accounts::class,
        Beneficiary::class,
        FormControl::class,
        Modules::class,
        ActionControls::class,
        StaticDataDetails::class,
        LayoutData::class,
        AtmData::class,
        CarouselData::class
    ],
    version = 1, exportSchema = false
)

@TypeConverters(
    GroupFormTypeConverter::class,
    DynamicDataResponseTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun authDao(): AuthDao

    abstract fun widgetDao(): WidgetDao

}