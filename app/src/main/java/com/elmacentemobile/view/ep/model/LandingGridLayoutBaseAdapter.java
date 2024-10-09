package com.elmacentemobile.view.ep.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.elmacentemobile.R;
import com.elmacentemobile.databinding.BlockLandingPageItemBinding;
import com.elmacentemobile.util.callbacks.AppCallbacks;
import com.elmacentemobile.view.ep.data.LandingPageItem;
import com.elmacentemobile.view.ep.model.holder.LandingGridViewHolder;

import java.util.List;


public class LandingGridLayoutBaseAdapter extends BaseAdapter {

    private final List<LandingPageItem> itemList;
    private final AppCallbacks appCallbacks;

    public LandingGridLayoutBaseAdapter(List<LandingPageItem> itemList,
                                        AppCallbacks appCallbacks) {
        this.itemList = itemList;
        this.appCallbacks = appCallbacks;
    }


    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LandingGridViewHolder holder;
        if (view == null) {
            BlockLandingPageItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.block_landing_page_item, viewGroup, false);
            holder = new LandingGridViewHolder(itemBinding);
            holder.view = itemBinding.getRoot();
            holder.view.setTag(holder);
            itemBinding.executePendingBindings();
        } else {
            holder = (LandingGridViewHolder) view.getTag();
        }
        holder.binding.setData(itemList.get(i));
        holder.binding.setCallback(appCallbacks);
        return holder.binding.getRoot();
    }
}
