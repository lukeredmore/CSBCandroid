package com.csbcsaints.CSBCandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.csbcsaints.CSBCandroid.Options.ActivePassesActivity
import com.csbcsaints.CSBCandroid.writeToScreen
import java.util.*

///Active Passes data adapter
class ActivePassesAdapter(private val activePassesActivity: ActivePassesActivity) : BaseAdapter() {
    private val inflater: LayoutInflater = activePassesActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val TYPE_ITEM = 0

    private val listData = ArrayList<Triple<String,String,String>>()

    fun addItem(item: Triple<String, String, String>) {
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

    override fun getItem(position: Int): Triple<String,String,String> {
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
                    convertView = inflater.inflate(R.layout.active_pass_cell_layout, null)
                    holder.cell = convertView.findViewById(R.id.cell)
                    holder.studentNameTextView = convertView.findViewById(R.id.nameTextField)
                    holder.timeElapsedTextView = convertView.findViewById(R.id.timeElapsedTextField)
                    holder.locationTextView = convertView.findViewById(R.id.locationTextField)
                }
            }
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as PassesViewHolder
        }

        val model = listData[position]

        holder.studentNameTextView?.text = model.first
        holder.timeElapsedTextView?.text = model.second
        holder.locationTextView?.text = model.third
        holder.cell?.setOnClickListener {
            activePassesActivity.writeToScreen("This feature is not currently supported")
        }

        return convertView
    }

}


class PassesViewHolder {
    var cell : ConstraintLayout? = null
    var studentNameTextView: TextView? = null
    var timeElapsedTextView: TextView? = null
    var locationTextView: TextView? = null
}