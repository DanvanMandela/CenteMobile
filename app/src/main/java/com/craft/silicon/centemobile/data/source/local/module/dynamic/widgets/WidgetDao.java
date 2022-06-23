package com.craft.silicon.centemobile.data.source.local.module.dynamic.widgets;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetDataSource;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface WidgetDao extends WidgetDataSource {

    @Override
    @Insert(onConflict = REPLACE)
    void saveFormControl(List<FormControl> data);

    @Override
    @Query("SELECT * FROM form_control_tb WHERE moduleID=:moduleID")
    Observable<List<FormControl>> getFormControl(String moduleID);

    @Override
    @Insert(onConflict = REPLACE)
    void saveModule(List<Modules> data);

    @Override
    @Query("SELECT * FROM modules_tbl WHERE parentModule=:moduleID")
    Observable<List<Modules>> getModules(String moduleID);



    @Override
    @Query("SELECT * FROM action_control_tb WHERE controlID=:moduleID")
    Observable<List<ActionControls>> getActionControl(String moduleID);

    @Override
    @Insert(onConflict = REPLACE)
    void saveAction(List<ActionControls> data);
}
