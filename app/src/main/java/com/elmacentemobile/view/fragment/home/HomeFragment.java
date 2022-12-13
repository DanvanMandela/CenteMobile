package com.elmacentemobile.view.fragment.home;

import static com.elmacentemobile.util.BaseClass.nonCaps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager2.widget.ViewPager2;

import com.elmacentemobile.R;
import com.elmacentemobile.data.model.CarouselData;
import com.elmacentemobile.data.model.control.FormControl;
import com.elmacentemobile.data.model.converter.DynamicAPIResponseConverter;
import com.elmacentemobile.data.model.dynamic.DynamicAPIResponse;
import com.elmacentemobile.data.model.dynamic.FormField;
import com.elmacentemobile.data.model.module.ModuleCategory;
import com.elmacentemobile.data.model.module.ModuleIdEnum;
import com.elmacentemobile.data.model.module.Modules;
import com.elmacentemobile.data.model.user.Accounts;
import com.elmacentemobile.data.model.user.AlertServices;
import com.elmacentemobile.data.model.user.FrequentModules;
import com.elmacentemobile.data.model.user.ModuleDisable;
import com.elmacentemobile.data.model.user.ModuleHide;
import com.elmacentemobile.data.source.constants.StatusEnum;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.databinding.FragmentHomeBinding;
import com.elmacentemobile.util.AppLogger;
import com.elmacentemobile.util.BaseClass;
import com.elmacentemobile.util.OnAlertDialog;
import com.elmacentemobile.util.ShowAlertDialog;
import com.elmacentemobile.util.ShowToast;
import com.elmacentemobile.util.callbacks.AppCallbacks;
import com.elmacentemobile.view.activity.MainActivity;
import com.elmacentemobile.view.activity.beneficiary.BeneficiaryManageActivity;
import com.elmacentemobile.view.activity.level.FalconHeavyActivity;
import com.elmacentemobile.view.activity.transaction.PendingTransactionActivity;
import com.elmacentemobile.view.activity.transaction.TransactionCenterActivity;
import com.elmacentemobile.view.binding.BindingAdapterKt;
import com.elmacentemobile.view.dialog.AlertDialogFragment;
import com.elmacentemobile.view.dialog.DayTipData;
import com.elmacentemobile.view.dialog.DialogData;
import com.elmacentemobile.view.dialog.InfoFragment;
import com.elmacentemobile.view.dialog.LoadingFragment;
import com.elmacentemobile.view.dialog.TipConverter;
import com.elmacentemobile.view.dialog.TipItem;
import com.elmacentemobile.view.dialog.TipItemConverter;
import com.elmacentemobile.view.dialog.TipTypeEnum;
import com.elmacentemobile.view.ep.controller.AlertList;
import com.elmacentemobile.view.ep.data.AccountData;
import com.elmacentemobile.view.ep.data.BodyData;
import com.elmacentemobile.view.ep.data.BusData;
import com.elmacentemobile.view.ep.data.GroupForm;
import com.elmacentemobile.view.ep.data.GroupModule;
import com.elmacentemobile.view.ep.data.MiniList;
import com.elmacentemobile.view.ep.data.MiniStatement;
import com.elmacentemobile.view.ep.data.MiniTypeConverter;
import com.elmacentemobile.view.fragment.MiniStatementFragment;
import com.elmacentemobile.view.fragment.landing.LogoutFeedback;
import com.elmacentemobile.view.model.AccountViewModel;
import com.elmacentemobile.view.model.AuthViewModel;
import com.elmacentemobile.view.model.BaseViewModel;
import com.elmacentemobile.view.model.WidgetViewModel;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
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
public class HomeFragment extends Fragment implements AppCallbacks, OnAlertDialog {

    private FragmentHomeBinding binding;
    private AuthViewModel authViewModel;
    private WidgetViewModel widgetViewModel;
    private AccountViewModel accountViewModel;
    private MotionLayout motionLayout;
    private BaseViewModel baseViewModel;


