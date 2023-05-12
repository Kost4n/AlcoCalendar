package com.kost4n.alcocalendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import butterknife.ButterKnife
import com.github.tibolte.agendacalendarview.AgendaCalendarView
import com.github.tibolte.agendacalendarview.CalendarManager
import com.github.tibolte.agendacalendarview.CalendarPickerController
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent
import com.github.tibolte.agendacalendarview.models.CalendarEvent
import com.github.tibolte.agendacalendarview.models.DayItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity(), CalendarPickerController /**, OnNavigationButtonClickedListener*/ {

    private lateinit var mAgendaCalendarView: AgendaCalendarView
    private val eventList: MutableList<CalendarEvent> = ArrayList()
    private val recordDao by lazy {
        MyDatabase.getMyDatabase(applicationContext).getRecordDao()
    }


    private lateinit var myDayItem: DayItem

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        mAgendaCalendarView = findViewById<AgendaCalendarView>(R.id.agenda_calendar_view)
        mAgendaCalendarView.setBackgroundColor(android.R.color.holo_orange_light)
        ButterKnife.bind(this)

        val minDate = Calendar.getInstance()
        val maxDate = Calendar.getInstance()

        minDate.add(Calendar.MONTH, -2)
        minDate[Calendar.DAY_OF_MONTH] = 1
        maxDate.add(Calendar.YEAR, 1)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            showDialog()
        }

//        mockList(eventList)
        getRecords(eventList)


        val calendarManager = CalendarManager.getInstance(applicationContext)
        calendarManager.buildCal(minDate, maxDate, Locale.getDefault())
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this)
        mAgendaCalendarView.addEventRenderer(DrawableEventRenderer())

    }

    private fun addAlcoEvent(dayItem: DayItem, degree: String, drink: String) {
        val day = dayItem.date.date
        val month = convertMonth(dayItem.month)
        Log.i("DAY", "--------------------------$day-----------------------")
        val startTime = Calendar.getInstance()
        val endTime = Calendar.getInstance()
        startTime[Calendar.HOUR_OF_DAY] = 14
        startTime[Calendar.MINUTE] = 0
        startTime[Calendar.DAY_OF_MONTH] = day
        startTime[Calendar.MONTH] = month
        endTime[Calendar.HOUR_OF_DAY] = 15
        endTime[Calendar.MINUTE] = 0
        endTime[Calendar.DAY_OF_MONTH] = day
        endTime[Calendar.MONTH] = month
        var color = 0
        when (degree) {
            "жёстко нахуярился(ась)" -> {
                color = android.R.color.holo_red_light
            }
            "ну так расслабился(ась)" -> {
                color = android.R.color.holo_green_light
            }
            "нормально посидел(а)" -> {
                color = android.R.color.holo_orange_light
            }
        }
        val event = BaseCalendarEvent(
            "Я пил(а) $drink", "fff", "Абакан",
            ContextCompat.getColor(this, color), startTime, endTime, true
        )
        eventList.add(event)
        val minDate = Calendar.getInstance()
        val maxDate = Calendar.getInstance()

        minDate.add(Calendar.MONTH, -2)
        minDate[Calendar.DAY_OF_MONTH] = 1
        maxDate.add(Calendar.YEAR, 1)

        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this)

        lifecycleScope.launch(Dispatchers.IO) {
            val record = Record(day, month, drink, degree)
            recordDao.insert(record)
        }
    }

    private fun convertMonth(month: String?): Int {
        var mm = 0
        when(month) {
            "января" -> mm = 0
            "февраля" -> mm = 1
            "марта" -> mm = 2
            "апреля" -> mm = 3
            "мая" -> mm = 4
            "июня" -> mm = 5
            "июля" -> mm = 6
            "августа" -> mm = 7
            "сентября" -> mm = 8
            "октября" -> mm = 9
            "ноября" -> mm = 10
            "декабря" -> mm = 11
        }

        return mm
    }

    private fun getRecords(eventList: MutableList<CalendarEvent>) {
        lifecycleScope.launch(Dispatchers.IO) {
            val records = recordDao.getRecords()
            for (rec in records) {
                val startTime = Calendar.getInstance()
                val endTime = Calendar.getInstance()
                startTime[Calendar.DAY_OF_MONTH] = rec.day
                startTime[Calendar.MONTH] = rec.month
                startTime[Calendar.HOUR_OF_DAY] = 14
                startTime[Calendar.MINUTE] = 0
                endTime[Calendar.HOUR_OF_DAY] = 15
                endTime[Calendar.MINUTE] = 0
                endTime[Calendar.DAY_OF_MONTH] = rec.day
                endTime[Calendar.MONTH] = rec.month

                var color = 0
                when (rec.degree) {
                    "жёстко нахуярился(ась)" -> {
                        color = android.R.color.holo_red_light
                    }
                    "ну так расслабился(ась)" -> {
                        color = android.R.color.holo_green_light
                    }
                    "нормально посидел(а)" -> {
                        color = android.R.color.holo_orange_light
                    }
                }

                val event = BaseCalendarEvent(
                    "Я пил(а) ${rec.drink}", "fff", "Абакан",
                    ContextCompat.getColor(applicationContext, color), startTime, endTime, true
                )

                eventList.add(event)
            }
        }
    }

    override fun onDaySelected(dayItem: DayItem?) {
        Log.d("CalendarController", java.lang.String.format("Selected day: %s", dayItem))
        dayItem?.let {
            myDayItem = it
        }

    }

    override fun onEventSelected(event: CalendarEvent?) {
        Log.d("CalendarController", String.format("Selected event: %s", event))
    }

    override fun onScrollToDate(calendar: Calendar) {
        if (supportActionBar != null) {
            supportActionBar!!.title = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        }
    }
    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.add_drinking_date_dialog, null)
        builder.setView(view)
        val writeAlco = view.findViewById<EditText>(R.id.write_alco)
        val spinner = view.findViewById<Spinner>(R.id.spinner_degree)
        val myItems = resources.getStringArray(R.array.drinking_degree)
        val aa = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, myItems)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = aa
        spinner.setSelection(0)

        var degree = spinner.selectedItem.toString()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                degree = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        builder.setCancelable(false)
            .setNegativeButton("Отмена") { dialogInterface, _ ->
                dialogInterface?.cancel()
            }
            .setPositiveButton("Я тогда бухал") { _, _ ->

                myDayItem?.let {
                    addAlcoEvent(it, degree, writeAlco.text.toString())
                }
            }

        builder.show()
    }
}

