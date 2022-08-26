package com.craft.silicon.centemobile.view.fragment.go.steps;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.databinding.FragmentTermsAndConditionBinding;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.fragment.go.PagerData;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TermsAndConditionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class TermsAndConditionFragment extends Fragment implements AppCallbacks, View.OnClickListener {
    public PagerData pagerData;

    private FragmentTermsAndConditionBinding binding;


    public TermsAndConditionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TermsAndConditionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TermsAndConditionFragment newInstance(PagerData pagerData) {
        TermsAndConditionFragment fragment = new TermsAndConditionFragment();
        fragment.pagerData = pagerData;
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTermsAndConditionBinding.inflate(inflater, container, false);
        setBinding();
        setOnClick();
        setToolbar();
        return binding.getRoot().getRootView();
    }

    private void setToolbar() {
        if(pagerData!=null){
            binding.toolbar.setNavigationOnClickListener(view -> pagerData.onBack(1));
        }else{
            //BaseClass.show_toast(getActivity(), "---pagerData null 1----");
        }

    }

    @Override
    public void setBinding() {
        int animationDuration = requireContext().getResources()
                .getInteger(R.integer.animation_duration);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            stopShimmer();
            binding.mainLay.setVisibility(View.VISIBLE);
            setWebView();
        }, animationDuration);

    }

    @Override
    public boolean validateFields() {
        if (binding.termsCheck.isChecked())
            return true;
        else {
            new ShowToast(requireContext(),
                    getString(R.string.please_agree_terms), true);
            return false;
        }
    }

    @Override
    public void setOnClick() {
        binding.buttonNext.setOnClickListener(this);
        binding.buttonBack.setOnClickListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        binding.webView2.getSettings().setJavaScriptEnabled(true);
        binding.webView2.loadUrl(getString(R.string.terms));
    }

    private void stopShimmer() {
        binding.shimmerContainer.setVisibility(View.GONE);
        binding.shimmerContainer.stopShimmer();
    }

    @Override
    public void onClick(View view) {
        if(pagerData!=null){
            if (view.equals(binding.buttonNext)) {
                if (validateFields()) pagerData.onNext(3);
            } else if (view.equals(binding.buttonBack)) {
                pagerData.onBack(1);
            }
        }else{
            //BaseClass.show_toast(getActivity(), "---pagerData null----");
        }

    }
}