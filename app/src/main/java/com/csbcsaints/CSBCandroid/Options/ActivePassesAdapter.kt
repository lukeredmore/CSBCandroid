package com.csbcsaints.CSBCandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.csbcsaints.CSBCandroid.ui.UserFontFamilies
import com.csbcsaints.CSBCandroid.ui.UserFontStyles
import com.csbcsaints.CSBCandroid.ui.setCustomFont
import java.util.*

///Active Passes data adapter
class ActivePassesAdapter(context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val TYPE_ITEM = 0

    private val listData = ArrayList<Pair<String,String>>()
    fun addItem(item: Pair<String, String>) {
        listData.add(item)
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_ITEM
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(position: Int): Pair<String,String> {
        return listData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: PassesViewHolder?
        val rowType = getItemViewType(position)

        if (convertView == null) {
            holder = PassesViewHolder()
            when (rowType) {
                TYPE_ITEM -> {
                    convertView = inflater.inflate(R.layout.single_line_list_with_detail_layout, null)
                    holder.text = convertView!!.findViewById<View>(R.id.text) as TextView
                    holder.detailText = convertView!!.findViewById<View>(R.id.detailText) as TextView
                }
            }
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as PassesViewHolder
        }

        val model = listData[position]

        holder.text?.text = model.first
        holder.detailText?.text = model.second

        return convertView
    }

}


class PassesViewHolder {
    var text: TextView? = null
    var detailText: TextView? = null
}