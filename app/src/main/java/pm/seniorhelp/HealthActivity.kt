package pm.seniorhelp

import android.app.Activity
import android.os.Bundle

class HealthActivity  : Activity(){
    var miBand = MiBand.getInstance(this@HealthActivity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health)
    }
}