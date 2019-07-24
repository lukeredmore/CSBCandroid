package com.csbcsaints.CSBCandroid

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity

//TODO - ensure all accounts still work, add new accounts, open in correct app, depending on what's installed

class ConnectActivity : CSBCAppCompatActivity() { //Fragment() {

    val socialArray = arrayOf(
        arrayOf( //Seton
            arrayOf(
                "Catholic Schools of Broome County",
                "Dr. Elizabeth Carter, President",
                "Seton Catholic Central",
                "Matthew Martinkovic, Principal",
                "SCC Student Council",
                "SCC Key Club",
                "SCC Fan Club",
                "SCC Junior Fan Club"
            ), //t
            arrayOf("Catholic Schools of Broome County", "Seton Catholic Central", "SCC Junior Fan Club"), //f
            arrayOf("SCC Junior Fan Club") //i
        ), arrayOf( //St. John's
            arrayOf("Catholic Schools of Broome County", "Dr. Elizabeth Carter, President", "St. John School"), //t
            arrayOf("Catholic Schools of Broome County", "St. John School") //f
        ), arrayOf( //All Saints
            arrayOf(
                "Catholic Schools of Broome County",
                "Dr. Elizabeth Carter, President",
                "Angela Tierno-Sherwood, Principal"
            ), // t
            arrayOf("Catholic Schools of Broome County", "All Saints School") //f
        ), arrayOf( //St. James
            arrayOf(
                "Catholic Schools of Broome County",
                "Dr. Elizabeth Carter, President",
                "St. James School",
                "Suzy Kitchen, Principal"
            ), //t
            arrayOf("Catholic Schools of Broome County", "St. James School"), //f
            arrayOf("St. James School", "Suzy Kitchen") //i
        )
    )
    val socialURLArray = arrayOf(
        arrayOf( //Seton
            arrayOf(
                "CatholicSchools",
                "CatholicSchPres",
                "SetonCatholicNY",
                "SCCPrincipal",
                "studentcouncSCC",
                "scckeyclub",
                "sccgreenhouse",
                "SCCJrFanClub"
            ), //t
            arrayOf("103950839670987", "197877966951594", "608965236166888"), //f
            arrayOf("sccjrfanclub") //i
        ), arrayOf( //St. John's
            arrayOf("CatholicSchools", "CatholicSchPres", "StJohnSchoolBin"), //t
            arrayOf("103950839670987", "399338100169777") //f
        ), arrayOf( //All Saints
            arrayOf("CatholicSchools", "CatholicSchPres", "atierno_"), //t
            arrayOf("103950839670987", "210263249141313") //f
        ), arrayOf( //St. James
            arrayOf("CatholicSchools", "CatholicSchPres", "StJamesSchoolJC", "StJamesJC"), //t
            arrayOf("103950839670987", "136066559773647"), //f
            arrayOf("stjamesschooljc", "stjamesjcprincipal") //i
        )
    )
    val tableHeaders = arrayOf("Twitter", "Facebook", "Instagram")
    var linkList : MutableList<String> = mutableListOf<String>()
    var listView : ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        listView = findViewById(R.id.listView)

        setupViewForTabbedActivity(R.layout.activity_connect)
    }

    override fun tabSelectedHandler() {
        val connectAdapter = SingleLineListAdapter(this)
        var x = 0
        for(socialMediaSite in 0 until socialArray[schoolSelectedInt].size) {
            connectAdapter.addSectionHeaderItem(tableHeaders[x])
            linkList.add("HEADER")
            x++
            for(username in 0 until socialArray[schoolSelectedInt][socialMediaSite].size) {
                connectAdapter.addItem(socialArray[schoolSelectedInt][socialMediaSite][username])
                linkList.add(socialURLArray[schoolSelectedInt][socialMediaSite][username])
            }
        }

        listView?.adapter = connectAdapter
        listView?.setOnItemClickListener{ adapterView: AdapterView<*>?, view: View?, position: Int, l: Long ->
            val id = linkList[position]

            if((position == connectAdapter.mData.size - 1 || position == connectAdapter.mData.size - 2) && id != "HEADER") {
                openInstagram(id)
            } else if(id.first().isLetter() && id != "HEADER"){
                openTwitter(id)
            } else if(id != "HEADER") {
                openFacebook(id)
            }
        }
    }

    fun openTwitter(id : String) {
        var intent: Intent? = null
        try {
            // get the Twitter app if possible
            this.getPackageManager()?.getPackageInfo("com.twitter.android", 0)
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=$id"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) {
            // no Twitter app, revert to browser
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/$id"))
        }
        this.startActivity(intent)
    }
    fun openInstagram(id : String) {
        var intent: Intent? = null
        try {
            // get the Instagram app if possible
            this.getPackageManager()?.getPackageInfo("com.instagram.android", 0)
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("instagram://user?username=$id"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) {
            // no Instagram app, revert to browser
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/$id"))
        }
        this.startActivity(intent)
    }
    fun openFacebook(id : String) {
        var intent: Intent? = null
        try {
            // get the Facebook app if possible
            this.getPackageManager()?.getPackageInfo("com.facebook.android", 0)
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/$id"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) {
            // no Facebook app, revert to browser
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/$id"))
        }
        this.startActivity(intent)
    }

}
