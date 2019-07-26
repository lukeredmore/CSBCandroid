package com.csbcsaints.CSBCandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.csbcsaints.CSBCandroid.Docs.ActualDocActivity
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity

//TODO - Pass document itself or local link to viewer

class DocsActivity : CSBCAppCompatActivity() { //Fragment() {

    val documentTitles = arrayOf(
        arrayOf("SCC Parent - Student Handbook", "SCC Bell Schedule", "SCC Course Description and Information Guide", "SCC Monthly Calendar", "CSBC Calendar", "SCC Dress Code"),
        arrayOf(""),
        arrayOf("All Saints Cafeteria Info","All Saints Illness Policy"),
        arrayOf("St. James Parent - Student Handbook","St. James Code of Conduct"))
    val pdfTitleStrings = arrayOf(
        arrayOf("scchandbook18-19","sccbellschedule18-19","scccoursedescription18-19","sccmonthlycalendar18-19","csbccalendar18-19","sccdresscode18-19"),
        arrayOf(),
        arrayOf("saintscafeteriainfo18-19","saintssickpolicy18-19"),
        arrayOf("jameshandbook18-19","jamescodeofconduct18-19"))
    var documentListView : ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docs)

        documentListView = findViewById(R.id.documentListView)

        setupViewForTabbedActivity(R.layout.activity_docs)
    }

    override fun tabSelectedHandler() {
        val connectAdapter = SingleLineListAdapter(this)
        for(pdfTitle in documentTitles[schoolSelectedInt]) {
            connectAdapter.addItem(pdfTitle)
        }

        documentListView?.adapter = connectAdapter
        documentListView?.setOnItemClickListener{ adapterView: AdapterView<*>?, view: View?, position: Int, l: Long ->
            println("The document selected has link: " + pdfTitleStrings[schoolSelectedInt][position])
            val intent = Intent(baseContext, ActualDocActivity::class.java)
            intent.putExtra("selectedLink", pdfTitleStrings[schoolSelectedInt][position])
            startActivityForResult(intent, MainActivity.START_CALENDAR_ACTIVITY_REQUEST_CODE)

        }
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
