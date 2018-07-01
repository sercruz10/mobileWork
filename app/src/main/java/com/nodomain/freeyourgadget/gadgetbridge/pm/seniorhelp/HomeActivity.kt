package com.nodomain.freeyourgadget.gadgetbridge.pm.seniorhelp


import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.nodomain.freeyourgadget.gadgetbridge.R
import com.nodomain.freeyourgadget.gadgetbridge.activities.ControlCenterv2
import com.nodomain.freeyourgadget.gadgetbridge.pm.objects.ConsumeApi
import kotlinx.android.synthetic.main.activity_home.*
import okhttp3.*
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*


class HomeActivity : Activity() {

    private val APP_ID = "22bf8738d265b443463933882f83cfbd"
    var contador = 0

    var coorLat = 9.99
    var coordLong = 9.99

    var localDateNow = Calendar.getInstance().time
    val database = FirebaseDatabase.getInstance()
    private var locationManager : LocationManager? = null

    val client = OkHttpClient()

    private val perms = arrayOf(
            ACCESS_FINE_LOCATION,
            WRITE_EXTERNAL_STORAGE,
            CALL_PHONE)

    private fun getPermissions() {
        if (!EasyPermissions.hasPermissions(this, ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, "", 0,
                    ACCESS_FINE_LOCATION)
        }
        if (!EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "", 0,
                   WRITE_EXTERNAL_STORAGE)
        }
        if (!EasyPermissions.hasPermissions(this,CALL_PHONE)) {
            EasyPermissions.requestPermissions(this, "", 0,
                    CALL_PHONE)
        }
        if (!EasyPermissions.hasPermissions(this, ACCESS_COARSE_LOCATION)) {
            EasyPermissions.requestPermissions(this, "", 0,
                    ACCESS_COARSE_LOCATION)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val yourmilliseconds = System.currentTimeMillis()


        getPermissions()

        imageWallet.setOnClickListener {
            val intent = Intent(this, FinanceActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("DateTime", localDateNow)
            startActivity(intent)
        }
        imageGear.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        imageCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        imageHealth.setOnClickListener {
            startActivity(Intent(this, ControlCenterv2::class.java))
        }
        imageMapa.setOnClickListener {

            val gmmIntentUri = Uri.parse("geo:"+coorLat.toString()+","+coordLong.toString())
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            }
        }

        getGreetingNumber("/number1",call1,textPhone1)
        getGreetingNumber("/number2",call2,textPhone2)
        getGreetingNumber("/number3",call3,textPhone3)
        personalInfo()
        temp()

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            123 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Log.d("TAG", "Call Permission Not Granted")
            }

            else -> {
            }
        }
    }
    fun callSomeone(telemovel: String) {
        val permissionCheck = ContextCompat.checkSelfPermission(this, CALL_PHONE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(CALL_PHONE),
                    123)
        } else {
            startActivity(Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+telemovel)))
        }
    }

    fun getGreetingNumber(str:String, btn: LinearLayout, txt:TextView){
        val myRefNumber1 = database.getReference(str)
        myRefNumber1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val separate1 = snapshot.value.toString().split(",".toRegex())
                for (item in separate1) {
                    //println("TestBed: " + item)
                    val separete2 = item.split("=".toRegex())
                    txt.text = separete2[1]
                    btn.setOnClickListener {
                        callSomeone(separete2[2])
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun personalInfo(){
        val myRefNumber1 = database.getReference("/personalInfo")

        myRefNumber1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val separate1 = snapshot.value.toString().split(",".toRegex())
                for (item in separate1) {
                    //println("TestBed: " + item)
                    val separete2 = item.split("=".toRegex())
                    lblPersonalInfo.text = "  Welcome"+", "+ separete2[1]

                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })


    }


    fun temp() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
            try {
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            } catch(ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available")
            }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            coorLat = location.latitude.toDouble()
            coordLong = location.longitude.toDouble()

            val URL = "http://api.openweathermap.org/data/2.5/weather?lat="+location.latitude+"&lon="+location.longitude
            val COMPLETE_URL = "$URL&APPID=$APP_ID"
            if(contador == 0) {
                execute(COMPLETE_URL)
                System.out.println("merda" + location.longitude + ":" + location.latitude)
            }



        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun execute(str: String) {
        contador = 1
        val url = str
        run(url)
    }

    fun run(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val te = response.body()?.string().toString()
                val jsonObj = JSONObject(te)
                val d = Gson().fromJson(te, ConsumeApi::class.java)
                var temp = d.main.temp
                var country = d.sys.country
                var city = d.name
                var clouds = d.weather[0].description
                var icon = d.weather[0].icon
                icon = "a" + icon
                val id = resources.getIdentifier(icon, "drawable", getPackageName())
                val tempo = temp.toDouble() - 273.15
                val formatTempo = "%.2f".format(tempo)
                val tempTxt = lblPersonalInfo.text
                var flag = false
                while (!flag) {

                    try {
                        lblPersonalInfo.setText(tempTxt.toString() + " " + city + " " + country + " " + formatTempo + " Cº" + " " + clouds)
                        imageIcon.setImageResource(id)
                        flag = true
                    } catch (ex: Exception) {

                    }
                }
            }
        })
    }
}



