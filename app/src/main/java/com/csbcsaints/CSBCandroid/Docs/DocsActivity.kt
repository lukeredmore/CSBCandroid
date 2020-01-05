package com.csbcsaints.CSBCandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.csbcsaints.CSBCandroid.CSBCAppCompatActivity

//TODO - Pass document itself or local link to viewer

class DocsActivity : CSBCAppCompatActivity() {
    val documentTitles = arrayOf(
        arrayOf("SCC Parent - Student Handbook", "SCC Bell Schedule", "SCC Course Description and Information Guide", "SCC Monthly Calendar", "CSBC Calendar"),
        arrayOf(),
        arrayOf("All Saints Cafeteria Info","All Saints Illness Policy"),
        arrayOf("St. James Parent - Student Handbook","St. James Code of Conduct"))
    val pdfTitleStrings = arrayOf(
        arrayOf("https://csbcsaints.org/wp-content/uploads/SCC-Parent-Student-Handbook-2019-2020.pdf","https://csbcsaints.org/wp-content/uploads/Bell-Schedules-1.pdf","https://csbcsaints.org/wp-content/uploads/Course-Description-Guide-2019-20.pdf","https://csbcsaints.org/wp-content/uploads/2019-20-SCC-Monthly-Calendar.pdf","https://csbcsaints.org/wp-content/uploads/2019-2020-CSBC-School-Calendar.pdf"),
        arrayOf(),
        arrayOf("https://csbcsaints.org/wp-content/uploads/cafeteria-info.pdf","https://csbcsaints.org/wp-content/uploads/sick-policy.pdf"),
        arrayOf("https://csbcsaints.org/wp-content/uploads/rev9_18-Student-Parent-POLICIES-Handbook-12.pdf","https://csbcsaints.org/wp-content/uploads/Official-Code-of-Conduct.doc-2.pdf"))
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
            val docIntent = Intent(baseContext, ActualDocActivity::class.java).apply {
                putExtra("selectedLink", pdfTitleStrings[schoolSelectedInt][position])
                putExtra("selectedTitle", documentTitles[schoolSelectedInt][position])
            }
            startActivity(docIntent)

        }
    }
}
