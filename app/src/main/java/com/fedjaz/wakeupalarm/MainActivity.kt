package com.fedjaz.wakeupalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.orm.SugarContext
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    private var selectedQrs = arrayListOf<Int>()
    private var selectedAlarms = arrayListOf<Int>()
    var sectionsPagerAdapter: SectionsPagerAdapter?= null
    var dataAccessLayer: DataAccessLayer? = null

    private var alarms = arrayListOf<Alarm>()

    private var qrs = arrayListOf<QR>()

    private val activityLauncher = registerForActivityResult(CreateAlarmContract()) { alarm ->
        if(alarm != null){
            addNewAlarmFromActivity(alarm)
        }

    }

    private val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val alarmId = intent?.extras?.getInt("alarmId", -1)
            if(alarmId != -1){
                for(alarm in alarms){
                    if(alarm.id == alarmId){
                        alarm.enabled = false
                        break
                    }
                }
                (sectionsPagerAdapter?.alarmsFragment?.view as RecyclerView).adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intentFilter = IntentFilter()
        intentFilter.addAction("com.fedjaz.wakeupalarm.MainActivity.action")
        registerReceiver(broadcastReceiver, intentFilter)

        dataAccessLayer = DataAccessLayer(this)

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
                fragment.created = { qr ->
                    qr.id = dataAccessLayer!!.createQr(qr)
                    qr.createImage()
                    dataAccessLayer!!.editQr(qr)
                    qrs.add(qr)
                    (sectionsPagerAdapter?.qrsFragment?.view as RecyclerView).adapter?.notifyItemInserted(qrs.size - 1)
                }

                fragment.show(supportFragmentManager, "tag")
            }
            else{
                if(qrs.size == 0){
                    Toast.makeText(this, "You should first add some QR codes in the second tab", Toast.LENGTH_LONG).show()
                }
                else{
                    activityLauncher.launch(Pair(qrs, null))
                }
            }
        }

        sectionsPagerAdapter?.qrsFragment?.onItemClick = { position, QR ->
            val fragment = QrSheetFragment.newInstance(QR)
            fragment.edited = { newQR ->
                newQR.createImage()
                dataAccessLayer!!.editQr(newQR)
                for(i in 0 until qrs.size){
                    if(qrs[i].id == newQR.id){
                        qrs[i] = newQR
                    }
                }
                (sectionsPagerAdapter?.qrsFragment?.view as RecyclerView).adapter?.notifyItemChanged(position)
            }
            fragment.show(supportFragmentManager, "tag")
        }

        sectionsPagerAdapter?.qrsFragment?.onItemSelected = { id, isChecked ->
            if(isChecked){
                selectedQrs.add(id)
            }
            else{
                selectedQrs.remove(id)
            }

            enableButtons(tabs)
        }

        sectionsPagerAdapter?.alarmsFragment?.onItemClick = {_, alarm ->
            val scheduler = AlarmScheduler(this)
            scheduler.cancel(alarm)
            activityLauncher.launch(Pair(qrs, alarm))
        }

        sectionsPagerAdapter?.alarmsFragment?.onItemEnabled = { position, enabled ->
            val scheduler = AlarmScheduler(this)
            if(enabled){
                val time = scheduler.schedule(alarms[position])
                displayTime(time)
            }
            else{
                scheduler.cancel(alarms[position])
            }

            alarms[position].enabled = enabled
            dataAccessLayer!!.enableAlarm(alarms[position])
        }

        sectionsPagerAdapter?.alarmsFragment?.onItemSelected = {id, checked ->
            if(checked){
                selectedAlarms.add(id)
            }
            else{
                selectedAlarms.remove(id)
            }
            enableButtons(tabs)
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
                val alarmsToDelete = arrayListOf<Alarm>()
                val scheduler = AlarmScheduler(this)
                for(alarm in alarms){
                    if(alarm.id in selectedAlarms){
                        scheduler.cancel(alarm)
                        alarmsToDelete.add(alarm)
                        dataAccessLayer!!.deleteAlarm(alarm)
                    }
                }
                for(alarm in alarmsToDelete){
                    alarms.remove(alarm)
                }
                (sectionsPagerAdapter?.alarmsFragment?.view as RecyclerView).adapter?.notifyDataSetChanged()
                selectedAlarms = arrayListOf<Int>()
                enableButtons(tabs)
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
        val scheduler = AlarmScheduler(this)
        if(alarm.id == -1){
            alarm.id = dataAccessLayer!!.createAlarm(alarm)
            alarms.add(alarm)
            val time = scheduler.schedule(alarm)
            displayTime(time)
            (sectionsPagerAdapter?.alarmsFragment?.view as RecyclerView).adapter?.notifyItemInserted(alarms.size - 1)
        }
        else{
            dataAccessLayer!!.editAlarm(alarm)
            for(i in 0 until alarms.size){
                if(alarms[i].id == alarm.id){
                    alarms[i] = alarm
                }
            }
            if(alarm.enabled){
                val time = scheduler.schedule(alarm)
                displayTime(time)
            }
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

    private fun displayTime(timeDiff: Long){
        val days: Int = (timeDiff / (24 * 60 * 60 * 1000)).toInt()
        val hours = timeDiff % (24 * 60 * 60 * 1000) / (60 * 60 * 1000)
        val minutes = timeDiff % (60 * 60 * 1000) / (60 * 1000)

        val displayData = arrayListOf<String>()
        if(days > 0){
            displayData.add("$days d")
        }
        if(hours > 0){
            displayData.add("$hours h")
        }
        if(minutes > 0){
            displayData.add("$minutes m")
        }

        var displayString = "The alarm will trigger in "
        if(displayData.size == 0){
            displayString += "less than a minute"
        }
        else{
            for(i in 0 until displayData.size){
                displayString += if(displayData.size - i > 2){
                    displayData[i] + ", "
                } else if(displayData.size - i > 1){
                    displayData[i] + " and "
                } else{
                    displayData[i]
                }
            }
        }
        Toast.makeText(applicationContext, displayString, Toast.LENGTH_SHORT).show()
    }
}