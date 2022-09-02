package com.craft.silicon.centemobile.view.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.AtmData;
import com.craft.silicon.centemobile.data.model.CarouselData;
import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails;
import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.craft.silicon.centemobile.data.model.user.Beneficiary;
import com.craft.silicon.centemobile.data.model.user.ModuleHide;
import com.craft.silicon.centemobile.data.model.user.PendingTransaction;
import com.craft.silicon.centemobile.data.receiver.NotificationData;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetDataSource;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepository;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.data.source.remote.helper.ConnectionObserver;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.view.ep.data.LayoutData;
import com.craft.silicon.centemobile.view.navigation.NavigationDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

@HiltViewModel
public class WidgetViewModel extends ViewModel implements WidgetDataSource {

    private final WidgetRepository widgetRepository;
    public final StorageDataSource storageDataSource;
    private final NavigationDataSource navigationDataSource;
    public final ConnectionObserver connectionObserver;


    private final BehaviorSubject<Boolean> loadingUi = BehaviorSubject.createDefault(false);
    public Observable<Boolean> loading = loadingUi.hide();


    @Inject
    public WidgetViewModel(WidgetRepository widgetRepository,
                           StorageDataSource storageDataSource,
                           NavigationDataSource navigationDataSource,
                           ConnectionObserver connectionObserver) {
        this.widgetRepository = widgetRepository;
        this.storageDataSource = storageDataSource;
        this.navigationDataSource = navigationDataSource;
        this.connectionObserver = connectionObserver;
    }

    @Override
    public NavigationDataSource navigation() {
        return navigationDataSource;
    }

    @Override
    public LiveData<List<StaticDataDetails>> storageStaticData() {
        return new MutableLiveData<>(storageDataSource.getStaticData().getValue());
    }

    @Override
    public LiveData<List<Accounts>> accountsData() {
        return new MutableLiveData<>(storageDataSource.getAccounts().getValue());
    }

    @Override
    public Observable<List<Modules>> getModules(String moduleID) {
        return widgetRepository.getModules(moduleID).map(modules -> {
            List<ModuleHide> moduleHides = storageDataSource.getHiddenModule().getValue();
            List<Modules> modulesList = new ArrayList<>();
            modules.forEach(module -> {
                if (moduleHides != null)
                    if (!moduleHides.isEmpty()) {
                        boolean hide = moduleHides.stream()
                                .map(ModuleHide::getId)
                                .filter(Objects::nonNull)
                                .anyMatch(type -> (Objects.equals(module.getModuleID(), type)));
                        if (!hide) modulesList.add(module);
                    } else {
                        modulesList.add(module);
                    }
                else modulesList.add(module);
            });
            return modulesList;
        });
    }

    @Override
    public Observable<Modules> getModule(String moduleID) {
        return widgetRepository.getModule(moduleID);
    }

    @Override
    public Observable<List<ActionControls>> getActionControl(String moduleID) {
        return widgetRepository.getActionControl(moduleID);
    }

    @Override
    public Observable<List<ActionControls>> getActionControlCID(String controlID) {
        return widgetRepository.getActionControlCID(controlID);
    }

    @Override
    public Observable<List<FormControl>> getFormControl(String moduleID, String seq) {
        return widgetRepository.getFormControl(moduleID, seq);
    }

    @Override
    public Observable<List<StaticDataDetails>> getStaticData() {
        return widgetRepository.getStaticData();
    }

    @Override
    public LiveData<List<Beneficiary>> beneficiaryData() {
        return new MutableLiveData<>(storageDataSource.getBeneficiary().getValue());
    }

    @Override
    public Observable<List<ActionControls>> getActionControlByFM(String moduleID, String formID) {
        return widgetRepository.getActionControlByFM(moduleID, formID);
    }

    @Override
    public Single<DynamicResponse> recentList(String moduleID, String merchantID, Context context) {
        try {
            String iv = storageDataSource.getDeviceData().getValue().getRun();
            String device = storageDataSource.getDeviceData().getValue().getDevice();
            String customerID = storageDataSource.getActivationData().getValue().getId();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.DB_CALL.getType(),
                    customerID,
                    true,
                    storageDataSource);
            jsonObject.put("MerchantID", merchantID);
            jsonObject.put("ModuleID", moduleID);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("HEADER", "GETRECENTLIST");
            jsonObject.put("DynamicForm", jsonObject1);

            String newRequest = jsonObject.toString();
            Log.e("Recent", newRequest);
            String path = new SpiltURL(storageDataSource.getDeviceData().getValue()
                    == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(storageDataSource
                    .getDeviceData().getValue().getOther())).getPath();

            return widgetRepository.requestWidget(
                            new PayloadData(
                                    storageDataSource.getUniqueID().getValue(),
                                    BaseClass.encryptString(newRequest, device, iv)
                            ), path).doOnSubscribe(disposable -> loadingUi.onNext(true))
                    .doOnSuccess(disposable -> loadingUi.onNext(false))
                    .doOnError(disposable -> loadingUi.onNext(false));

        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveLayoutData(LayoutData data) {
        widgetRepository.saveLayoutData(data);
    }

    @Override
    public Single<LayoutData> layoutData() {
        return widgetRepository.layoutData();
    }

    @Override
    public Observable<List<FormControl>> getFormControlNoSq(String moduleID) {
        return widgetRepository.getFormControlNoSq(moduleID);
    }

    @Override
    public LiveData<String> versionData() {
        return new MutableLiveData<>(storageDataSource.getVersion().getValue());
    }

    @Override
    public Observable<List<CarouselData>> getCarousel() {
        return widgetRepository.getCarousel();
    }

    @Override
    public Observable<List<AtmData>> getAtms() {
        return widgetRepository.getAtms();
    }

    @Override
    public Observable<List<AtmData>> getATMBranch(boolean b) {
        return widgetRepository.getATMBranch(b);
    }

    @Override
    public Observable<List<NotificationData>> getNotification() {


        return widgetRepository.getNotification();
    }

    @Override
    public void deleteNotifications() {
        widgetRepository.deleteNotifications();
    }

    @Override
    public void deleteNotification(int id) {
        widgetRepository.deleteNotification(id);
    }

    @Override
    public void saveAtms(List<AtmData> atmData) {
        widgetRepository.saveAtms(atmData);
    }

    @Override
    public void savePendingTransaction(PendingTransaction pendingTransaction) {
        widgetRepository.savePendingTransaction(pendingTransaction);
    }

    @Override
    public void deletePendingTransactions() {
        widgetRepository.deletePendingTransactions();
    }

    @Override
    public Observable<List<PendingTransaction>> getPendingTransaction() {
        return widgetRepository.getPendingTransaction()
                .doOnSubscribe(t -> loadingUi.onNext(true))
                .doOnError(t -> loadingUi.onNext(false))
                .doOnComplete(() -> loadingUi.onNext(false));
    }

    @Override
    public void deletePendingTransactionsByID(int id) {
        widgetRepository.deletePendingTransactionsByID(id);
    }


}
