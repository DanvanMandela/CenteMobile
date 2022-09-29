package com.elmacentemobile.view.ep.adapter.holder;

import android.view.View;

import com.elmacentemobile.databinding.BlockSingleTextDropDownBinding;


public class SingleTextViewHolder {
    public View view;
    public BlockSingleTextDropDownBinding binding;

    public SingleTextViewHolder(BlockSingleTextDropDownBinding binding) {
        this.view = binding.getRoot();
        this.binding = binding;
    }
}
