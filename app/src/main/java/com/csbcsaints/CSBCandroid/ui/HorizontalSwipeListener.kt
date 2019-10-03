package com.csbcsaints.CSBCandroid.ui

import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


abstract class HorizontalSwipeListener: View.OnTouchListener {

    private val minDistance: Int = 200
    private var firstX: Float = 0.toFloat()

    internal abstract fun onSwipeRight()

    internal abstract fun onSwipeLeft()

    override fun onTouch(view: View, event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                firstX = event.x
                return true
            }
            MotionEvent.ACTION_UP -> {
                val secondX = event.x
                if (abs(secondX - firstX) > minDistance) {
                    if (secondX > firstX) {
                        onSwipeLeft()
                    } else {
                        onSwipeRight()
                    }
                }
                return true
            }
        }
        return view.performClick()
    }

}