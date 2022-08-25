package com.craft.silicon.centemobile.view.ep.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.setImageRes
import com.craft.silicon.centemobile.view.binding.textStrRes
import com.craft.silicon.centemobile.view.ep.data.LandingPageItem
import com.google.android.material.card.MaterialCardView

class LandingItemGrid(
    private val context: Context,
    private val mutableList: MutableList<LandingPageItem>,
    private val appCallbacks: AppCallbacks
) :
    BaseAdapter() {
    //declaring variables
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageView: ImageView
    private lateinit var title: TextView
    private lateinit var card: MaterialCardView

    override fun getCount(): Int {

        return mutableList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var listView = convertView
        if (layoutInflater == null) {

            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            listView = layoutInflater!!.inflate(R.layout.block_landing_page_item, null)
        }

        imageView = listView!!.findViewById(R.id.avatar)
        title = listView!!.findViewById(R.id.title)
        card = listView.findViewById(R.id.cardItem)

        imageView.setImageRes(mutableList[position].avatar)
        title.textStrRes(mutableList[position].title)

        card.setOnClickListener {
            appCallbacks.onLanding(mutableList[position])
        }
        return listView
    }
}