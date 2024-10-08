package com.csbcsaints.CSBCandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.csbcsaints.CSBCandroid.UserFontFamilies
import com.csbcsaints.CSBCandroid.UserFontStyles
import com.csbcsaints.CSBCandroid.setCustomFont
import java.util.*

///Athletics data adapter
class AthleticsAdapter(private val context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val TYPE_ITEM = 0
    private val TYPE_SEPARATOR = 1
    val headerData = ArrayList<String>()
    val rowData : MutableMap<Int,AthleticsModelSingle> = mutableMapOf()
    private val sectionHeader = TreeSet<Int>()

    fun addItem(item: AthleticsModel, index: Int) {
        val itemToAdd = AthleticsModelSingle(item.title[index], item.level[index], item.time[index], item.date)
        rowData[headerData.size] = itemToAdd
        headerData.add("ROW")
//        notifyDataSetChanged()
    }

    fun addSectionHeaderItem(item: String) {
        headerData.add(item)
        sectionHeader.add(headerData.size - 1)
//        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (sectionHeader.contains(position)) TYPE_SEPARATOR else TYPE_ITEM
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getCount(): Int {
        return headerData.size
    }

    override fun getItem(position: Int): String {
        return headerData.get(position)
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
                    convertView = inflater.inflate(R.layout.athletics_list_layout, null)
                    holder.titleLabel = convertView!!.findViewById<View>(R.id.titleLabel) as TextView
                    holder.levelLabel = convertView!!.findViewById<View>(R.id.levelLabel) as TextView
                    holder.timeLabel = convertView!!.findViewById<View>(R.id.timeLabel) as TextView
                }
                TYPE_SEPARATOR -> {
                    convertView = inflater.inflate(R.layout.section_header_layout, null)
                    holder.textView = convertView!!.findViewById<View>(R.id.textSeparator) as TextView
                }
            }
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        val model = rowData[position]

        holder.textView?.text = headerData.get(position)
        holder.textView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
        holder.titleLabel?.text = model?.title
        holder.titleLabel?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
        holder.levelLabel?.text = model?.level
        holder.levelLabel?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.ITALIC)
        holder.timeLabel?.text = model?.time
        holder.timeLabel?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)

        return convertView
    }

}


class ViewHolder {
    var textView: TextView? = null
    var titleLabel: TextView? = null
    var levelLabel: TextView? = null
    var timeLabel: TextView? = null
}