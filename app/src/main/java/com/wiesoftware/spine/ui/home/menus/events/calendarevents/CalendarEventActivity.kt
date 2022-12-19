package com.wiesoftware.spine.ui.home.menus.events.calendarevents

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.reponses.EventsRecord
import com.wiesoftware.spine.databinding.ActivityCalendarEventBinding
import com.wiesoftware.spine.ui.home.menus.events.EVE_RECORD
import kotlinx.android.synthetic.main.activity_calendar_event.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import java.lang.Exception
import java.util.*


class CalendarEventActivity : AppCompatActivity(),KodeinAware, CalendarEventsEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    lateinit var binding: ActivityCalendarEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_calendar_event)
        val viewmodel=ViewModelProvider(this).get(CaklendarEventViewModel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.calendarEventsEventListener=this


        setCalData()

    }

    private fun setCalData() {
        val record=intent.getSerializableExtra(EVE_RECORD) as EventsRecord
        val eveStartDate=record.startDate
        val start_time=record.startTime
        val eveEndDate=record.endDate
        val end_time=record.endTime
        try {
            val sd= eveStartDate.split("-")
            val st=start_time.split(":")
            val  sy=sd.get(0).toInt(); val sm=sd.get(1).toInt();  val sdd=sd.get(2).toInt(); val sh=st.get(0).toInt();val smm=st.get(1).toInt()

            val ed= eveEndDate.split("-")
            val et=end_time.split(":")
            val  ey=ed.get(0).toInt(); val em=ed.get(1).toInt();  val edd=ed.get(2).toInt(); val eh=et.get(0).toInt();val emm=et.get(1).toInt()

            val startMillis: Long = Calendar.getInstance().run {
                set(sy, (sm-1), sdd, sh, smm)
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(ey, (em-1), edd, eh, emm)
                timeInMillis
            }
        calendarView.date = startMillis
        calendarView.minDate = startMillis
        calendarView.maxDate = endMillis

    }catch (e: Exception){
    e.printStackTrace()
    }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onSaveEvents() {
        val record=intent.getSerializableExtra(EVE_RECORD) as EventsRecord
        val eveStartDate=record.startDate
        val start_time=record.startTime
        val eveEndDate=record.endDate
        val end_time=record.endTime
        val desc=record.description
        val location=record.location
        try {
            val sd= eveStartDate.split("-")
            val st=start_time.split(":")
            val  sy=sd.get(0).toInt(); val sm=sd.get(1).toInt();  val sdd=sd.get(2).toInt(); val sh=st.get(0).toInt();val smm=st.get(1).toInt()

            val ed= eveEndDate.split("-")
            val et=end_time.split(":")
            val  ey=ed.get(0).toInt(); val em=ed.get(1).toInt();  val edd=ed.get(2).toInt(); val eh=et.get(0).toInt();val emm=et.get(1).toInt()


            val startMillis: Long = Calendar.getInstance().run {
                set(sy, (sm-1), sdd, sh, smm)
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(ey, (em-1), edd, eh, emm)
                timeInMillis
            }
            val calendarEvent: Calendar = Calendar.getInstance()
            val intent = Intent(Intent.ACTION_INSERT)
            intent.type = "vnd.android.cursor.item/event"
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            intent.putExtra("allDay", true)
            intent.putExtra("rule", "FREQ=YEARLY")
            intent.putExtra(CalendarContract.Events.DESCRIPTION, desc)
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            intent.putExtra(CalendarContract.Events.TITLE, title)
            startActivity(intent)
        }catch (e: Exception){
            e.printStackTrace()
        }

    }
}