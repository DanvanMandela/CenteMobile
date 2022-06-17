package com.craft.silicon.centemobile.view.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.data.model.DataResponse;
import com.craft.silicon.centemobile.data.model.converter.DataResponseTypeConverter;
import com.craft.silicon.centemobile.databinding.FragmentHomeBinding;
import com.craft.silicon.centemobile.util.JSONUtil;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.ep.controller.AppController;
import com.craft.silicon.centemobile.view.ep.data.AppData;
import com.craft.silicon.centemobile.view.ep.data.Body;
import com.craft.silicon.centemobile.view.ep.data.BodyData;
import com.craft.silicon.centemobile.view.ep.data.CardData;
import com.craft.silicon.centemobile.view.ep.data.HeaderData;
import com.craft.silicon.centemobile.view.ep.data.ModuleData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment implements AppCallbacks {


    private FragmentHomeBinding binding;
    private AppController controller;

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
        setController();
        setHomeData();
        return binding.getRoot().getRootView();
    }

    private void setHomeData() {

        DataResponse dataResponse = new JSONUtil().loadJSON(requireActivity());


        List<CardData> cardData = new ArrayList<>();
        cardData.add(new CardData("B"));
        cardData.add(new CardData("A"));


        ModuleData moduleData = new ModuleData(dataResponse.getModules().stream().filter(p -> p.getParentModule().equals("MAIN")).collect(Collectors.toList()));

        List<Body> bodyList = new ArrayList<>();
        bodyList.add(moduleData);


        BodyData bodyData = new BodyData(bodyList);

        AppData p = new HeaderData(cardData);


        List<AppData> s = new ArrayList<>();
        s.add(p);
        s.add(bodyData);
        controller.setData(s);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

        }, 300);

    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setCallback(this);
    }

    @Override
    public void setController() {
        controller = new AppController(this);
        binding.container.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.container.setController(controller);
    }
}