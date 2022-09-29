package com.elmacentemobile.view.fragment.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.elmacentemobile.databinding.FragmentSplashBinding;
import com.elmacentemobile.util.callbacks.AppCallbacks;
import com.elmacentemobile.view.binding.BindingAdapterKt;
import com.elmacentemobile.view.model.WidgetViewModel;
import com.scottyab.rootbeer.RootBeer;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class SplashFragment extends Fragment implements AppCallbacks {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentSplashBinding binding;
    private WidgetViewModel widgetViewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SplashFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SplashFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SplashFragment newInstance(String param1, String param2) {
        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        setBinding();
        return binding.getRoot().getRootView();
    }

    @Override
    public void onStart() {
        super.onStart();
        setViewModel();
    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @Override
    public void setViewModel() {
        RootBeer rootBeer = new RootBeer(requireContext());
        widgetViewModel = new ViewModelProvider(this).get(WidgetViewModel.class);
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                BindingAdapterKt.navigate(this, rootBeer.isRooted() ? widgetViewModel
                        .navigation().navigateToDeviceRooted() : widgetViewModel
                        .navigation().navigateLanding()), 1000);
    }
}