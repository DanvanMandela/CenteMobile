package com.craft.silicon.centemobile.view.fragment.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.craft.silicon.centemobile.data.model.DataResponse;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.user.FrequentModules;
import com.craft.silicon.centemobile.databinding.FragmentHomeBinding;
import com.craft.silicon.centemobile.util.JSONUtil;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.OnAppData;
import com.craft.silicon.centemobile.view.ep.controller.AppController;
import com.craft.silicon.centemobile.view.ep.data.AppData;
import com.craft.silicon.centemobile.view.ep.data.BodyData;
import com.craft.silicon.centemobile.view.ep.data.HeaderData;
import com.craft.silicon.centemobile.view.model.AuthViewModel;

import java.util.ArrayList;
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
public class HomeFragment extends Fragment implements AppCallbacks, OnAppData {


    private FragmentHomeBinding binding;
    private AppController controller;
    private AuthViewModel authViewModel;
    private final List<AppData> appDataList = new ArrayList<>();


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
    public static HomeFragment newInstance(OnAppData onAppData) {
        HomeFragment homeFragment = new HomeFragment();
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
        setController();
        setHomeData();
        return binding.getRoot().getRootView();
    }


    private void getModuleData(List<FrequentModules> data) {
        DataResponse dataResponse = new JSONUtil().loadJSON(requireActivity());
        List<Modules> moduleData = dataResponse.getModules().stream().filter(p -> Objects.equals(p.getParentModule(), "MAIN")).collect(Collectors.toList());
        BodyData bodyData = new BodyData(moduleData, data);
        appDataList.add(bodyData);
        updateController();
    }


    private void updateController() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.shimmerContainer.stopShimmer();
            binding.shimmerContainer.setVisibility(View.GONE);
            controller.setData(appDataList);
        }, 1500);
    }

    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setHomeData() {
        subscribe.add(authViewModel.getAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    appDataList.add(new HeaderData(data));
                }, Throwable::printStackTrace));

        subscribe.add(authViewModel.getFrequentModules()
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getModuleData, Throwable::printStackTrace));


    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setCallback(this);
        binding.shimmerContainer.startShimmer();
    }

    @Override
    public void setController() {
        controller = new AppController(this);
        binding.container.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.container.setController(controller);
    }

    @Override
    public void onApData(@NonNull AppData appData) {

        appDataList.add(appData);
        controller.setData(appDataList);
        updateController();

    }
}