package com.fedjaz.wakeupalarm

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.orm.SugarContext
import java.io.File


class MainActivity : AppCompatActivity() {
    private var selectedQrs = arrayListOf<Int>()
    private var selectedAlarms = arrayListOf<Int>()
    var sectionsPagerAdapter: SectionsPagerAdapter?= null
    var dataAccessLayer: DataAccessLayer? = null

    private var alarms = arrayListOf<Alarm>()

    private var qrs = arrayListOf<QR>()

    private val activityLauncher = registerForActivityResult(CreateAlarmContract()) { alarm ->
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

        SugarContext.init(this)
        dataAccessLayer = DataAccessLayer()

        qrs = dataAccessLayer!!.getAllQrs()
        alarms = dataAccessLayer!!.getAllAlarms()

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
                    QR.id = dataAccessLayer!!.createQr(QR)
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
                dataAccessLayer!!.editQr(newQR)
                qrs[newPosition] = newQR
                (sectionsPagerAdapter?.qrsFragment?.view as RecyclerView).adapter?.notifyItemChanged(position)
            }
            fragment.show(supportFragmentManager, "tag")

        }

        sectionsPagerAdapter?.qrsFragment?.onItemSelected = { position, isChecked ->
            if(isChecked){
                selectedQrs.add(qrs[position].id)
            }
            else{
                selectedQrs.remove(qrs[position].id)
            }

            enableButtons(tabs)
        }

        sectionsPagerAdapter?.alarmsFragment?.onItemClick = {position, alarm ->
            activityLauncher.launch(Pair(qrs, alarm))
        }

        val printButton = findViewById<ImageButton>(R.id.printButton)
        printButton.setOnClickListener {
            val qrsToPrint = mutableListOf<QR>()
            for(qr in qrs){
                if(qr.id in selectedQrs){
                    qrsToPrint.add(qr)
                }
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

        val deleteButton = findViewById<ImageButton>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            if(tabs.selectedTabPosition == 0){
                TODO()
            }
            else{
                val qrsToDelete = arrayListOf<QR>()
                for(qr in qrs){
                    if(qr.id in selectedQrs){
                        for(alarm in alarms){
                            alarm.qrIds.remove(qr.id)
                        }
                        dataAccessLayer!!.deleteQR(qr)
                        qrsToDelete.add(qr)
                    }
                }
                for(qr in qrsToDelete){
                    qrs.remove(qr)
                }
                (sectionsPagerAdapter?.qrsFragment?.view as RecyclerView).adapter?.notifyDataSetChanged()
                selectedQrs = arrayListOf<Int>()
                enableButtons(tabs)
            }
        }
    }

    private fun addNewAlarmFromActivity(alarm: Alarm){
        if(alarm.id == 0){
            alarm.id = dataAccessLayer!!.createAlarm(alarm)
            alarms.add(alarm)
            (sectionsPagerAdapter?.alarmsFragment?.view as RecyclerView).adapter?.notifyItemInserted(alarms.size - 1)
        }
        else{
            dataAccessLayer!!.editAlarm(alarm)
            (sectionsPagerAdapter?.alarmsFragment?.view as RecyclerView).adapter?.notifyDataSetChanged()
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