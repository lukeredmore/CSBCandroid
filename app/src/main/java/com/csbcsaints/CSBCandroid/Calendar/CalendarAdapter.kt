package com.csbcsaints.CSBCandroid.Calendar

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.csbcsaints.CSBCandroid.EventsModel
import com.csbcsaints.CSBCandroid.R
import com.csbcsaints.CSBCandroid.UserFontFamilies
import com.csbcsaints.CSBCandroid.UserFontStyles
import com.csbcsaints.CSBCandroid.setCustomFont
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(private val context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val TYPE_ITEM = 0
    private val TYPE_SEPARATOR = 1
    val headerData = ArrayList<String>()
    val rowData : MutableList<EventsModel> = arrayListOf()
    private val sectionHeader = TreeSet<Int>()

    fun addItem(item: EventsModel) {
        rowData.add(item)
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
        var holder: CalendarViewHolder? = null
        val rowType = getItemViewType(position)

        if (convertView == null) {
            holder = CalendarViewHolder()
            when (rowType) {
                TYPE_ITEM -> {
                    convertView = inflater.inflate(R.layout.calendar_list_layout, null)
                    holder.titleLabel = convertView!!.findViewById<View>(R.id.titleLabel) as TextView
                    holder.monthLabel = convertView!!.findViewById<View>(R.id.monthLabel) as TextView
                    holder.dayLabel = convertView!!.findViewById<View>(R.id.dayLabel) as TextView
                    holder.timeLabel = convertView!!.findViewById<View>(R.id.timeLabel) as TextView
                    holder.schoolsLabel = convertView!!.findViewById<View>(R.id.schoolsLabel) as TextView
                }
                TYPE_SEPARATOR -> {
                    convertView = inflater.inflate(R.layout.calendar_supplemental_list_layout, null)
                    holder.textView = convertView!!.findViewById(R.id.nameTextField)
                    holder.container = convertView!!.findViewById(R.id.container)
                }
            }
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as CalendarViewHolder
        }



        holder.textView?.text = headerData.get(position)
        holder.textView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.ITALIC)
        if (holder.textView?.text == "View More >") {
            holder.textView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
            holder.container?.setOnClickListener(View.OnClickListener {
                val builder : CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                val customTabsIntent : CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(parent.context, Uri.parse("https://csbcsaints.org/calendar"))
            })
        }

        if (rowData.count() > 0 && position < rowData.count()) {
            val model = rowData[position]
            holder.titleLabel?.text = model.event.toUpperCase()
            holder.titleLabel?.setCustomFont(UserFontFamilies.MONTSERRAT, UserFontStyles.SEMIBOLD)
            holder.monthLabel?.text = SimpleDateFormat("MMM").format(model.date.time)
            holder.monthLabel?.setCustomFont(UserFontFamilies.MONTSERRAT, UserFontStyles.REGULAR)
            holder.dayLabel?.text = SimpleDateFormat("dd").format(model.date.time)
            holder.dayLabel?.setCustomFont(UserFontFamilies.MONTSERRAT, UserFontStyles.SEMIBOLD)
            holder.timeLabel?.text = model.time
            holder.timeLabel?.setCustomFont(UserFontFamilies.MONTSERRAT, UserFontStyles.ITALIC)
            holder.schoolsLabel?.text = model.schools
            holder.schoolsLabel?.setCustomFont(UserFontFamilies.MONTSERRAT, UserFontStyles.REGULAR)
        }

        return convertView
    }

}


class CalendarViewHolder {
    var textView: TextView? = null
    var container : ConstraintLayout? = null
    var titleLabel: TextView? = null
    var monthLabel: TextView? = null
    var dayLabel: TextView? = null
    var timeLabel: TextView? = null
    var schoolsLabel: TextView? = null
}