package com.elmacentemobile.view.ep.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.elmacentemobile.databinding.BlockMiniStatementItemBinding

import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.MiniStatement
import java.util.*

class MiniAdapterItem(
    private var itemList: MutableList<MiniStatement>?,
    private val callback: AppCallbacks
) : RecyclerView.Adapter<MiniAdapterItem.Holder>(), Filterable {

    private var itemListFiltered: MutableList<MiniStatement>? = null


    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(list: MutableList<MiniStatement>) {
        this.itemList = list
        this.itemListFiltered = list
        notifyDataSetChanged()
    }

    init {
        itemListFiltered = itemList
    }

    class Holder(private val binding: BlockMiniStatementItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MiniStatement, callback: AppCallbacks) {
            binding.data = data
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemBinding =
            BlockMiniStatementItemBinding.inflate(
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
                itemListFiltered = if (charString.isEmpty()) {
                    itemList
                } else {
                    val filteredList: MutableList<MiniStatement> = ArrayList()
                    for (row in itemList!!) {
                        if (row.narration!!.lowercase(Locale.getDefault())
                                .contains(charString.lowercase(Locale.getDefault()))
                            || row.date.toString().lowercase(Locale.getDefault())
                                .contains(charSequence)
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
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
                itemListFiltered = filterResults.values as MutableList<MiniStatement>?
                notifyDataSetChanged()
            }
        }
    }
}