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
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails;
import com.craft.silicon.centemobile.databinding.BlockAutoCompleteItemBinding;
import com.craft.silicon.centemobile.view.ep.adapter.holder.AutoTextViewHolder;
import com.craft.silicon.centemobile.view.ep.adapter.holder.TextLayoutViewHolder;

import java.util.List;

public class NameBaseAdapter extends ArrayAdapter<String> {


    public NameBaseAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
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
        TextLayoutViewHolder holder;
        if (convertView == null) {
            BlockAutoCompleteItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.block_auto_complete_item, parent, false);
            holder = new TextLayoutViewHolder(itemBinding);
            holder.view = itemBinding.getRoot();
            holder.view.setTag(holder);
            itemBinding.executePendingBindings();
        } else {
            holder = (TextLayoutViewHolder) convertView.getTag();
        }
        holder.binding.businessName.setText(getItem(position));
        return holder.binding.getRoot();
    }


    public View initView(int i, View view, ViewGroup viewGroup) {
        AutoTextViewHolder holder;
        if (view == null) {
            BlockAutoCompleteItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.block_auto_complete_item, viewGroup, false);
            holder = new AutoTextViewHolder(itemBinding);
            holder.view = itemBinding.getRoot();
            holder.view.setTag(holder);
            itemBinding.executePendingBindings();
        } else {
            holder = (AutoTextViewHolder) view.getTag();
        }
        holder.binding.businessName.setText(getItem(i));
        return holder.binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }
}
