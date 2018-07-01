package com.nodomain.freeyourgadget.gadgetbridge.pm.seniorhelp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.nodomain.freeyourgadget.gadgetbridge.R
import kotlinx.android.synthetic.main.activity_map.*


class Map : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val extras = intent.extras

        val newString = extras.getStringArray("ev")

       val ver = web
        web.settings.javaScriptEnabled = true

        val string = ddToDms(newString[0].toDouble(),newString[1].toDouble())



        val url = "https://www.google.pt/maps/search/@"+newString[0]+","+newString[1]

        System.out.println(url)

        ver.loadUrl(url)


        val gmmIntentUri = Uri.parse("geo:"+newString[0]+","+newString[1])
val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
    startActivity(mapIntent)
}







    }



    fun ddToDms(latitude:Double, longitude:Double):String {


        var latResult = "z"
        var latResultNS = "z"
        var lngResult = "z"
        var lngResultNS = "z"


        var dmsResult = "z"



        if (latitude >= 0) {
            latResultNS = "N"
        } else {
            latResultNS = "S"
        }


        latResult = getDms(latitude)
        latResult += latResultNS

        if (longitude >= 0) {
            lngResultNS = "E"
        } else {
            lngResultNS = "W"
        }


        // Call to getDms(lng) function for the coordinates of Longitude in DMS.
        // The result is stored in lngResult variable.
        lngResult = getDms(longitude)
        lngResult += lngResultNS

        // Joining both variables and separate them with a space.
        dmsResult = latResult + '+' + lngResult

        // Return the resultant string
        return dmsResult
    }

    fun getDms(value:Double):String {

        var valDeg = 1.11
        var valMin = 1.11
        var valSec = 1.11
        var result = "ok"

        val nvalue = Math.abs(value)

        valDeg = Math.floor(nvalue)
        result = valDeg.toString() + "º"

        valMin = Math.floor((nvalue - valDeg) * 60)
        result += valMin.toString() + "'"

        valSec = Math.round((nvalue.toDouble() - valDeg.toDouble() - valMin.toDouble() / 60) * 3600 * 1000).toDouble() / 1000
        result += valSec.toString() + '"'



        return result
    }


}
