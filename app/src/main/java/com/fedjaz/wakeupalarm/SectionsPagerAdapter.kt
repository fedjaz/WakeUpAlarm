package com.fedjaz.wakeupalarm

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, private val alarms: ArrayList<Alarm>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if(position == 1){
            AlarmInfoFragment.newInstance(1, alarms)
        } else{
            AlarmInfoFragment.newInstance(1, alarms)
        }
    }

    override fun getCount(): Int {
        return 2
    }
}