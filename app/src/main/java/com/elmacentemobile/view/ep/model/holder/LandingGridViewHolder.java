package com.elmacentemobile.view.ep.model.holder;

import android.view.View;

import com.elmacentemobile.databinding.BlockLandingPageItemBinding;


public class LandingGridViewHolder {
    public View view;
    public BlockLandingPageItemBinding binding;

    public LandingGridViewHolder(BlockLandingPageItemBinding binding) {
        this.view = binding.getRoot();
        this.binding = binding;
    }
}
