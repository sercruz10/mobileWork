package pm.seniorhelp

import android.app.ActionBar
import android.app.Activity
import android.app.usage.UsageEvents
import android.widget.Toast
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import kotlinx.android.synthetic.main.activity_calendar.*
import java.text.SimpleDateFormat
import java.util.*
import com.github.sundeepk.compactcalendarview.domain.Event






class CalendarActivity : Activity() {
    private val TAB = "CalendarActivity"

    private val dateFormat = SimpleDateFormat("MMMM- yyyy", Locale.getDefault())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        btnAddEvent.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))


        }


        val ev1 = Event(Color.RED, 1527688591000L, "Teste event day")

        compactcalendar_view.addEvent(ev1)

        compactcalendar_view.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                val context = applicationContext

                val data = dateClicked.toString()
                Log.d(TAB, "DATA: $data")

                if (dateClicked.toString().compareTo("Fri Apr 27 00:00:00 GMT+01:00 2018") === 0) {
                    Toast.makeText(context, "Teste event day", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "No Event", Toast.LENGTH_LONG).show()
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                actionBar.title = dateFormat.format(firstDayOfNewMonth)

            }
        })


        }
}