    private final CompositeDisposable subscribe = new CompositeDisposable();
    private final MutableLiveData<List<CarouselItem>> adverts = new MutableLiveData<>();

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
    public static HomeFragment newInstance() {
        return new HomeFragment();
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
        setHomeData();
        return binding.getRoot().getRootView();
    }


    @SuppressLint("NewApi")
    private void getAdverts() {
        ArrayList<CarouselItem> carouselItems = new ArrayList<>();
//        subscribe.add(widgetViewModel.getCarousel()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(v -> {
//                    if (!v.isEmpty()) {
//                        ArrayList<CarouselItem> carouselItems = new ArrayList<>();
//                        v.forEach(data -> carouselItems.add(new CarouselItem(
//                                data.getImageURL(),
//                                data.getImageInfoURL()
//
//                        )));
//                        adverts.setValue(carouselItems);
//
//                    }
//                }, Throwable::printStackTrace));


        BindingAdapterKt.tipData(widgetViewModel.storageDataSource.getDayTipData())
                .observe(getViewLifecycleOwner(),
                        tipData -> {
                            if (!tipData.isEmpty()) {
                                for (DayTipData t : tipData) {
                                    carouselItems.add(new CarouselItem(
                                            t.getBannerImage(),
                                            new TipItemConverter().from(
                                                    new TipItem(
                                                            new Gson().toJson(t),
                                                            TipTypeEnum.Dialog
                                                    )
                                            )
                                    ));
                                }
                            }
                        });


        BindingAdapterKt.carouselData(widgetViewModel.storageDataSource.getCarouselData())
                .observe(getViewLifecycleOwner(),
                        carouselData -> {
                            if (!carouselData.isEmpty()) {
                                for (CarouselData c : carouselData) {
                                    carouselItems.add(new CarouselItem(
                                            c.getImageURL(),
                                            new TipItemConverter().from(
                                                    new TipItem(
                                                            c.getImageInfoURL(),
                                                            TipTypeEnum.Url
                                                    )
                                            )
                                    ));
                                }
                            }
                        });

        widgetViewModel.carouselList.setValue(carouselItems);

    }

    @Override
    public void onAlertService(AlertServices services) {
        subscribe.add(widgetViewModel.getModule(services.getModuleID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getFormControl, Throwable::printStackTrace));
    }

    @Override
    public void logOut() {
        new ShowAlertDialog().showDialog(
                requireContext(),
                getString(R.string.log_out),
                getString(R.string.are_you_sure),
                this
        );
    }

    @Override
    public void navigateToNotification() {
        BindingAdapterKt.navigate(this,
                widgetViewModel.navigation().navigateToNotification());
    }

    @Override
    public void onPositive() {
        int feedback = authViewModel.storage.getFeedbackTimer().getValue();
        int feedbackMax = authViewModel.storage.getFeedbackTimerMax().getValue() == null ?
                5 : authViewModel.storage.getFeedbackTimerMax().getValue() == 0 ? 5 :
                authViewModel.storage.getFeedbackTimerMax().getValue();

        new AppLogger().appLog("RATE", String.valueOf(feedbackMax));

        if (feedback >= feedbackMax) {
            LogoutFeedback.setData(this);
            BindingAdapterKt.navigate(this,
                    widgetViewModel.navigation().navigateToLogoutFeedBack());
        } else BindingAdapterKt.navigate(this,
                widgetViewModel.navigation().navigateLanding());

    }

    @Override
    public void onNegative() {

    }

