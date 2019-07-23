package com.csbcsaints.CSBCandroid.Today

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.csbcsaints.CSBCandroid.R
import android.widget.TextView
import com.csbcsaints.CSBCandroid.AthleticsModel
import com.csbcsaints.CSBCandroid.Calendar.EventsModel
import com.csbcsaints.CSBCandroid.ui.UserFontFamilies
import com.csbcsaints.CSBCandroid.ui.UserFontStyles
import com.csbcsaints.CSBCandroid.ui.setCustomFont
import java.util.*



class TodayAdapter(private val context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val TYPE_ITEM = 0
    private val TYPE_SEPARATOR = 1
    private val TYPE_HEADER = 2
    var rowData : MutableMap<Int, EventsModel> = mutableMapOf()
    private val sectionHeader = TreeSet<Int>()
    var tableHeader = ""
    var itemCount = 0

    fun addItem(item: EventsModel) {
        println("Adding event at $itemCount")
        rowData[itemCount] = item
        itemCount += 1
        notifyDataSetChanged()
    }
    fun addItem(item: AthleticsModel) {
        for (i in 0 until item.sport.size) {
            println("Adding athletics at $itemCount")
            val titleText = (item.gender[i] + "'s " + item.sport[i] + " " + item.homeGame[i] + " " + item.opponent[i])
            val itemToAppend : EventsModel = EventsModel(
                titleText, item.date, item.date, item.date, item.time[i], item.level[i]
            )
            rowData[itemCount] = itemToAppend
            itemCount += 1
        }
        notifyDataSetChanged()
    }
    fun addSeparator(text: String) {
        println("Adding separator at $itemCount")
        val itemToAppend : EventsModel = EventsModel(
            "SEPARATOR", text, text, text,text, text
        )
        rowData[itemCount] = itemToAppend
        itemCount += 1
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        println(rowData[itemCount]?.event)
        println(rowData[itemCount]?.month)
        if (rowData[itemCount]?.event == "SEPARATOR") {
            println("row $position is a seap")
            return TYPE_SEPARATOR
        } else {
            println("row $position is a item")
            return TYPE_ITEM
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getCount(): Int {
        return itemCount + 1
    }

    override fun getItem(position: Int): String {
        return tableHeader
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: TodayViewHolder? = null
        val rowType = getItemViewType(position)

        if (convertView == null) {
            holder = TodayViewHolder()
            when (rowType) {
                TYPE_SEPARATOR -> {
                    convertView = inflater.inflate(R.layout.section_header_layout, null)
                    holder.text = convertView!!.findViewById(R.id.text) as TextView
//                    holder.text!!.setText(rowData[position]?.month)
                }
                TYPE_ITEM -> {
                    //convertView = inflater.inflate(R.layout.section_header_layout, null)

                    convertView = inflater.inflate(R.layout.athletics_list_layout, null)
                    holder.titleLabel = convertView!!.findViewById<View>(R.id.titleLabel) as TextView
                    holder.levelLabel = convertView!!.findViewById<View>(R.id.levelLabel) as TextView
                    holder.timeLabel = convertView!!.findViewById<View>(R.id.timeLabel) as TextView
////                    holder.titleLabel!!.setText(rowData[position]?.event)
//                    holder.levelLabel!!.setText(rowData[position]?.schools)
//                    holder.timeLabel!!.setText(rowData[position]?.time)
                }
            }
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as TodayViewHolder
        }

        val model = rowData[position]

        holder.textView?.setText(model?.month)
        holder.text?.setText(model?.month)
        holder.titleLabel?.setText(model?.event)
        holder.levelLabel?.setText(model?.schools)
        holder.timeLabel?.setText(model?.time)

        return convertView
    }



    class TodayViewHolder {
        var textView: TextView? = null
        var titleLabel: TextView? = null
        var levelLabel: TextView? = null
        var timeLabel: TextView? = null
        var text: TextView? = null
    }

}