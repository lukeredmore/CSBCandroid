package com.csbcsaints.CSBCandroid.Connect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.csbcsaints.CSBCandroid.R
import android.widget.TextView
import com.csbcsaints.CSBCandroid.ui.UserFontFamilies
import com.csbcsaints.CSBCandroid.ui.UserFontStyles
import com.csbcsaints.CSBCandroid.ui.setCustomFont
import java.util.*


class ConnectAdapter(private val context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val TYPE_ITEM = 0
    private val TYPE_SEPARATOR = 1
    val mData = ArrayList<String>()
    private val sectionHeader = TreeSet<Int>()

    fun addItem(item: String) {
        mData.add(item)
        notifyDataSetChanged()
    }

    fun addSectionHeaderItem(item: String) {
        mData.add(item)
        sectionHeader.add(mData.size - 1)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (sectionHeader.contains(position)) TYPE_SEPARATOR else TYPE_ITEM
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(position: Int): String {
        return mData.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: ViewHolder? = null
        val rowType = getItemViewType(position)

        if (convertView == null) {
            holder = ViewHolder()
            when (rowType) {
                TYPE_ITEM -> {
                    convertView = inflater.inflate(R.layout.single_line_list_layout, null)
                    holder.textView = convertView!!.findViewById<View>(R.id.text) as TextView
                    holder.textView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
                }
                TYPE_SEPARATOR -> {
                    convertView = inflater.inflate(R.layout.section_header_layout, null)
                    holder.textView = convertView!!.findViewById<View>(R.id.textSeparator) as TextView
                    holder.textView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
                }
            }
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.textView!!.setText(mData.get(position))

        return convertView
    }



    class ViewHolder {
        var textView: TextView? = null
    }

}