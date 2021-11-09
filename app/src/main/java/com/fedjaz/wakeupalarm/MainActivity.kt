package com.fedjaz.wakeupalarm

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.opengl.Visibility
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import android.view.View.*

class MainActivity : AppCompatActivity() {
    private val selectedQrs = mutableListOf<Int>()
    private val selectedAlarms = mutableListOf<Int>()
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
            QR(3, "Computer", 1, "work"),
            QR(4, "Computer", 2, "work"),
            QR(5, "Computer", 3, "work"),
            QR(6, "Computer", 4, "work"),
            QR(7, "Computer", 5, "work"),
            QR(8, "Computer", 6, "work"),
            QR(9, "Computer", 7, "work"),
            QR(10, "Computer", 8, "work"),
            QR(11, "Computer", 9, "work"),
        )

        for(qr in qrs){
            qr.createImage()
        }

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
                    enableButtons(tabs)
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
                    enableButtons(tabs)
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
                    QR.createImage()
                    qrs.add(QR)
                    (sectionsPagerAdapter.qrsFragment.view as RecyclerView).adapter?.notifyItemInserted(qrs.size - 1)
                }

                fragment.show(supportFragmentManager, "tag")
            }
        }

        sectionsPagerAdapter.qrsFragment.onItemClick = { position, QR ->
            val fragment = QrSheetFragment.newInstance(position, QR.id, QR.name, QR.location, QR.number)
            fragment.edited = {newPosition, newQR ->
                newQR.createImage()
                qrs[newPosition] = newQR
                (sectionsPagerAdapter.qrsFragment.view as RecyclerView).adapter?.notifyItemChanged(position)
            }
            fragment.show(supportFragmentManager, "tag")

        }

        sectionsPagerAdapter.qrsFragment.onItemSelected = {position, isChecked ->
            if(isChecked){
                selectedQrs.add(position)
            }
            else{
                selectedQrs.remove(position)
            }

            enableButtons(tabs)
        }

        val printButton = findViewById<ImageButton>(R.id.printButton)
        printButton.setOnClickListener {
            val qrsToPrint = mutableListOf<QR>()
            for(i in selectedQrs){
                qrsToPrint.add(qrs[i])
            }
            QR.createPdf(qrsToPrint, applicationContext.cacheDir)
        }

    }

    private fun enableButtons(tabs: TabLayout){
        val printButton = findViewById<ImageButton>(R.id.printButton)
        val deleteButton = findViewById<ImageButton>(R.id.deleteButton)
        if(tabs.selectedTabPosition == 0){
            printButton.visibility = GONE
            if(selectedAlarms.isNotEmpty()){
                deleteButton.visibility = VISIBLE
            }
            else{
                deleteButton.visibility = GONE
            }
        }
        else{
            if(selectedQrs.isNotEmpty()){
                deleteButton.visibility = VISIBLE
                printButton.visibility = VISIBLE
            }
            else{
                deleteButton.visibility = GONE
                printButton.visibility = GONE
            }
        }
    }
}