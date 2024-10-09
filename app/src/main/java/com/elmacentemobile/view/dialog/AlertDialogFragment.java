package com.elmacentemobile.view.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.elmacentemobile.R;
import com.elmacentemobile.databinding.FragmentAlertDialogBinding;
import com.elmacentemobile.util.callbacks.AppCallbacks;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertDialogFragment extends DialogFragment implements AppCallbacks {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATA = "data";
    private FragmentAlertDialogBinding binding;
    private DialogData dialogData;
    private AppCallbacks appCallbacks;


    public AlertDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param data DialogData.
     * @return A new instance of fragment AlertDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertDialogFragment newInstance(DialogData data, FragmentManager manager) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA, data);
        fragment.setArguments(args);
        fragment.show(manager, AlertDialogFragment.class.getSimpleName());
        return fragment;
    }

    public static AlertDialogFragment setCallback(AppCallbacks appCallbacks,
                                                  DialogData data,
                                                  FragmentManager manager) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.appCallbacks = appCallbacks;
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA, data);
        fragment.setArguments(args);
        fragment.show(manager, AlertDialogFragment.class.getSimpleName());
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dialogData = getArguments().getParcelable(ARG_DATA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAlertDialogBinding.inflate(inflater, container, false);
        setBinding();
        return binding.getRoot().getRootView();
    }


    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setData(dialogData);
        binding.setCallback(this);
    }


    @Override
    public void onDialog() {
        Objects.requireNonNull(getDialog()).dismiss();
        if (appCallbacks != null) appCallbacks.onDialog();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getDialog()).setCancelable(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.MyDialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

}

