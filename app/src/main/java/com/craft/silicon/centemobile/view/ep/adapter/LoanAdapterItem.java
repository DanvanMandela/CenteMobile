package com.craft.silicon.centemobile.view.ep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.data.model.LoanData;
import com.craft.silicon.centemobile.databinding.BlockSingleTextDropDownBinding;
import com.craft.silicon.centemobile.view.ep.adapter.holder.SingleTextViewHolder;

import java.util.List;
import java.util.Objects;

public class LoanAdapterItem extends ArrayAdapter<LoanData> {


    public LoanAdapterItem(@NonNull Context context,
                           int resource,
                           @NonNull List<LoanData> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = textView(position, convertView, parent);
        view.setPadding(0, view.getPaddingTop(), 0, view.getPaddingBottom());
        return view;
    }

    private View textView(int position, View convertView, ViewGroup parent) {
        SingleTextViewHolder holder;
        if (convertView == null) {
            BlockSingleTextDropDownBinding itemBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                            R.layout.block_single_text_drop_down, parent, false);
            holder = new SingleTextViewHolder(itemBinding);
            holder.view = itemBinding.getRoot();
            holder.view.setTag(holder);
            itemBinding.executePendingBindings();
        } else {
            holder = (SingleTextViewHolder) convertView.getTag();
        }
        holder.binding.setData(Objects.requireNonNull(getItem(position)).getDescription());
        return holder.binding.getRoot();
    }


    public View initView(int i, View view, ViewGroup viewGroup) {
        SingleTextViewHolder holder;
        if (view == null) {
            BlockSingleTextDropDownBinding itemBinding
                    = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                    R.layout.block_single_text_drop_down, viewGroup, false);
            holder = new SingleTextViewHolder(itemBinding);
            holder.view = itemBinding.getRoot();
            holder.view.setTag(holder);
            itemBinding.executePendingBindings();
        } else {
            holder = (SingleTextViewHolder) view.getTag();
        }
        holder.binding.setData(Objects.requireNonNull(getItem(i)).getDescription());
        return holder.binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Nullable
    @Override
    public LoanData getItem(int position) {
        return super.getItem(position);
    }
}
