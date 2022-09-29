package com.elmacentemobile.view.fragment.registration.self;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.elmacentemobile.R;
import com.elmacentemobile.databinding.FragmentSelfRegistrationBinding;
import com.elmacentemobile.util.ShowToast;
import com.elmacentemobile.util.callbacks.AppCallbacks;
import com.elmacentemobile.view.activity.MainActivity;
import com.elmacentemobile.view.model.AuthViewModel;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelfRegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class SelfRegistrationFragment extends Fragment implements AppCallbacks, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentSelfRegistrationBinding binding;
    private AuthViewModel authViewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SelfRegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelfRegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelfRegistrationFragment newInstance(String param1, String param2) {
        SelfRegistrationFragment fragment = new SelfRegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public boolean validateFields() {
        if (TextUtils.isEmpty(binding.editMobile.getText().toString())) {
            new ShowToast(requireContext(), getString(R.string.mobile_required), true);
            return false;
        } else {
            if (binding.editMobile.getText().toString().length() < 9) {
                new ShowToast(requireContext(), getString(R.string.mobile_less), true);
                return false;
            } else return true;
        }
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
        binding = FragmentSelfRegistrationBinding.inflate(inflater, container, false);
        setBinding();
        setViewModel();
        setOnClick();
        return binding.getRoot().getRootView();
    }

    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public void setBinding() {
        binding.setCallback(this);
    }

    @Override
    public void setOnClick() {
        binding.materialButton.setOnClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.materialButton)) {
            if (validateFields())
                ((MainActivity) requireActivity())
                        .provideNavigationGraph()
                        .navigate(authViewModel
                                .navigationDataSource
                                .navigateCardDetails(binding.countryCodeHolder
                                        .getSelectedCountryCode()
                                        + Objects.requireNonNull(binding.editMobile.getText())));
        }
    }
}