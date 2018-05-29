package pm.seniorhelp

import android.Manifest
import android.app.Activity
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.R.attr.button
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_create_event.*
import android.widget.Toast
import pub.devrel.easypermissions.EasyPermissions
import android.graphics.BitmapFactory

import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE

import android.R.attr.data
import android.R.attr.button
import android.net.Uri
import android.R.attr.data
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.DialogFragment


class CreateEventActivity : Activity() {


    var button: Button? = null
    private val RESULT_LOAD_IMAGE = 1
    private val TAB = "Create Event"

    private val galleryPermissions = arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        getPermissions()
        btnLoadImage.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(intent, RESULT_LOAD_IMAGE)
        })









    }

    override fun onActivityResult(requestCode: Int, result: Int, data: Intent) {
        super.onActivityResult(requestCode, result, data)
        if (requestCode == RESULT_LOAD_IMAGE && result == Activity.RESULT_OK && null != data) {
            val selectImg = data.data

            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            Log.d(TAB, "OKOK: " +selectImg)
            val cursor = contentResolver.query(selectImg, filePathColumn, null, null, null)
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))


        } else {

            Toast.makeText(this, "You haven´t pick Image", Toast.LENGTH_LONG).show()
        }


    }

    private fun getPermissions() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "", 0,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "", 0,
                    WRITE_EXTERNAL_STORAGE)
        }

    }





}