package com.elmacentemobile.view.ep.adapter.holder;

import android.view.View;

import com.elmacentemobile.databinding.BlockBeneficiaryItemBinding;

public class BeneficiaryViewHolder {
    public View view;
    public BlockBeneficiaryItemBinding binding;

    public BeneficiaryViewHolder(BlockBeneficiaryItemBinding binding) {
        this.view = binding.getRoot();
        this.binding = binding;
    }
}
