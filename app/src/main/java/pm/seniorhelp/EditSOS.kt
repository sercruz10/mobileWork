package pm.seniorhelp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_sos.*

class EditSOS : Activity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("/number2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_sos)

        btnSubmitSOS.setOnClickListener {
            val name = txtNameSOS.text
            var number = txtNumberSOS.text

            myRef.setValue(System.currentTimeMillis().toString() +"="+ name+"="+number)
            val intent = Intent(baseContext, HomeActivity::class.java)

            startActivity(intent)


        }
    }
}
