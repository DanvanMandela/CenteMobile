package com.elmacentemobile.data.source.local.module.dynamic.widgets;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.elmacentemobile.data.model.AtmData;
import com.elmacentemobile.data.model.CarouselData;
import com.elmacentemobile.data.model.action.ActionControls;
import com.elmacentemobile.data.model.control.FormControl;
import com.elmacentemobile.data.model.module.Modules;
import com.elmacentemobile.data.model.static_data.StaticDataDetails;
import com.elmacentemobile.data.model.user.PendingTransaction;
import com.elmacentemobile.data.receiver.NotificationData;
import com.elmacentemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.elmacentemobile.view.ep.data.LayoutData;

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
    @Query("SELECT * FROM modules_tbl WHERE moduleID=:moduleID")
    Observable<Modules> getFrequentModule(String moduleID);

    @Override
    @Query("SELECT * FROM modules_tbl WHERE parentModule=:moduleID")
    Observable<Modules> getModule(String moduleID);

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

    @Override
    @Insert(onConflict = REPLACE)
    void saveAtms(List<AtmData> atmData);

    @Override
    @Query("SELECT * FROM atm_branch_tbl")
    Observable<List<AtmData>> getAtms();

    @Override
    @Query("DELETE FROM atm_branch_tbl")
    void deleteAtms();

    @Override
    @Insert(onConflict = REPLACE)
    void saveCarousel(List<CarouselData> data);

    @Override
    @Query("DELETE FROM carousel_tbl")
    void deleteCarousel();

    @Override
    @Query("SELECT * FROM carousel_tbl")
    Observable<List<CarouselData>> getCarousel();

    @Override
    @Query("SELECT * FROM atm_branch_tbl WHERE type=:b")
    Observable<List<AtmData>> getATMBranch(boolean b);


    @Insert(onConflict = REPLACE)
    @Override
    void saveNotifications(NotificationData data);

    @Query("SELECT * FROM notifications ORDER BY time DESC")
    @Override
    Observable<List<NotificationData>> getNotification();

    @Query("DELETE FROM notifications")
    @Override
    void deleteNotifications();

    @Query("DELETE FROM notifications WHERE id=:id")
    @Override
    void deleteNotification(int id);


    @Insert(onConflict = REPLACE)
    @Override
    void savePendingTransaction(PendingTransaction pendingTransaction);

    @Query("DELETE FROM pending_table")
    @Override
    void deletePendingTransactions();

    @Query("SELECT * FROM pending_table")
    @Override
    Observable<List<PendingTransaction>> getPendingTransaction();

    @Query("DELETE FROM pending_table WHERE id=:id")
    @Override
    void deletePendingTransactionsByID(int id);
}
