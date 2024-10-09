package com.elmacentemobile.view.ep.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.elmacentemobile.data.receiver.NotificationData
import com.elmacentemobile.databinding.BlockNotificationLayoutBinding

import com.elmacentemobile.util.callbacks.AppCallbacks
import java.util.*

class NotificationAdapterItem(
    private var itemList: MutableList<NotificationData>,
    private val callback: AppCallbacks
) : RecyclerView.Adapter<NotificationAdapterItem.Holder>(), Filterable {


    private var itemListFiltered: MutableList<NotificationData>? = null


    @SuppressLint("NotifyDataSetChanged")
    fun replaceData(list: MutableList<NotificationData>) {
        this.itemList = list
        this.itemListFiltered = list
        notifyDataSetChanged()
    }

    init {
        itemListFiltered = itemList
    }

    class Holder(private val binding: BlockNotificationLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NotificationData, callback: AppCallbacks) {
            binding.data = data
            binding.callback = callback
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val itemBinding =
            BlockNotificationLayoutBinding.inflate(
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
                    val filteredList: MutableList<NotificationData> = ArrayList()
                    for (row in itemList) {
                        if (row.title!!.lowercase(Locale.getDefault())
                                .contains(charString.lowercase(Locale.getDefault()))
                            || row.body.toString().lowercase(Locale.getDefault())
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
                itemListFiltered = filterResults.values as MutableList<NotificationData>?
                notifyDataSetChanged()
            }
        }
    }

}