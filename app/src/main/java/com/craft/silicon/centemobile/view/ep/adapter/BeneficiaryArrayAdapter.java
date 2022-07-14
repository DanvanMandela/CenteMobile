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
import com.craft.silicon.centemobile.data.model.user.Beneficiary;
import com.craft.silicon.centemobile.databinding.BlockBeneficiaryItemBinding;
import com.craft.silicon.centemobile.view.ep.adapter.holder.BeneficiaryViewHolder;

import java.util.List;

public class BeneficiaryArrayAdapter extends ArrayAdapter<Beneficiary> {


    public BeneficiaryArrayAdapter(@NonNull Context context, int resource, @NonNull List<Beneficiary> objects) {
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
        BeneficiaryViewHolder holder;
        if (convertView == null) {
            BlockBeneficiaryItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.block_beneficiary_item, parent, false);
            holder = new BeneficiaryViewHolder(itemBinding);
            holder.view = itemBinding.getRoot();
            holder.view.setTag(holder);
            itemBinding.executePendingBindings();
        } else {
            holder = (BeneficiaryViewHolder) convertView.getTag();
        }
        holder.binding.setData(getItem(position));
        return holder.binding.getRoot();
    }


    public View initView(int i, View view, ViewGroup viewGroup) {
        BeneficiaryViewHolder holder;
        if (view == null) {
            BlockBeneficiaryItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.block_beneficiary_item, viewGroup, false);
            holder = new BeneficiaryViewHolder(itemBinding);
            holder.view = itemBinding.getRoot();
            holder.view.setTag(holder);
            itemBinding.executePendingBindings();
        } else {
            holder = (BeneficiaryViewHolder) view.getTag();
        }
        holder.binding.setData(getItem(i));
        return holder.binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Nullable
    @Override
    public Beneficiary getItem(int position) {
        return super.getItem(position);
    }
}
