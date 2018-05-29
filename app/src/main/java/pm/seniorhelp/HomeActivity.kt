package pm.seniorhelp

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home.*
import pub.devrel.easypermissions.EasyPermissions

import com.google.firebase.database.DatabaseReference


import java.util.*
import android.graphics.BitmapFactory


class HomeActivity : Activity() {






    var localDateNow = Calendar.getInstance().getTime()
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
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Sérgio Cruz")

        getPermissions()


        imageWallet.setOnClickListener(){
            val intent = Intent(this, FinanceActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("DateTime", localDateNow)
            startActivity(intent)
        }
        imageGear.setOnClickListener(){
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        imageCalendar.setOnClickListener(){
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        imageHealth.setOnClickListener(){
            startActivity(Intent(this, HealthActivity::class.java))
        }


        //TODO Connect a a text config maybe upload to firebase and download that information an put in uri parse
        call1.setOnClickListener(){

            callSomeone("960233626")
        }

        call2.setOnClickListener(){
            callSomeone("960233626")
        }

        call3.setOnClickListener(){
            callSomeone("960233626")
        }
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
}
