package com.elmacentemobile.view.ep.adapter.holder;

import android.view.View;

import com.elmacentemobile.databinding.BlockAutoCompleteItemBinding;


public class TextLayoutViewHolder {
    public View view;
    public BlockAutoCompleteItemBinding binding;

    public TextLayoutViewHolder(BlockAutoCompleteItemBinding binding) {
        this.view = binding.getRoot();
        this.binding = binding;
    }
}
