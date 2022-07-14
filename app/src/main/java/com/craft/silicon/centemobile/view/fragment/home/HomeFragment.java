package com.craft.silicon.centemobile.view.fragment.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.data.model.control.FormControl;
import com.craft.silicon.centemobile.data.model.control.FormNavigation;
import com.craft.silicon.centemobile.data.model.converter.DynamicAPIResponseConverter;
import com.craft.silicon.centemobile.data.model.dynamic.DynamicAPIResponse;
import com.craft.silicon.centemobile.data.model.dynamic.FormField;
import com.craft.silicon.centemobile.data.model.module.ModuleCategory;
import com.craft.silicon.centemobile.data.model.module.ModuleIdEnum;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.craft.silicon.centemobile.data.model.user.AlertServices;
import com.craft.silicon.centemobile.data.model.user.FrequentModules;
import com.craft.silicon.centemobile.data.source.constants.StatusEnum;
import com.craft.silicon.centemobile.databinding.FragmentHomeBinding;
import com.craft.silicon.centemobile.util.AppLogger;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.MainActivity;
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment;
import com.craft.silicon.centemobile.view.dialog.DialogData;
import com.craft.silicon.centemobile.view.dialog.LoadingFragment;
import com.craft.silicon.centemobile.view.ep.controller.AlertList;
import com.craft.silicon.centemobile.view.ep.controller.AppController;
import com.craft.silicon.centemobile.view.ep.data.AccountData;
import com.craft.silicon.centemobile.view.ep.data.BodyData;
import com.craft.silicon.centemobile.view.ep.data.GroupForm;
import com.craft.silicon.centemobile.view.ep.data.GroupModule;
import com.craft.silicon.centemobile.view.fragment.dynamic.DynamicFragment;
import com.craft.silicon.centemobile.view.fragment.validation.ValidationFragment;
import com.craft.silicon.centemobile.view.model.AccountViewModel;
import com.craft.silicon.centemobile.view.model.AuthViewModel;
import com.craft.silicon.centemobile.view.model.WidgetViewModel;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment implements AppCallbacks {

    private FragmentHomeBinding binding;
    private AppController controller;
    private AuthViewModel authViewModel;
    private WidgetViewModel widgetViewModel;

    private AccountViewModel accountViewModel;

    private final CompositeDisposable subscribe = new CompositeDisposable();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(AppCallbacks appCallbacks) {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        setBinding();
        setViewModel();
        setController();
        setHomeData();
        setAccountData(widgetViewModel.accountsData().getValue());
        setToggle();
        setAdvert();
        tryBalance();
        return binding.getRoot().getRootView();
    }

    private void setAdvert() {
        int[] advert = new int[]{R.drawable.advert, R.drawable.advert, R.drawable.advert};
        binding.carouselView.setPageCount(advert.length);
        binding.carouselView.setImageListener((position, imageView) -> imageView.setImageResource(advert[position]));
    }

    private void setToggle() {
        binding.headerItem.toggleGroup.check(R.id.accountButton);
        binding.headerItem.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.accountButton) {
                setUtilsItem();
            } else if (checkedId == R.id.utilsButton) {
                setAccountItems();
            }

        });
    }

    private void setUtilsItem() {
        binding.headerItem.motionContainer.setTransition(R.id.accountState, R.id.utilsState);
        binding.headerItem.motionContainer.transitionToEnd();
    }

    private void setAccountItems() {
        binding.headerItem.motionContainer.setTransition(R.id.utilsState, R.id.accountState);
        binding.headerItem.motionContainer.transitionToEnd();
    }

    private void tryBalance() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("BANKACCOUNTID", "25102024410");
            jsonObject.put("MerchantID", "BALANCE");

            subscribe.add(accountViewModel.checkBalance(jsonObject,
                            requireContext(), "HOME")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> {
                        Log.e("TAG", BaseClass.decryptLatest(v.getResponse(),
                                authViewModel.storage.getDeviceData().getValue().getDevice(),
                                true,
                                authViewModel.storage.getDeviceData().getValue().getRun()
                        ));
                    }, Throwable::printStackTrace)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getModuleData(List<FrequentModules> data) {
        subscribe.add(widgetViewModel.getModules(ModuleIdEnum.MAIN.getType())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(f -> {
                    BodyData bodyData = new BodyData(f, data);
                    binding.bodyItem.setData(bodyData);
                }, Throwable::printStackTrace));

    }


    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        widgetViewModel = new ViewModelProvider(this).get(WidgetViewModel.class);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
    }

    private void setHomeData() {
        binding.setUserData(authViewModel.storage.getActivationData().getValue());
        subscribe.add(authViewModel.getFrequentModules()
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getModuleData, Throwable::printStackTrace));
    }


    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setCallback(this);
        binding.bodyItem.setCallback(this);
        binding.headerItem.setCallback(this);
    }


    @Override
    public void onModule(Modules modules) {

        if (modules.getModuleURLTwo() != null) {
            if (!TextUtils.isEmpty(modules.getModuleURLTwo())) {
                openUrl(modules.getModuleURLTwo());
            } else navigate(modules);
        } else navigate(modules);

    }

    @Override
    public void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void navigate(Modules modules) {
        if (Objects.equals(modules.getModuleCategory(), ModuleCategory.BLOCK.getType())) {
            subscribe.add(widgetViewModel.getModules(modules.getModuleID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(m -> {
                        DynamicFragment.setData(new GroupModule(modules, m));
                        onDestination();
                    }, Throwable::printStackTrace));
        } else getFormControl(modules);
    }


    @Override
    public void onFrequent(FrequentModules modules) {
        subscribe.add(widgetViewModel.getModule(modules.getParentModule())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getFormControl, Throwable::printStackTrace));
    }

    private void getFormControl(Modules modules) {
        subscribe.add(widgetViewModel.getFormControl(modules.getModuleID(), "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(f -> setFormNavigation(f, modules), Throwable::printStackTrace));
    }

    @Override
    public void onDestination() {
        ((MainActivity) requireActivity())
                .provideNavigationGraph()
                .navigate(widgetViewModel.navigation().navigateDynamic());
    }

    public void setAccountData(List<Accounts> accountData) {
        binding.headerItem.accountPager.setCallback(this);
        binding.headerItem.accountPager.setData(new AccountData(accountData));
    }

    @Override
    public void currentAccount(Accounts accounts) {
        List<AlertServices> servicesList = authViewModel
                .storage.getAlerts().getValue().stream()
                .filter(a -> a.getBankAccountID().equals(accounts.getId()))
                .collect(Collectors.toList());
        binding.headerItem.utilPager.setData(new AlertList(servicesList));
        binding.headerItem.utilPager.setCallback(this);
    }

    @Override
    public void onBalance(TextView textView, Accounts accounts) {
        LoadingFragment.show(requireActivity().getSupportFragmentManager());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("BANKACCOUNTID", accounts.getId());
            jsonObject.put("MerchantID", "BALANCE");

            subscribe.add(accountViewModel.checkBalance(jsonObject,
                            requireContext(), "HOME")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> {
                        LoadingFragment.dismiss(requireActivity().getSupportFragmentManager());
                        new AppLogger().appLog(
                                "BALANCE:Response", BaseClass.decryptLatest(
                                        v.getResponse(),
                                        accountViewModel.dataSource.getDeviceData()
                                                .getValue().getDevice(),
                                        true,
                                        accountViewModel.dataSource.getDeviceData()
                                                .getValue().getRun()
                                )
                        );
                        if (!BaseClass.nonCaps(v.getResponse())
                                .equals(StatusEnum.ERROR.getType())) {
                            DynamicAPIResponse resData = new DynamicAPIResponseConverter().to(
                                    BaseClass.decryptLatest(
                                            v.getResponse(),
                                            accountViewModel.dataSource.getDeviceData()
                                                    .getValue().getDevice(),
                                            true,
                                            accountViewModel.dataSource.getDeviceData()
                                                    .getValue().getRun()));
                            if (!BaseClass.nonCaps(v.getResponse())
                                    .equals(StatusEnum.SUCCESS.getType())) {
                                if (resData != null)
                                    if (resData.getRData() != null)
                                        for (FormField field : resData.getRData()) {
                                            if (field.getControlID().equals("BALTEXT")) {
                                                String balance = "UGX. " + field.getControlValue();
                                                textView.setText(balance);
                                            }
                                        }
                                new Handler(Looper.getMainLooper()).postDelayed(()
                                                -> textView.setText(BaseClass.maskCardNumber("####")),
                                        8500);
                            } else {
                                AlertDialogFragment.newInstance(
                                        new DialogData(
                                                R.string.error,
                                                resData.getMessage(),
                                                R.drawable.warning_app
                                        ),
                                        requireActivity().getSupportFragmentManager()
                                );
                            }
                        } else {

                            AlertDialogFragment.newInstance(
                                    new DialogData(
                                            R.string.error,
                                            getString(R.string.something_),
                                            R.drawable.warning_app
                                    ),
                                    requireActivity().getSupportFragmentManager()
                            );
                        }
                    }, Throwable::printStackTrace)
            );
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setFormNavigation(List<FormControl> forms, Modules modules) {
        String destination = forms.stream()
                .map(FormControl::getFormID)
                .collect(Collectors.toList())
                .stream().findFirst().get();
        if (BaseClass.nonCaps(destination)
                .equals(BaseClass.nonCaps(FormNavigation.VALIDATE.name())))
            validate(forms, modules);
        else if (BaseClass.nonCaps(destination)
                .equals(BaseClass.nonCaps(FormNavigation.VALIDATE.name())))
            payment(forms, modules);
    }

    private void payment(List<FormControl> forms, Modules modules) {

    }

    private void validate(List<FormControl> forms, Modules modules) {
        ValidationFragment.setData(new GroupForm(
                modules, forms));
        ((MainActivity) requireActivity())
                .provideNavigationGraph()
                .navigate(widgetViewModel.navigation().navigateValidation());
    }


}