package com.craft.silicon.centemobile.view.model;

import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepository;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;

@HiltViewModel
public class WidgetViewModel extends ViewModel implements WidgetDataSource {

    private final WidgetRepository widgetRepository;
    private final StorageDataSource storageDataSource;

    @Inject
    public WidgetViewModel(WidgetRepository widgetRepository, StorageDataSource storageDataSource) {
        this.widgetRepository = widgetRepository;
        this.storageDataSource = storageDataSource;
    }

    @Override
    public Observable<List<Modules>> getModules(String moduleID) {
        return widgetRepository.getModules(moduleID);
    }


    @Override
    public Observable<List<FormControl>> getFormControl(String moduleID) {
        return widgetRepository.getFormControl(moduleID);
    }
}