    private void setAdvert() {
        binding.carousel.registerLifecycle(getViewLifecycleOwner());
        widgetViewModel.carouselList.observe(getViewLifecycleOwner(), v -> binding.carousel.setData(v));

        binding.carousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater,
                                                  @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding,
                                         @NonNull CarouselItem carouselItem, int i) {

            }

            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {

                TipItem action = new TipItemConverter().to(carouselItem.getCaption());
                assert action != null;
                if (action.getType() == TipTypeEnum.Url) {
                    if (carouselItem.getCaption() != null) openUrl((String) action.getTip());
                } else BindingAdapterKt.navigate(HomeFragment.this, widgetViewModel.navigation()
                        .navigateToTips(new TipConverter().to((String) action.getTip())));
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });
    }

    private void setToggle() {
        motionLayout = binding.headerItem.motionContainer;
        binding.headerItem.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.accountButton) {
                    setAccountItems();
                } else if (checkedId == R.id.utilsButton) {
                    setUtilsItem();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.headerItem.toggleGroup.check(R.id.accountButton);
    }

    private void setUtilsItem() {
        motionLayout.setTransition(R.id.accountState, R.id.utilsState);
        motionLayout.transitionToEnd();
    }

    private void setAccountItems() {
        motionLayout.setTransition(R.id.utilsState, R.id.accountState);
        motionLayout.transitionToEnd();
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
        baseViewModel = new ViewModelProvider(this).get(BaseViewModel.class);
    }

    private void setHomeData() {
        binding.setUserData(authViewModel.storage.getActivationData().getValue());
        subscribe.add(authViewModel.getFrequentModules()
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getModuleData, Throwable::printStackTrace));


    }

    @Override
    public void setOnIndicator(ViewPager2 viewPager2) {
        TabLayoutMediator tabLayoutMediator =
                new TabLayoutMediator(binding.headerItem.indicatorTab,
                        viewPager2,
                        true, (tab, position) -> {
                });
        tabLayoutMediator.attach();
    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setCallback(this);
        binding.bodyItem.setCallback(this);
        binding.headerItem.setCallback(this);
        int animationDuration = requireContext()
                .getResources().getInteger(R.integer.animation_duration);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            stopShimmer();
            setAccountData(widgetViewModel.accountsData().getValue());
            setToggle();
            getAdverts();
            setAdvert();
        }, animationDuration);

    }

    private void stopShimmer() {
        binding.mainLay.setVisibility(View.VISIBLE);
        binding.shimmerContainer.stopShimmer();
        binding.shimmerContainer.setVisibility(View.GONE);
    }

    @SuppressLint("NewApi")
    @Override
    public void onModule(Modules modules) {
        List<ModuleDisable> moduleDisable = baseViewModel.dataSource.getDisableModule().getValue();
        if (!moduleDisable.isEmpty()) {
            ModuleDisable isDisabled = moduleDisable.stream().parallel()
                    .filter(type -> Objects.equals(type.getId(), modules.getModuleID()))
                    .findAny().orElse(null);
            if (isDisabled != null) new ShowToast(requireContext(), isDisabled.getMessage());
            else if (modules.getModuleURLTwo() != null) {
                if (!TextUtils.isEmpty(modules.getModuleURLTwo())) {
                    openUrl(modules.getModuleURLTwo());
                } else navigateTo(modules);
            } else navigateTo(modules);
        } else navigateTo(modules);

    }

    @Override
    public void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
         new    ShowToast(requireContext(), getString(R.string.nothing_show));
            e.printStackTrace();
        }

    }

    private void navigateTo(Modules modules) {
        if (nonCaps(modules.getModuleID()).equals(nonCaps("TRANSACTIONSCENTER")))
            navigateToTransactionCenter(modules);
        else if (nonCaps(modules.getModuleID()).equals(nonCaps("VIEWBENEFICIARY")))
            navigateToBeneficiary(modules);
        else if (nonCaps(modules.getModuleID()).equals(nonCaps("PENDINGTRANSACTIONS")))
            navigateToPendingTransaction(modules);
        else if (Objects.equals(modules.getModuleCategory(), ModuleCategory.BLOCK.getType())) {
            subscribe.add(widgetViewModel.getModules(modules.getModuleID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(m -> setDynamicModules(m, modules), Throwable::printStackTrace));
        } else getFormControl(modules);
    }

    private void navigateToBeneficiary(Modules modules) {
        EventBus.getDefault().postSticky(modules);
        Intent i = new Intent(getActivity(), BeneficiaryManageActivity.class);
        startActivity(i);
    }

    private void navigateToPendingTransaction(Modules modules) {
        EventBus.getDefault().postSticky(modules);
        Intent i = new Intent(getActivity(), PendingTransactionActivity.class);
        startActivity(i);
    }

    private void navigateToTransactionCenter(Modules modules) {
        EventBus.getDefault().postSticky(modules);
        Intent i = new Intent(getActivity(), TransactionCenterActivity.class);
        startActivity(i);
    }


    private void setDynamicModules(List<Modules> m, Modules modules) {
        EventBus.getDefault().postSticky(new BusData(new GroupModule(modules, m),
                null,
                null));
        Intent i = new Intent(getActivity(), FalconHeavyActivity.class);
        startActivity(i);
    }


    @Override
    public void onFrequent(FrequentModules modules) {
        subscribe.add(widgetViewModel.getFrequentModule(modules.getModuleId())
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


    public void setAccountData(List<Accounts> accountData) {
        binding.headerItem.headerAux.accountPager.setCallback(this);
        binding.headerItem.headerAux.accountPager.setData(new AccountData(accountData));
    }

    @SuppressLint("NewApi")
    @Override
    public void currentAccount(Accounts accounts) {
        binding.headerItem.headerAux.utilPager.setCallback(this);
        List<AlertServices> servicesList = authViewModel
                .storage.getAlerts().getValue();
        if (servicesList != null) {
//            servicesList = authViewModel
//                    .storage.getAlerts().getValue().stream()
//                    .filter(a -> a.getBankAccountID().equals(accounts.getId()))
//                    .collect(Collectors.toList());
            if (!servicesList.isEmpty()) {
                servicesList.sort(Comparator.comparing(AlertServices::getDueDate));
            }
            binding.headerItem.headerAux.utilPager
                    .setData(servicesList.isEmpty() ? null : new AlertList(servicesList));
        } else {
            binding.headerItem.headerAux.utilPager
                    .setData(null);
        }


    }

    @Override
    public void onBalance(TextView textView,
                          Accounts accounts,
                          TextView info,
                          Boolean b) {
        if (!b) setInfo(info, false);
        else {
            setLoading(true);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("BANKACCOUNTID", accounts.getId());
                jsonObject.put("MerchantID", "BALANCE");

                subscribe.add(accountViewModel.checkBalance(jsonObject,
                                requireContext(), "HOME")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> setOnSuccessBalance(data, textView, info),
                                Throwable::printStackTrace)
                );
            } catch (
                    JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void setInfo(TextView info, Boolean b) {
        if (b) {
            info.setText(getString(R.string.hide_balance));
            info.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_baseline_visibility_off_24
                    ), null
            );
        } else {
            info.setText(getString(R.string.view_balance));
            info.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_baseline_remove_red_eye_24
                    ), null
            );
        }
    }


    private void setOnSuccessBalance(DynamicResponse data,
                                     TextView textView,
                                     TextView info) {
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("UGX"));

            new AppLogger().appLog(
                    "BALANCE:Response", BaseClass.decryptLatest(
                            data.getResponse(),
                            accountViewModel.dataSource.getDeviceData()
                                    .getValue().getDevice(),
                            true,
                            accountViewModel.dataSource.getDeviceData()
                                    .getValue().getRun()
                    )
            );

            if (data.getResponse() == null) {
                setLoading(false);
                new ShowToast(requireContext(), getString(R.string.fatal_error));
            } else {
                if (data.getResponse().equals("ok")) {
                    setLoading(false);
                    new ShowToast(requireContext(), getString(R.string.something_));
                } else {
                    DynamicAPIResponse resData = new DynamicAPIResponseConverter().to(
                            BaseClass.decryptLatest(
                                    data.getResponse(),
                                    accountViewModel.dataSource.getDeviceData()
                                            .getValue().getDevice(),
                                    true,
                                    accountViewModel.dataSource.getDeviceData()
                                            .getValue().getRun()));

                    if (resData != null) {
                        if (nonCaps(resData.getStatus()).equals(nonCaps(StatusEnum.ERROR.getType()))) {
                            setLoading(false);
                            new ShowToast(requireContext(), resData.getMessage());
                        } else if (nonCaps(resData.getStatus())
                                .equals(nonCaps(StatusEnum.SUCCESS.getType()))) {
                            setLoading(false);
                            if (resData.getRData() != null) {
                                for (FormField field : resData.getRData()) {
                                    if (Objects.equals(field.getControlID(), "BALTEXT")) {
                                        String balance = field.getControlValue();
                                        textView.setText(balance);
                                        setInfo(info, true);
                                    }
                                }
                            } else new ShowToast(requireContext(), getString(R.string.something_));

                        } else if (Objects.equals(resData.getStatus(), StatusEnum.TOKEN.getType())) {
                            setLoading(false);
                            InfoFragment.showDialog(this.getChildFragmentManager(), this);
                        } else {
                            setLoading(false);
                            new ShowToast(requireContext(), resData.getMessage());
                        }
                    } else {
                        setLoading(false);
                        new ShowToast(requireContext(), getString(R.string.unable_to_get_balance));
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            setLoading(false);
            new ShowToast(requireContext(), getString(R.string.fatal_error));
        }
    }


    @Override
    public void setFormNavigation(List<FormControl> forms, Modules modules) {
        AppLogger.Companion.getInstance().appLog(HomeFragment.class.getSimpleName(),
                new Gson().toJson(modules));
        EventBus.getDefault().postSticky(new BusData(new GroupForm(
                modules,
                null,
                forms,
                false),
                null,
                null));
        Intent i = new Intent(getActivity(), FalconHeavyActivity.class);
        startActivity(i);
    }


    private void setLoading(boolean b) {
        if (b) LoadingFragment.show(getChildFragmentManager());
        else LoadingFragment.dismiss(getChildFragmentManager());
    }

    @Override
    public void checkMiniStatement(Accounts accounts) {
        setLoading(true);
        subscribe.add(baseViewModel.checkMiniStatement(accounts, requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setOnMini, Throwable::printStackTrace));

    }

    private void setOnMini(DynamicResponse v) {
        setLoading(false);
        AppLogger.Companion.getInstance().appLog("Mini", new Gson().toJson(v.getResponse()));
        if (!nonCaps(v.getResponse()).equals(nonCaps(StatusEnum.ERROR.getType()))) {
            try {
                DynamicAPIResponse response = new DynamicAPIResponseConverter().to(
                        BaseClass.decryptLatest(
                                v.getResponse(),
                                baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                                true,
                                baseViewModel.dataSource.getDeviceData().getValue().getRun()));

                AppLogger.Companion.getInstance().appLog("Mini", new Gson().toJson(response));

                assert response != null;
                if (nonCaps(response.getStatus()).equals(nonCaps(StatusEnum.SUCCESS.getType()))) {
                    List<MiniStatement> dataList =
                            new MiniTypeConverter().from(new Gson()
                                    .toJson(response.getAccountStatement()));
                    MiniStatementFragment.setData(new MiniList(dataList));
                    BindingAdapterKt.navigate(this, widgetViewModel.navigation().navigateToMini());
                } else if (Objects.equals(response.getStatus(), StatusEnum.TOKEN.getType())) {
                    setLoading(false);
                    InfoFragment.showDialog(this.getChildFragmentManager(), this);
                } else {
                    AlertDialogFragment.newInstance(
                            new DialogData(
                                    R.string.error,
                                    response.getMessage(),
                                    R.drawable.warning_app
                            ),
                            getChildFragmentManager()
                    );
                }

            } catch (Exception e) {
                new ShowToast(requireContext(), getString(R.string.error_fetching_mini));
                e.printStackTrace();
            }

        } else {
            new ShowToast(requireContext(), getString(R.string.error_fetching_mini));
        }
    }

    @Override
    public void timeOut() {
        BindingAdapterKt.navigate(this, baseViewModel.navigationData.navigateLanding());
    }

    @Override
    public void onDialog() {
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> BindingAdapterKt.navigate(this, widgetViewModel
                        .navigation().navigateLanding()), 200);
    }
}