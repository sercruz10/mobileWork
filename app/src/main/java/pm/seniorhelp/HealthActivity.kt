package pm.seniorhelp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import java.io.File
import java.io.IOException


class HealthActivity  : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health)
        val appsDir = File("/data/app");

        System.out.print("OLA " + appsDir)
        Log.d("ola : " ,appsDir.toString())

        val i = packageManager.getLaunchIntentForPackage("nodomain.freeyourgadget.gadgetbridge")
        startActivity(i)



        //val tempFile = openResourceFile(this, R.raw.app, "app.apk")
        //val intent = Intent(Intent.ACTION_VIEW)
        //intent.setDataAndType(Uri.fromFile(tempFile), "application/vnd.android.package-archive")
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //startActivity(intent)

    }

    @Throws(IOException::class)
    fun openResourceFile(context: Context, resFile: Int, tempFileName: String): File {
        val `in` = context.getResources().openRawResource(resFile)

        val b = ByteArray(`in`.available())
        `in`.read(b)

        val fout = context.openFileOutput(tempFileName, Context.MODE_WORLD_READABLE)

        fout.write(b)
        fout.close()
        `in`.close()

        return context.getFileStreamPath(tempFileName)
    }
}