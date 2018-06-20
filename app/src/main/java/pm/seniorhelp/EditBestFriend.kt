package pm.seniorhelp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_best_friend.*

class EditBestFriend : Activity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("/number1")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_best_friend)

        btnSubmitBestFriend.setOnClickListener {
            val name = txtNameBestFriend.text
            var number = txtNumberBestFriend.text

            myRef.setValue(System.currentTimeMillis().toString() +"="+ name+"="+number)
            val intent = Intent(baseContext, HomeActivity::class.java)

            startActivity(intent)


        }
    }
}
