package pm.seniorhelp

import android.app.Activity
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.R.attr.button
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_create_event.*



class CreateEventActivity : Activity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)


    }
}