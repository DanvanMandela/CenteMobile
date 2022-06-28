package com.craft.silicon.centemobile.view.ep.adapter.holder;

import android.view.View;

import com.craft.silicon.centemobile.databinding.BlockAutoCompleteItemBinding;


public class AutoTextViewHolder {
    public View view;
    public BlockAutoCompleteItemBinding binding;

    public AutoTextViewHolder(BlockAutoCompleteItemBinding binding) {
        this.view = binding.getRoot();
        this.binding = binding;
    }
}
