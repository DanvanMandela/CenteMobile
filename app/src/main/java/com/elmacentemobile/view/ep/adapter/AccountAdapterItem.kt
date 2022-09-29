package com.elmacentemobile.view.ep.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elmacentemobile.data.model.user.Accounts
import com.elmacentemobile.databinding.BlockHeaderItemCardLayoutBinding
import com.elmacentemobile.util.callbacks.AppCallbacks

class AccountAdapterItem(
    private var itemList: MutableList<Accounts>,
    private val callBacks: AppCallbacks
) : RecyclerView.Adapter<AccountAdapterItem.Holder>() {

    class Holder private constructor(private val binding: BlockHeaderItemCardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Accounts, callBacks: AppCallbacks) {
            binding.callback = callBacks
            binding.data = data
        }

        companion object {
            fun create(parent: ViewGroup): Holder {
                val itemBinding =
                    BlockHeaderItemCardLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )

                return Holder(itemBinding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder = Holder.create(parent)

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(itemList[position], callBacks)
    }

    override fun getItemCount(): Int = itemList.size

    fun currentItem(position: Int): Accounts = itemList[position]

}