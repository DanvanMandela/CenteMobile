package com.craft.silicon.centemobile.view.ep.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.craft.silicon.centemobile.data.model.user.PendingTransaction
import com.craft.silicon.centemobile.databinding.BlockPendingTransactionLayoutBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks

class PendingTransactionAdapterItem(
    private var itemList: MutableList<PendingTransaction>?,
    private val callback: AppCallbacks
) : RecyclerView.Adapter<PendingTransactionAdapterItem.Holder>() {

    private var itemListFiltered: MutableList<PendingTransaction>? = null


    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(list: MutableList<PendingTransaction>) {
        this.itemList = list
        notifyDataSetChanged()
    }

    class Holder(private val binding: BlockPendingTransactionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PendingTransaction, callback: AppCallbacks) {
            binding.data = data
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemBinding =
            BlockPendingTransactionLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(itemBinding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemListFiltered!![position], callback)
    }

    override fun getItemCount(): Int = itemList!!.size
}