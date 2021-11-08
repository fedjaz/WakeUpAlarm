package com.fedjaz.wakeupalarm

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val alarms = arrayListOf(
            Alarm(1, 7, 30, true,
                arrayListOf(true, true, true, true, true, false, false)),
            Alarm(2, 10, 0, true,
                arrayListOf(true, true, true, true, true, true, true)),
            Alarm(3, 18, 5, true,
                arrayListOf(false, true, false, false, false, true, true)),
            Alarm(4, 18, 5, true,
                arrayListOf(false, false, false, false, false, false, false)),
        )

        val qrs = arrayListOf(
            QR(1, "Bathroom", 1, "home"),
            QR(2, "Kitchen", 1, "home"),
            QR(3, "Computer", 1, "work")
        )


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, alarms, qrs)
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

        val addButton = findViewById<ImageButton>(R.id.addButton)

        addButton.setOnClickListener {
            if(tabs.selectedTabPosition == 1){
                val fragment = QrSheetFragment.newInstance()
                fragment.created = { QR ->
                    QR.id = qrs.size
                    qrs.add(QR)
                    (sectionsPagerAdapter.qrsFragment.view as RecyclerView).adapter?.notifyItemInserted(qrs.size - 1)
                }

                fragment.show(supportFragmentManager, "tag")
            }
        }

        sectionsPagerAdapter.qrsFragment.onItemClick = { position, QR ->
            val fragment = QrSheetFragment.newInstance(position, QR.id, QR.name, QR.location, QR.number)
            fragment.edited = {newPosition, newQR ->
                qrs[newPosition] = newQR
                (sectionsPagerAdapter.qrsFragment.view as RecyclerView).adapter?.notifyItemChanged(position)
            }
            fragment.show(supportFragmentManager, "tag")

        }


    }
}