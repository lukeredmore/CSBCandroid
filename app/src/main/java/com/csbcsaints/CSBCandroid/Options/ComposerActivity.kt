package com.csbcsaints.CSBCandroid.Options

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.csbcsaints.CSBCandroid.R
import android.widget.Toast
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.csbcsaints.CSBCandroid.ui.UserFontFamilies
import com.csbcsaints.CSBCandroid.ui.UserFontStyles
import com.csbcsaints.CSBCandroid.ui.setCustomFont
import java.io.Serializable

data class ComposerConfiguration(val submitButtonTitle : String, val placeholderText : String, val allowParagraphBreaks : Boolean) : Serializable


class ComposerActivity(context : Context, private val configuration : ComposerConfiguration, private val completion : ((String) -> Unit)? = null) : AlertDialog(context) {

    private var textView : EditText? = null
    private var submitButton : TextView? = null
    private var cancelButton : TextView? = null
    private var placeholderTextColor : Int = 0
    private var textColor : Int = 0

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_composer)
        supportActionBar?.hide()
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        textView = findViewById(R.id.textView)
        submitButton = findViewById(R.id.submitButton)
        cancelButton = findViewById(R.id.cancelButton)

        placeholderTextColor = ContextCompat.getColor(this.context, R.color.csbcGray)
        textColor = ContextCompat.getColor(this.context, R.color.csbcAlertTextColor)
        textView?.imeOptions = if (configuration!!.allowParagraphBreaks) { EditorInfo.IME_ACTION_SEND } else { EditorInfo.IME_ACTION_DONE }
        textView?.setText(configuration!!.placeholderText)
        textView?.setTextColor(placeholderTextColor)
        textView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
//        textView?.setOnClickListener {
//            if (textView?.text.toString() == configuration!!.placeholderText && textView?.currentTextColor == placeholderTextColor) {
//                textView?.setText("")
//                textView?.setTextColor(textColor)
//            }
//        }
        textView?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (!submittableText(textView?.text.toString())) {
                    textView?.setText(configuration!!.placeholderText)
                    textView?.setTextColor(placeholderTextColor)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        textView?.setOnEditorActionListener { textView, actionId, event ->
            if (!configuration.allowParagraphBreaks)
                confirmSubmission()
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> Toast.makeText(this.context, "Next", Toast.LENGTH_SHORT).show()

                EditorInfo.IME_ACTION_SEARCH -> Toast.makeText(this.context, "SEARCH", Toast.LENGTH_SHORT).show()
            }
            return@setOnEditorActionListener false
        }
        submitButton?.text = configuration.submitButtonTitle
        submitButton?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
        submitButton?.setOnClickListener {
            submit(textView?.text.toString())
        }
        cancelButton?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
        cancelButton?.setOnClickListener {
            if (!submittableText(textView?.text.toString())) { dismiss(); return@setOnClickListener
            }
            confirmCancellation()
        }
    }

    private fun submittableText(text : String) : Boolean {
        return text != "" && text != configuration.placeholderText && text != "\n"
    }
    private fun confirmSubmission() {
        if (!submittableText(textView?.text.toString())) { return }
        // setup the alert builder
        val builder = Builder(this.context)
        builder.setTitle("Ready to Submit?")

        // add the buttons
        builder.setPositiveButton("Submit") { _, _ ->
            submit(textView?.text.toString())
        }
        builder.setNegativeButton("Keep Editing", null)

        // create and show the alert dialog
        builder.create().show()
    }
    private fun submit(text : String) {
        if (!submittableText(text)) { return }
        submitButton?.isEnabled = false
        dismiss()
        completion?.invoke(text)
    }
    private fun confirmCancellation() {

        // setup the alert builder
        val builder = Builder(this.context)
        builder.setTitle("Are you sure you want to discard?")

        // add the buttons
        builder.setPositiveButton("Keep Editing", null)
        builder.setNegativeButton("Discard Changes") { _, _ ->
            dismiss()
        }

        // create and show the alert dialog
        builder.create().show()
    }

    override fun onBackPressed() {
        if (!submittableText(textView?.text.toString())) { dismiss(); return }
        confirmCancellation()

    }
}
