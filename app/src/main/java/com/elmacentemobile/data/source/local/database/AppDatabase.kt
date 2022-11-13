package com.elmacentemobile.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.elmacentemobile.data.model.AtmData
import com.elmacentemobile.data.model.CarouselData
import com.elmacentemobile.data.model.action.ActionControls
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.converter.DateConverter
import com.elmacentemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.elmacentemobile.data.model.converter.GroupFormTypeConverter
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.model.static_data.StaticDataDetails
import com.elmacentemobile.data.model.user.*
import com.elmacentemobile.data.receiver.NotificationData
import com.elmacentemobile.data.source.local.module.auth.AuthDao
import com.elmacentemobile.data.source.local.module.dynamic.widgets.WidgetDao
import com.elmacentemobile.view.ep.data.LayoutData

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
        CarouselData::class,
        NotificationData::class,
        PendingTransaction::class
    ],
    version = 2, exportSchema = false
)


@TypeConverters(
    GroupFormTypeConverter::class,
    DateConverter::class,
    DynamicDataResponseTypeConverter::class,
    DisplayHashTypeConverter::class,
    PendingTrxFormControlsConverter::class,
    PendingTrxActionControlsControlsConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun authDao(): AuthDao

    abstract fun widgetDao(): WidgetDao

}