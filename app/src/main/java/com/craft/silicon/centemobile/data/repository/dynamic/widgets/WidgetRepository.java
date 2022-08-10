package com.craft.silicon.centemobile.data.repository.dynamic.widgets;

import com.craft.silicon.centemobile.data.model.AtmData;
import com.craft.silicon.centemobile.data.model.CarouselData;
import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails;
import com.craft.silicon.centemobile.data.scope.Local;
import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.view.ep.data.LayoutData;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;

@Singleton
public class WidgetRepository implements WidgetDataSource {

    private final WidgetDataSource remoteData;
    private final WidgetDataSource localData;


    @Inject
    public WidgetRepository(@Remote WidgetDataSource remoteData,
                            @Local WidgetDataSource localData) {
        this.remoteData = remoteData;
        this.localData = localData;
    }


    @Override
    public void saveFormControl(List<FormControl> data) {
        localData.saveFormControl(data);
    }

    @Override
    public Observable<List<FormControl>> getFormControl(String moduleID, String seq) {
        return localData.getFormControl(moduleID, seq);
    }

    @Override
    public void deleteFormControl() {
        localData.deleteFormControl();
    }

    @Override
    public void saveModule(List<Modules> data) {
        localData.saveModule(data);
    }

    @Override
    public Observable<List<Modules>> getModules(String moduleID) {
        return localData.getModules(moduleID).map(modules ->
                modules.stream().filter(v -> !v.isDisabled()).collect(Collectors.toList()));
    }

    @Override
    public Observable<Modules> getModule(String moduleID) {
        return localData.getModule(moduleID);
    }

    @Override
    public void deleteFormModule() {
        localData.deleteFormModule();
    }

    @Override
    public void requestWidget(List<ActionControls> data) {
        localData.requestWidget(data);
    }

    @Override
    public Observable<List<ActionControls>> getActionControl(String moduleID) {
        return localData.getActionControl(moduleID);
    }

    @Override
    public Observable<List<ActionControls>> getActionControlCID(String controlID) {
        return localData.getActionControlCID(controlID);
    }

    @Override
    public Observable<List<ActionControls>> getActionControlByFM(String moduleID, String formID) {
        return localData.getActionControlByFM(moduleID, formID);
    }

    @Override
    public void saveAction(List<ActionControls> data) {
        localData.saveAction(data);
    }


    @Override
    public void deleteAction() {
        localData.deleteAction();
    }

    @Override
    public Single<DynamicResponse> requestWidget(PayloadData data, String path) {
        return remoteData.requestWidget(data, path);
    }

    @Override
    public void saveStaticData(List<StaticDataDetails> data) {
        localData.saveStaticData(data);
    }

    @Override
    public Observable<List<StaticDataDetails>> getStaticData() {
        return localData.getStaticData();
    }


    @Override
    public Single<LayoutData> layoutData() {
        return localData.layoutData();
    }

    @Override
    public void saveLayoutData(LayoutData data) {
        localData.saveLayoutData(data);
    }

    @Override
    public Observable<List<FormControl>> getFormControlNoSq(String moduleID) {
        return localData.getFormControlNoSq(moduleID);
    }


    @Override
    public void saveAtms(List<AtmData> atmData) {
        localData.saveAtms(atmData);
    }

    @Override
    public Observable<List<AtmData>> getAtms() {
        return localData.getAtms();
    }

    @Override
    public void deleteAtms() {
        localData.deleteAtms();
    }

    @Override
    public void saveCarousel(List<CarouselData> data) {
        localData.saveCarousel(data);
    }

    @Override
    public void deleteCarousel() {
        localData.deleteCarousel();
    }

    @Override
    public Observable<List<CarouselData>> getCarousel() {
        return localData.getCarousel();
    }

    @Override
    public Observable<List<AtmData>> getATMBranch(boolean b) {
        return localData.getATMBranch(b);
    }
}
