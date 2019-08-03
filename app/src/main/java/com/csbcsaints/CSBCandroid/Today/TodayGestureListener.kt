package com.csbcsaints.CSBCandroid

import android.view.MotionEvent
import android.view.GestureDetector


class TodayGestureListener(val parent : TodayActivity) : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        println("singleTap")
        parent.dateButtonTapped()
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        println("doubleTap")
        parent.dateButtonDoubleTapped()
        return true
    }
}