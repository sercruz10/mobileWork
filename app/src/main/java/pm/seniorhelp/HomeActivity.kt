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
import kotlinx.android.synthetic.main.activity_home.*
import pub.devrel.easypermissions.EasyPermissions


import java.util.*
import com.google.firebase.database.*


class HomeActivity : Activity() {

    val database = FirebaseDatabase.getInstance()
    var numbers : LinkedHashMap<String,String> = linkedMapOf();



    private val perms = arrayOf(
            ACCESS_FINE_LOCATION,
            WRITE_EXTERNAL_STORAGE,
            CALL_PHONE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        getPermissions()

        val myRef = database.getReference("/numbers/")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                numbers.clear()
                snapshot.children.forEach{item : DataSnapshot ->
                    numbers.put(item.key.toString(), item.value.toString())
                }
                textPhone1.text =ArrayList<String>(numbers.keys).get(0)
                textPhone2.text=ArrayList<String>(numbers.keys).get(1)
                textPhone3.text=ArrayList<String>(numbers.keys).get(2)
            }
            override fun onCancelled(error: DatabaseError) {}
        })        
        
        imageWallet.setOnClickListener(){
            val intent = Intent(this, FinanceActivity::class.java)
            // To pass any data to next activity
            //intent.putExtra("DateTime", localDateNow)
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

            callSomeone(ArrayList<String>(numbers.values).get(0))
        }

        call2.setOnClickListener(){
            callSomeone(ArrayList<String>(numbers.values).get(1))
        }

        call3.setOnClickListener(){
            callSomeone(ArrayList<String>(numbers.values).get(2))
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

    private fun callSomeone(telemovel: String) {
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
