package com.craft.silicon.centemobile.data.source.local.module.dynamic.widgets;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.craft.silicon.centemobile.view.ep.data.LayoutData;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface WidgetDao extends WidgetDataSource {

    @Override
    @Insert(onConflict = REPLACE)
    void saveFormControl(List<FormControl> data);

    @Override
    @Query("SELECT * FROM form_control_tb WHERE moduleID=:moduleID AND formSequence=:seq")
    Observable<List<FormControl>> getFormControl(String moduleID, String seq);

    @Override
    @Query("SELECT * FROM form_control_tb WHERE moduleID=:moduleID")
    Observable<List<FormControl>> getFormControlNoSq(String moduleID);

    @Override
    @Query("DELETE FROM form_control_tb")
    void deleteFormControl();

    @Override
    @Insert(onConflict = REPLACE)
    void saveModule(List<Modules> data);

    @Override
    @Query("SELECT * FROM modules_tbl WHERE parentModule=:moduleID")
    Observable<List<Modules>> getModules(String moduleID);

    @Override
    @Query("DELETE FROM modules_tbl")
    void deleteFormModule();

    @Override
    @Query("SELECT * FROM action_control_tb WHERE moduleID=:moduleID")
    Observable<List<ActionControls>> getActionControl(String moduleID);

    @Override
    @Query("SELECT * FROM action_control_tb WHERE controlID=:controlID")
    Observable<List<ActionControls>> getActionControlCID(String controlID);

    @Override
    @Query("SELECT * FROM action_control_tb WHERE moduleID=:moduleID AND formID=:formID")
    Observable<List<ActionControls>> getActionControlByFM(String moduleID, String formID);

    @Override
    @Insert(onConflict = REPLACE)
    void saveAction(List<ActionControls> data);

    @Override
    @Query("DELETE FROM action_control_tb")
    void deleteAction();

    @Override
    @Insert(onConflict = REPLACE)
    void saveStaticData(List<StaticDataDetails> data);

    @Override
    @Query("SELECT * FROM static_data_details_tbl")
    Observable<List<StaticDataDetails>> getStaticData();

    @Override
    @Query("SELECT * FROM layout_tbl")
    Single<LayoutData> layoutData();

    @Override
    @Insert(onConflict = REPLACE)
    void saveLayoutData(LayoutData data);
}
