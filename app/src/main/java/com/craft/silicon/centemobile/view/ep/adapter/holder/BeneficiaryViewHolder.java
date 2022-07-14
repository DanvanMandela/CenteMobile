package com.craft.silicon.centemobile.view.ep.adapter.holder;

import android.view.View;

import com.craft.silicon.centemobile.databinding.BlockBeneficiaryItemBinding;

public class BeneficiaryViewHolder {
    public View view;
    public BlockBeneficiaryItemBinding binding;

    public BeneficiaryViewHolder(BlockBeneficiaryItemBinding binding) {
        this.view = binding.getRoot();
        this.binding = binding;
    }
}
