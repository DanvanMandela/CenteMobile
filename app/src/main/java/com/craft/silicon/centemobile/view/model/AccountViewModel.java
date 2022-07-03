package com.craft.silicon.centemobile.view.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum;
import com.craft.silicon.centemobile.data.repository.account.AccountDataSource;
import com.craft.silicon.centemobile.data.repository.account.AccountRepository;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.util.BaseClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Single;

@HiltViewModel
public class AccountViewModel extends ViewModel implements AccountDataSource {
    private final AccountRepository accountRepository;
    public final StorageDataSource dataSource;

    @Inject
    public AccountViewModel(AccountRepository accountRepository, StorageDataSource dataSource) {
        this.accountRepository = accountRepository;
        this.dataSource = dataSource;
    }

    @Override
    public Single<DynamicResponse> checkBalance(JSONObject data, Context context, String moduleID) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String customerID = dataSource.getActivationData().getValue().getId();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.PAY_BILL.getType(),
                    customerID,
                    true);

            jsonObject.put("ModuleID", moduleID);
            jsonObject.put("PayBill", data);
            String newRequest = jsonObject.toString();

            String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ?
                    Constants.BaseUrl.URL : Objects.requireNonNull(dataSource.getDeviceData()
                    .getValue().getAccount())).getPath();

            Log.e("BALANCE", newRequest);

            return accountRepository.accountRequestT(
                    dataSource.getDeviceData().getValue().getToken(),
                    new PayloadData(
                            uniqueID,
                            BaseClass.encryptString(newRequest, device, iv)
                    ), path);

        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }


}
