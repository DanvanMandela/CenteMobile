package com.elmacentemobile.view.ep.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elmacentemobile.databinding.BlockLandingPageItemBinding

import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.LandingPageItem

class LandingPageAdapterItem(
    private var itemList: MutableList<LandingPageItem>,
    private val callback: AppCallbacks
) : RecyclerView.Adapter<LandingPageAdapterItem.Holder>() {


    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(list: MutableList<LandingPageItem>) {
        this.itemList = list
        notifyDataSetChanged()
    }


    class Holder(private val binding: BlockLandingPageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: LandingPageItem, callback: AppCallbacks) {
            binding.data = data
            binding.callback = callback
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemBinding =
            BlockLandingPageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(itemBinding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemList[position], callback)
    }

    override fun getItemCount(): Int = itemList.size

}