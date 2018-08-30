package itaycsguy.rtchordslearningapk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialogFragment
import android.widget.ImageButton
import android.widget.ImageView
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_SHORT
import android.view.View
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.support.v4.app.NotificationCompat.getExtras
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.menu_activity.*
import java.io.File
import java.util.jar.Manifest

@SuppressLint("ByteOrderMark")
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
    Testing for crop
     */
    lateinit var file : File

    lateinit var cropIntent : Intent
    lateinit var displayMetrics : DisplayMetrics
    var width : Int = 0
    var height : Int = 0
    lateinit var toolbar : Toolbar
    /*
    Const values for result
     */
    val PICK_IMAGE = 100

    private val TAG = "Permissions"
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PERMISSION_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        cordinatorView = findViewById(R.id.myCoordinatorLayout)
//        searchButton = findViewById(R.id.SearchButton)

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = ("Choose Operation")
        setSupportActionBar(toolbar)
        var permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_DENIED){
            Log.i(TAG, "Permission to use camera denied")
            makeRequest()
        }
//        uploadButton = findViewById(R.id.UploadButton)
//        uploadButton.setOnClickListener{
//            Snackbar.make(cordinatorView, "Sorry Not Implemented Yet!", LENGTH_SHORT).show()
//        }
//
//        editButton = findViewById(R.id.EditButton)
//        editButton.setOnClickListener{
//            Snackbar.make(cordinatorView, "Sorry Not Implemented Yet!", LENGTH_SHORT).show()
//        }
//        cameraButton = findViewById(R.id.CameraButoon)
//        cameraButton.setOnClickListener {
//            openCamera()
//        }
        imageView = findViewById(R.id.UploadedView)

//        searchButton.setOnClickListener {
//            openGallery()
//        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_PERMISSION_CODE)
    }

//    ﻿override fun onRequestPermissionsResult(requestCode: Int,
//                                             permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            REQUEST_PERMISSION_CODE -> {
//                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Log.i(TAG, "Permission has been denied by user")
//                } else {
//                    Log.i(TAG, "Permission has been granted by user")
//                }
//            }
//        }
//    }﻿
    private lateinit var uri: Uri
    private lateinit var takePictureIntent: Intent

    private fun openCamera() {
//        file = File(Environment.getExternalStorageDirectory(),
//                "file ${System.currentTimeMillis()}.jpg")
//        uri = Uri.fromFile(file)
        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//        takePictureIntent.putExtra("return-data", true)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if (item.itemId == R.id.btn_camera){
                openCamera()
            } else if (item.itemId == R.id.btn_gallery){
                openGallery()
            }
        }
        return false
    }



    //    TODO:This is how to swap activities!
//    fun activitySwap(){
//        startActivity(Intent(this, secondActivity::class.java))
//    }

}

