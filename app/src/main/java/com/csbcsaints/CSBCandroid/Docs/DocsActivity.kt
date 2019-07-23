package com.csbcsaints.CSBCandroid

import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import com.csbcsaints.CSBCandroid.ui.UserFontFamilies
import com.csbcsaints.CSBCandroid.ui.UserFontStyles
import com.csbcsaints.CSBCandroid.ui.setCustomFont

//TODO - Add documents list in ListView, segue with selected to viewer (All docs are included in app install), add share button

class DocsActivity : CSBCAppCompatActivity() { //Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docs)
        setupViewForTabbedActivity(R.layout.activity_docs)
    }

    override fun tabSelectedHandler() {

    }


}


/* !!!!!SAVE THIS IT IS THE ONLY RECORD OF THE OLD WAY!!!!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val activityTag = arguments?.getInt("activityTag")
        val root = inflater.inflate(R.layout.activity_docs, container, false)
        val textView = root.findViewById<TextView>(R.id.testSchoolSelected)


        schoolSelected = schoolsList[(arguments?.getInt(DocsActivity.ARG_SECTION_NUMBER) ?: 1)-1]
        textView.text = schoolSelected



        return root
    }
    */
