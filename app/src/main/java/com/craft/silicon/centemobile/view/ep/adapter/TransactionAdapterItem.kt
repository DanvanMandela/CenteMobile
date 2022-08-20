package com.craft.silicon.centemobile.view.ep.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.craft.silicon.centemobile.data.model.dynamic.TransactionData
import com.craft.silicon.centemobile.databinding.BlockTransactionItemLayoutBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import java.util.*

class TransactionAdapterItem(
    private var itemList: MutableList<TransactionData>,
    private val callback: AppCallbacks
) : RecyclerView.Adapter<TransactionAdapterItem.Holder>(), Filterable {


    private var itemListFiltered: MutableList<TransactionData>? = null


    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(list: MutableList<TransactionData>) {
        this.itemList = list
        this.itemListFiltered = list
        notifyDataSetChanged()
    }

    init {
        itemListFiltered = itemList
    }

    class Holder(private val binding: BlockTransactionItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TransactionData, callback: AppCallbacks) {
            binding.data = data
            binding.callback = callback
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemBinding =
            BlockTransactionItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return Holder(itemBinding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemListFiltered!![position], callback)
    }

    override fun getItemCount(): Int = itemListFiltered!!.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    itemListFiltered = itemList
                } else {
                    val filteredList: MutableList<TransactionData> = ArrayList()
                    for (row in itemList) {
                        if (row.type!!.lowercase(Locale.getDefault())
                                .contains(charString.lowercase(Locale.getDefault()))
                            || row.refNo.toString().lowercase(Locale.getDefault())
                                .contains(charSequence)
                        ) {
                            filteredList.add(row)
                        }
                    }
                    itemListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = itemListFiltered
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                itemListFiltered = filterResults.values as MutableList<TransactionData>?
                notifyDataSetChanged()
            }
        }
    }
}