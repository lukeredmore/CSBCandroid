package com.csbcsaints.CSBCandroid.ui.main

import android.content.Context
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.csbcsaints.CSBCandroid.*

//private val TAB_TITLES = arrayOf("Seton", "St. John's", "All Saints", "St. James")


//class SchoolSelectorAdapter(private val context: Context, fm: FragmentManager, activityTag: Int, eventsArray: Array<Parcelable>?, athleticsArray: Array<Parcelable>?) : FragmentPagerAdapter(fm) {
//
//    val activityDictionary = mapOf(1 to TodayActivity(), 2 to PortalActivity(), 3 to ContactActivity(), 6 to LunchActivity(), 9 to ConnectActivity(), 10 to DressCodeActivity(), 11 to DocsActivity())
//    val activityTag = activityTag
//    val eventsArray = eventsArray
//    val athleticsArray = athleticsArray
//
//    override fun getItem(position: Int): Fragment {
//        // getItem is called to instantiate the fragment for the given page.
//        // Return a PlaceholderFragment (defined as a static inner class below).
//        when(activityTag) {
//            //1 -> return TodayActivity.newInstance(position + 1, athleticsArray, eventsArray)
//            2 -> return PortalActivity.newInstance(position + 1)
//            3 -> return ContactActivity.newInstance(position + 1)
//            6 -> return LunchActivity.newInstance(position + 1)
//            9 -> return ConnectActivity.newInstance(position + 1)
//            10 -> return DressCodeActivity.newInstance(position + 1)
//            11 -> return DocsActivity.newInstance(position + 1)
//            else -> return ContactActivity.newInstance(position + 1)
//        }
//
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        return TAB_TITLES[position]
//    }
//
//    override fun getCount(): Int {
//        // Show 2 total pages.
//        return 4
//    }
//}