package com.fedjaz.wakeupalarm

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, private val alarms: ArrayList<Alarm>, private val qrs: ArrayList<QR>) : FragmentPagerAdapter(fm) {
    var alarmsFragment: AlarmInfoFragment = AlarmInfoFragment.newInstance(1, alarms)
    var qrsFragment: QRFragment = QRFragment.newInstance(qrs, false)

    override fun getItem(position: Int): Fragment {
        return if(position == 0){
            alarmsFragment
        } else{
            qrsFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}