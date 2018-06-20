package pm.seniorhelp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_wh.*

class EditWH : Activity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("/number3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_wh)

        btnSubmitWF.setOnClickListener {
            val name = txtNameWF.text
            var number = txtNumberWF.text

            myRef.setValue(System.currentTimeMillis().toString() +"="+ name+"="+number)
            val intent = Intent(baseContext, HomeActivity::class.java)

            startActivity(intent)


        }
    }
}
