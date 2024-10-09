package com.elmacentemobile.view.ep.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elmacentemobile.data.model.dynamic.TransactionDynamicList
import com.elmacentemobile.databinding.BlockTransactionItemDynamicLayoutBinding
import com.elmacentemobile.util.callbacks.AppCallbacks
import java.util.*

class TransactionDynamicAdapterItem(
    private var itemList: MutableList<TransactionDynamicList>,
    private val callback: AppCallbacks
) : RecyclerView.Adapter<TransactionDynamicAdapterItem.Holder>() {


    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(list: MutableList<TransactionDynamicList>) {
        this.itemList = list
        notifyDataSetChanged()
    }


    class Holder(private val binding: BlockTransactionItemDynamicLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TransactionDynamicList, callback: AppCallbacks) {
            binding.data = data
            binding.callback = callback
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemBinding =
            BlockTransactionItemDynamicLayoutBinding.inflate(
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