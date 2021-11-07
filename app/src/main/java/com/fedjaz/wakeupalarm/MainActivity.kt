package com.fedjaz.wakeupalarm

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val alarms = arrayListOf(
            Alarm(7, 30, true,
                arrayListOf(true, true, true, true, true, false, false)),
            Alarm(10, 0, true,
                arrayListOf(true, true, true, true, true, true, true)),
            Alarm(18, 5, true,
                arrayListOf(false, true, false, false, false, true, true)),
            Alarm(18, 5, true,
                arrayListOf(false, false, false, false, false, false, false)),
        )


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, alarms)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        val firstTab = tabs.getTabAt(0)
        val selectedIconColor = ContextCompat.getColor(applicationContext, R.color.blue_app)
        firstTab?.icon?.setColorFilter(selectedIconColor, PorterDuff.Mode.SRC_IN)

        tabs.addOnTabSelectedListener(
            object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    super.onTabSelected(tab)
                    val iconColor = ContextCompat.getColor(applicationContext, R.color.blue_app)
                    tab.icon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    val iconColor = ContextCompat.getColor(applicationContext, R.color.black)
                    tab.icon?.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
                }
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

        viewPager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener{
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {}

                override fun onPageSelected(position: Int) {
                    tabs.getTabAt(position)?.select()
                }

                override fun onPageScrollStateChanged(state: Int) {}
            }
        )

    }
}