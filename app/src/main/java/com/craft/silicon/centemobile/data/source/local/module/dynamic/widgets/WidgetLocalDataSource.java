package com.craft.silicon.centemobile.data.source.local.module.dynamic.widgets;

import com.craft.silicon.centemobile.data.model.AtmData;
import com.craft.silicon.centemobile.data.model.CarouselData;
import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails;
import com.craft.silicon.centemobile.data.model.user.PendingTransaction;
import com.craft.silicon.centemobile.data.receiver.NotificationData;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.craft.silicon.centemobile.util.AppLogger;
import com.craft.silicon.centemobile.util.scheduler.BaseSchedulerProvider;
import com.craft.silicon.centemobile.view.ep.data.LayoutData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Singleton
public class WidgetLocalDataSource implements WidgetDataSource {

    private final WidgetDao widgetDao;
    private final BaseSchedulerProvider schedulerProvider;


    @Inject
    public WidgetLocalDataSource(WidgetDao widgetDao, BaseSchedulerProvider schedulerProvider) {
        this.widgetDao = widgetDao;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void saveFormControl(List<FormControl> data) {
        Completable.fromRunnable(() -> widgetDao.saveFormControl(data))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Observable<List<FormControl>> getFormControl(String moduleID, String seq) {
        return widgetDao.getFormControl(moduleID, seq);
    }

    @Override
    public void deleteFormControl() {
        Completable.fromRunnable(widgetDao::deleteFormControl)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void saveModule(List<Modules> data) {
        Completable.fromRunnable(() -> widgetDao.saveModule(data))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Observable<List<Modules>> getModules(String moduleID) {
        return widgetDao.getModules(moduleID);
    }

    @Override
    public void deleteFormModule() {
        Completable.fromRunnable(widgetDao::deleteFormModule)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Observable<List<ActionControls>> getActionControl(String moduleID) {
        return widgetDao.getActionControl(moduleID);
    }

    @Override
    public Observable<List<ActionControls>> getActionControlByFM(String moduleID, String formID) {
        return widgetDao.getActionControlByFM(moduleID, formID);
    }

    @Override
    public Observable<List<FormControl>> getFormControlNoSq(String moduleID) {
        return widgetDao.getFormControlNoSq(moduleID);
    }

    @Override
    public Observable<List<ActionControls>> getActionControlCID(String controlID) {
        return widgetDao.getActionControlCID(controlID);
    }

    @Override
    public void saveAction(List<ActionControls> data) {
        Completable.fromRunnable(() -> widgetDao.saveAction(data))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void deleteAction() {
        Completable.fromRunnable(widgetDao::deleteAction)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void saveStaticData(List<StaticDataDetails> data) {
        Completable.fromRunnable(() -> widgetDao.saveStaticData(data))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Observable<List<StaticDataDetails>> getStaticData() {
        return widgetDao.getStaticData();
    }

    @Override
    public Single<LayoutData> layoutData() {
        return widgetDao.layoutData();
    }

    @Override
    public void saveLayoutData(LayoutData data) {
        Completable.fromRunnable(() -> widgetDao.saveLayoutData(data))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Observable<Modules> getModule(String moduleID) {
        return widgetDao.getModule(moduleID);
    }

    @Override
    public void saveAtms(List<AtmData> atmData) {
        Completable.fromRunnable(() -> widgetDao.saveAtms(atmData))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void deleteAtms() {
        Completable.fromRunnable(widgetDao::deleteAtms)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Observable<List<AtmData>> getAtms() {
        return widgetDao.getAtms();
    }

    @Override
    public void saveCarousel(List<CarouselData> data) {
        Completable.fromRunnable(() -> widgetDao.saveCarousel(data))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void deleteCarousel() {
        Completable.fromRunnable(widgetDao::deleteCarousel)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Observable<List<CarouselData>> getCarousel() {
        return widgetDao.getCarousel();
    }

    @Override
    public Observable<List<AtmData>> getATMBranch(boolean b) {
        return widgetDao.getATMBranch(b);
    }


    @Override
    public void saveNotifications(NotificationData data) {
        Completable.fromRunnable(() -> widgetDao.saveNotifications(data))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void deleteNotifications() {
        Completable.fromRunnable(widgetDao::deleteNotifications)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Observable<List<NotificationData>> getNotification() {
        return widgetDao.getNotification();
    }

    @Override
    public void deleteNotification(int id) {
        Completable.fromRunnable(() -> widgetDao.deleteNotification(id))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void savePendingTransaction(PendingTransaction pendingTransaction) {
        Completable.fromRunnable(() -> widgetDao.savePendingTransaction(pendingTransaction))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public void deletePendingTransactions() {
        Completable.fromRunnable(widgetDao::deletePendingTransactions)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    @Override
    public Observable<List<PendingTransaction>> getPendingTransaction() {
        return widgetDao.getPendingTransaction();
    }

    @Override
    public void deletePendingTransactionsByID(int id) {
        Completable.fromRunnable(() -> widgetDao.deletePendingTransactionsByID(id))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }
}
