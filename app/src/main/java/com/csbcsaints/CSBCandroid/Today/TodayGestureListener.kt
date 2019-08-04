package com.csbcsaints.CSBCandroid

import android.view.MotionEvent
import android.view.GestureDetector
import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter


class TodayGestureListener(val parent : TodayActivity) : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        DeveloperPrinter().print("singleTap")
        parent.dateButtonTapped()
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        DeveloperPrinter().print("doubleTap")
        parent.dateButtonDoubleTapped()
        return true
    }
}