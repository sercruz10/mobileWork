package pm.seniorhelp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class SettingsActivity : Activity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("/personalInfo")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        lblDateBirth.setOnClickListener {
            getTime()
        }

        btnConfigBest.setOnClickListener {

            startActivity(Intent(this, EditBestFriend::class.java))
        }


        btnConfigSOS.setOnClickListener {

            startActivity(Intent(this, EditSOS::class.java))
        }

        btnConfigWH.setOnClickListener {

            startActivity(Intent(this, EditWH::class.java))
        }

        getGreetingNumber("/number1",btnConfigSOS)
        getGreetingNumber("/number2",btnConfigBest)
        getGreetingNumber("number3",btnConfigWH)

        btnSubmitPersonalInfo.setOnClickListener {
            val name = txtNameInfo.text
            val number = txtPhonePersonal.text
            val dataEvent = lblDateBirth.text
            val adress = txtAdressPersonal.text


            myRef.setValue(System.currentTimeMillis().toString() +"="+ name+"="+number+"="+dataEvent+"="+adress)
            val intent = Intent(baseContext, HomeActivity::class.java)

            startActivity(intent)


        }
    }

    fun getTime(){



        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            val m = monthOfYear.toInt()+1;
            lblDateBirth.setText("" + dayOfMonth + "." + m + "." + year)
        }, year, month, day)
        dpd.show()


    }

    fun getGreetingNumber(str:String, btn: Button){
        val myRefNumber1 = database.getReference(str)
        val gson = Gson()
        myRefNumber1.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {


                val separate1 = snapshot.value.toString().split(",".toRegex())


                var y = 0
                for (item in separate1) {
                    //println("TestBed: " + item)
                    val separete2 = item.split("=".toRegex())
                    btn.text = separete2[1]

                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
