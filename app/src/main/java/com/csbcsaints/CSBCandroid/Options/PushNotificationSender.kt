package com.csbcsaints.CSBCandroid.Options

import com.csbcsaints.CSBCandroid.ui.createWithParameters
import okhttp3.*
import java.io.IOException

/// Takes a given notification and publishes it with preconfigured settings and reports to a delegate (the composer)
class PushNotificationSender {

    fun send(message : String, school : Int, completion: ((String?) -> Unit)? = null) {
        val url = "https://us-east4-csbcprod.cloudfunctions.net/sendMessageFromAdmin"
        val params : Map<String, String> = mapOf(
            "message" to message,
            "schoolInt" to "$school"
        )

        val request = Request.Builder().createWithParameters(url, params)
        if (request == null) {
            print("Invalid URLRequest")
            completion?.invoke("Invalid URLRequest")
            return
        }
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to Publish Notification: ")
                println(e)
                completion?.invoke(e.localizedMessage)
            }
            override fun onResponse(call: Call, response: Response) {
                println("Successfully sent notification")
                completion?.invoke(null)
            }
        })
    }
}