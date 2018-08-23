package itaycsguy.rtchordslearningapk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.ImageView
import android.R.attr.duration
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_SHORT
import android.view.View
import android.graphics.Bitmap
import android.R.attr.data
import android.support.v4.app.NotificationCompat.getExtras




class MenuActivity : AppCompatActivity() {
    /*
    Variables of the activity
     */
    lateinit var searchButton : ImageButton
    lateinit var uploadButton: ImageButton
    lateinit var editButton: ImageButton
    lateinit var cameraButton: ImageButton
    lateinit var imageView : ImageView
    lateinit var cordinatorView : View

    /*
    Const values for result
     */
    val PICK_IMAGE = 100
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        cordinatorView = findViewById(R.id.myCoordinatorLayout)
        searchButton = findViewById(R.id.SearchButton)

        uploadButton = findViewById(R.id.UploadButton)
        uploadButton.setOnClickListener{
            Snackbar.make(cordinatorView, "Sorry Not Implemented Yet!", LENGTH_SHORT).show()
        }

        editButton = findViewById(R.id.EditButton)
        editButton.setOnClickListener{
            Snackbar.make(cordinatorView, "Sorry Not Implemented Yet!", LENGTH_SHORT).show()
        }
        cameraButton = findViewById(R.id.CameraButoon)
        cameraButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
        imageView = findViewById(R.id.UploadedView)

        searchButton.setOnClickListener {
            openGallery()
        }

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun openGallery(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent){
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode == Activity.RESULT_OK).and(requestCode == PICK_IMAGE)){
            imageView.setImageURI(data.data)
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            val imageBitmap = extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
        }

    }

//    TODO:This is how to swap activities!
//    fun activitySwap(){
//        startActivity(Intent(this, secondActivity::class.java))
//    }

}

