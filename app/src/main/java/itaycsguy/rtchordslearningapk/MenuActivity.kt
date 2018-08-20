package itaycsguy.rtchordslearningapk

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView

class MenuActivity : AppCompatActivity() {

    lateinit var searchButton : ImageButton
    lateinit var uploadButton: ImageButton
    lateinit var imageView : ImageView
//    private lateinit var imageUri: Uri

    val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        searchButton = findViewById(R.id.SearchButton)
        uploadButton = findViewById(R.id.UploadButton)
        imageView = findViewById(R.id.UploadedView)

        searchButton.setOnClickListener {
            openGallery()
        }

    }

    private fun openGallery(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent){
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode == Activity.RESULT_OK).and(requestCode == PICK_IMAGE)){
//            imageUri = data.data
            imageView.setImageURI(data.data)
        }

    }

//    TODO:This is how to swap activities!
//    fun activitySwap(){
//        startActivity(Intent(this, secondActivity::class.java))
//    }

}

