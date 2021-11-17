package com.fedjaz.wakeupalarm

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.fedjaz.wakeupalarm.db.DataAccessLayer
import com.google.android.material.tabs.TabLayout
import java.io.File

class MainActivity : AppCompatActivity() {
    private val selectedQrs = mutableListOf<Int>()
    private val selectedAlarms = mutableListOf<Int>()
    var sectionsPagerAdapter: SectionsPagerAdapter?= null
    var dataAccessLayer: DataAccessLayer? = null

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

    val activityLauncher = registerForActivityResult(CreateAlarmContract()) { alarm ->
        if(alarm?.id == 0){
            addNewAlarmFromActivity(alarm)
        }
        else{
            1 + 1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataAccessLayer = DataAccessLayer(filesDir.absolutePath + "/database.db")

        for(qr in qrs){
            qr.createImage()
        }

        sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, alarms, qrs)
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
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                    ) {
                    }

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
                    (sectionsPagerAdapter?.qrsFragment?.view as RecyclerView).adapter?.notifyItemInserted(qrs.size - 1)
                }

                fragment.show(supportFragmentManager, "tag")
            }
            else{
                activityLauncher.launch(Pair(qrs, null))
            }
        }

        sectionsPagerAdapter?.qrsFragment?.onItemClick = { position, QR ->
            val fragment = QrSheetFragment.newInstance(position, QR.id, QR.name, QR.location, QR.number)
            fragment.edited = { newPosition, newQR ->
                newQR.createImage()
                qrs[newPosition] = newQR
                (sectionsPagerAdapter?.qrsFragment?.view as RecyclerView).adapter?.notifyItemChanged(position)
            }
            fragment.show(supportFragmentManager, "tag")

        }

        sectionsPagerAdapter?.qrsFragment?.onItemSelected = { position, isChecked ->
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
            val file = QR.createPdf(qrsToPrint, applicationContext.cacheDir)
            val newFile = File(applicationContext.cacheDir, "/pdfs/QRs.pdf")
            if(newFile.exists()){
                newFile.delete()
            }
            file.copyTo(newFile)
            val fileUri = FileProvider.getUriForFile(applicationContext, applicationContext.packageName + ".provider", newFile)
            grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "application/pdf"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Save pdf"))
        }
    }

    private fun addNewAlarmFromActivity(alarm: Alarm){
        alarm.id = alarms.size + 1
        alarms.add(alarm)
        (sectionsPagerAdapter?.alarmsFragment?.view as RecyclerView).adapter?.notifyItemInserted(alarms.size - 1)
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