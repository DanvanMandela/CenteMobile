package com.craft.silicon.centemobile.view.ep.adapter.holder;

import android.view.View;

import com.craft.silicon.centemobile.databinding.BlockSingleTextDropDownBinding;

public class SingleTextViewHolder {
    public View view;
    public BlockSingleTextDropDownBinding binding;

    public SingleTextViewHolder(BlockSingleTextDropDownBinding binding) {
        this.view = binding.getRoot();
        this.binding = binding;
    }
}
