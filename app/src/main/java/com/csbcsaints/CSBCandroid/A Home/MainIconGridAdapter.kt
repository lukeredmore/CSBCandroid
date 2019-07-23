package com.csbcsaints.CSBCandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.csbcsaints.CSBCandroid.ui.UserFontFamilies
import com.csbcsaints.CSBCandroid.ui.UserFontStyles
import com.csbcsaints.CSBCandroid.ui.setCustomFont
import kotlinx.android.synthetic.main.home_grid_layout.view.*

class MainIconGridAdapter : BaseAdapter {
    var iconsList = ArrayList<Icon>()
    var context: Context? = null

    constructor(context: Context, iconsList: ArrayList<Icon>) : super() {
        this.context = context
        this.iconsList = iconsList
    }

    override fun getCount(): Int {
        return iconsList.size
    }

    override fun getItem(position: Int): Any {
        return iconsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val food = this.iconsList[position]

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var iconView = inflator.inflate(R.layout.home_grid_layout, null)
        iconView.iconImage.setImageResource(food.image!!)
        iconView.iconLabel.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
        iconView.iconLabel.text = food.name!!

        return iconView
    }
}