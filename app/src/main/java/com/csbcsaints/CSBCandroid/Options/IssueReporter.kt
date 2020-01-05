package com.csbcsaints.CSBCandroid.Options

import com.csbcsaints.CSBCandroid.BuildConfig
import com.csbcsaints.CSBCandroid.ui.createWithParameters
import com.csbcsaints.CSBCandroid.ui.dateString
import okhttp3.*
import java.io.IOException
import java.util.*

class IssueReporter {

    fun report(message : String, completion: ((String?) -> Unit)? = null) {
        val url = "https://us-east4-csbcprod.cloudfunctions.net/sendReportEmail"
        val params : Map<String, String> = mapOf(
        "body" to "<i>App version: Android-${BuildConfig.VERSION_NAME}</i>\n<hr>\n$message",
        "sender" to "CSBC App Issue",
        "subject" to "New App Issue: ${Date().dateString()}"
        )

        val request = Request.Builder().createWithParameters(url, params)
        if (request == null) {
            print("Invalid URLRequest")
            completion?.invoke("Invalid URLRequest")
            return
        }
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to Submit Issue: ")
                println(e)
                completion?.invoke(e.localizedMessage)
            }
            override fun onResponse(call: Call, response: Response) {
                println("success")
                completion?.invoke(null)
            }
        })
    }
}