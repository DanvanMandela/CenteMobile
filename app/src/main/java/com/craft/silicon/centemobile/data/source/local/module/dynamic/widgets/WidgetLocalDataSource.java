package com.craft.silicon.centemobile.data.source.local.module.dynamic.widgets;

import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.craft.silicon.centemobile.util.scheduler.BaseSchedulerProvider;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;

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
    public Observable<List<FormControl>> getFormControl(String moduleID) {
        return widgetDao.getFormControl(moduleID);
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
    public Observable<List<ActionControls>> getActionControl(String moduleID) {
        return widgetDao.getActionControl(moduleID);
    }

    @Override
    public void saveAction(List<ActionControls> data) {
        Completable.fromRunnable(() -> widgetDao.saveAction(data))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }
}
