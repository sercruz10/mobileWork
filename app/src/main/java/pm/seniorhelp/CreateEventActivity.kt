package pm.seniorhelp

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_event.*
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.OnProgressListener

import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.util.UUID.randomUUID








class CreateEventActivity : Activity() {


    var button: Button? = null
    private val RESULT_LOAD_IMAGE = 1
    private val TAB = "Create Event"

    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.getReference()
    var imageName = "ok"



    private val galleryPermissions = arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)



        val spinner = color_spinner

        val adapter = ArrayAdapter.createFromResource(this,
                R.array.colors_array, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter




        getPermissions()
        btnLoadImage.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(intent, RESULT_LOAD_IMAGE)
        })


        lblDate.setOnClickListener {
            getTime()
        }

        btnAddValueSave.setOnClickListener{

            val formatter = SimpleDateFormat("dd.MM.yyyy, HH:mm")
            val name = nameEvent.text
            val color = color_spinner.selectedItem
            val dataEvent = lblDate.text
            val time = time_picker.getCurrentHour().toString() +":" +time_picker.getCurrentMinute().toString()
            val timeMills = formatter.parse(dataEvent.toString()+", "+time)
            val oldMillis = timeMills.time
            val img = imageView.drawable
            val otherStrings = arrayOf(name.toString(), color.toString(), oldMillis.toString(),imageName.toString())

            val intent = Intent(baseContext, CalendarActivity::class.java)
            intent.putExtra("ev", otherStrings)
            setResult(RESULT_OK, intent)
            startActivity(intent)

        }

    }

    override fun onActivityResult(requestCode: Int, result: Int, data: Intent) {
        super.onActivityResult(requestCode, result, data)
        if (requestCode == RESULT_LOAD_IMAGE && result == Activity.RESULT_OK && null != data) {
            val selectImg = data.data

            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            //Log.d(TAB, "OK: " +selectImg)
            val cursor = contentResolver.query(selectImg, filePathColumn, null, null, null)
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
            if (picturePath != null) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                imageName = UUID.randomUUID().toString()
                val ref = storageReference.child("images/" +imageName )
                ref.putFile(selectImg)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                        }
                        .addOnProgressListener { taskSnapshot ->
                            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                                    .totalByteCount
                            progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                        }
            }

        } else {

            Toast.makeText(this, "You haven´t pick Image", Toast.LENGTH_LONG).show()
        }



    }

    private fun getPermissions() {
    /*    if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "", 0,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "", 0,
                    WRITE_EXTERNAL_STORAGE)
        }
*/
    }


    fun getTime(){



      val c = Calendar.getInstance()
      val year = c.get(Calendar.YEAR)
      val month = c.get(Calendar.MONTH)
      val day = c.get(Calendar.DAY_OF_MONTH)


      val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

          val m = monthOfYear.toInt()+1;
          lblDate.setText("" + dayOfMonth + "." + m + "." + year)
      }, year, month, day)
      dpd.show()


    }





}

