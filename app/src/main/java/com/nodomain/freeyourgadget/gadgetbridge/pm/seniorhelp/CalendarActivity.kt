package com.nodomain.freeyourgadget.gadgetbridge.pm.seniorhelp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.nodomain.freeyourgadget.gadgetbridge.R
import kotlinx.android.synthetic.main.activity_calendar.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : Activity() {
    private val TAB = "CalendarActivity"



    private val dateFormat = SimpleDateFormat("MMMM- yyyy", Locale.getDefault())

    val database = FirebaseDatabase.getInstance()

    val myRef = database.getReference("/events")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        btnAddEvent.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))



        }
        var newString = "OLÁ"
        val extras = intent.extras
        if (extras == null) {
            newString = ""
        } else {
          val newString = extras.getStringArray("ev")
            //addEvent(newString[0],newString[1],newString[2].toLong())
            myRef.push().setValue(System.currentTimeMillis().toString() +"="+ newString[0]+"="+newString[1]+"="+newString[2]+"="+newString[3])
        }
        getGreeting()

        val ev1 = Event(Color.RED, 1527688591000L, "Teste event day")

        compactcalendar_view.addEvent(ev1)

        compactcalendar_view.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                val context = applicationContext

                val data = dateClicked.toString()
                Log.d(TAB, "DATA: $data")
                System.out.println("1- "+dateClicked.toString())
                System.out.println("2- "+dateClicked)

                val events = compactcalendar_view.getEvents(dateClicked)

                if(!events.isEmpty())
                {
                for (i in events )
                {
                    Toast.makeText(context,i.data.toString(), Toast.LENGTH_LONG).show()
                }
                }
                else
                {
                    Toast.makeText(context, "No Event", Toast.LENGTH_LONG).show()
                }

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                actionBar.title = dateFormat.format(firstDayOfNewMonth)

            }
        })



        }



    fun addEvent(text:String, color:String, time:Long )
    {

        if(color.equals("Vermelho"))
        {
            val ev1 = Event(Color.RED, time, text)

            compactcalendar_view.addEvent(ev1)
        }
        if(color.equals("Verde"))
        {
            val ev1 = Event(Color.GREEN, time, text)
            compactcalendar_view.addEvent(ev1)
        }
        if(color.equals("Azul"))
        {
            val ev1 = Event(Color.BLUE, time, text)

            compactcalendar_view.addEvent(ev1)
        }
        if(color.equals("Cinzento"))
        {
            val ev1 = Event(Color.GRAY, time, text)
            compactcalendar_view.addEvent(ev1)
        }
        if(color.equals("Preto"))
        {
            val ev1 = Event(Color.BLACK, time, text)
            compactcalendar_view.addEvent(ev1)
        }


    }

    fun getGreeting(){
        println("--------- start greeting ----------")
        val gson = Gson()
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

               //val objectList = gson.fromJson( snapshot.value.toString(), Array<String>::class.java).asList()
                val separate1 = snapshot.value.toString().split(",".toRegex())

                //var i = 0
                var y = 0
                for (item in separate1) {
                    //println("TestBed: " + item)
                    val separete2 = item.split("=".toRegex())


                    for (i in 1 until separete2.size-1 step 4 ) {
                        var d= separete2[i+3].replace("}","")

                        addEvent(separete2[i+1],separete2[i+2],d.toLong())
                        //System.out.println(separete2[i]+"-"+separete2[i+1]+"-"+separete2[i+2])
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }



}




