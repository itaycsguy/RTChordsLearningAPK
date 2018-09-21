package itaycsguy.rtchordslearningapk

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
//import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class SignupActivity : AppCompatActivity() , View.OnClickListener {
//    private val _database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var _buttonSignUp : Button
    private lateinit var _photoPath : Uri
    private var _imageview: ImageView? = null
    private val GALLERY = 1
    private val CAMERA = 2
    private val PASS_LENGTH = 8

    companion object {
        private val IMAGE_DIRECTORY = "/MusiciansDBPhotons"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        this._buttonSignUp = findViewById<Button>(R.id.registiration_signup_button)
        this._buttonSignUp.setOnClickListener(this)
        val btnAttachPhoto = findViewById<Button>(R.id.registiration_attach_photo_button)
        btnAttachPhoto.setOnClickListener(this)
        this._imageview = ImageView(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.registiration_signup_button -> {
                val email = (findViewById<EditText>(R.id.registiration_email)).text.toString()
                val givenname = (findViewById<EditText>(R.id.registiration_givenname)).text.toString()
                val familyname = (findViewById<EditText>(R.id.registiration_family_name)).text.toString()
                val username = (findViewById<EditText>(R.id.registiration_username)).text.toString()
                val password = (findViewById<EditText>(R.id.registiration_password)).text.toString()
                val photo = this._photoPath.toString()
                val isValid : Boolean = this.isValidPassword(password) && this.isValidEmail(email)
                val text : String
                if (!isValid) {
                    text = "Invalid provided details."
                    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                } else {
                    text = "Registered!"
                    Toast.makeText(this, text, Toast.LENGTH_LONG).show()

                    val map : HashMap<String,String> = HashMap()
                    map.put("authentication_vendor", "local")
                    map.put("user_name", username)
                    map.put("given_name",givenname)
                    map.put("family_name",familyname)
                    map.put("password", password)
                    map.put("email", email)
                    map.put("photo",photo)
                    this.writeDB("users/" + email,map)

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("email",email)
                    intent.putExtra("password",password)
                    intent.putExtra("photo",photo)
                    startActivity(intent)
                }
            }
            R.id.registiration_attach_photo_button -> {
                this.showPhotoDialog()
            }
        }
    }

    private fun showPhotoDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) {
            dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun visibleExploredData(){
        val phtoPathView = findViewById(R.id.registiration_photo_location_view) as TextView
        phtoPathView.append("Photo Location: " + this._photoPath.toString())
        phtoPathView.visibility = View.VISIBLE
        this._buttonSignUp.visibility = View.VISIBLE
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var SUCCESS = true
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val retVals = saveImage(bitmap)
                    if(!retVals.isEmpty()) {
                        this._photoPath = Uri.parse(retVals)
                    }
                    Toast.makeText(this, "Photo Saved!", Toast.LENGTH_SHORT).show()
                    this._imageview!!.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                    SUCCESS = true
                }
            }
        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            this._imageview!!.setImageBitmap(thumbnail)
            val retVals = saveImage(thumbnail)
            if(!retVals.isEmpty()) {
                this._photoPath = Uri.parse(retVals)
            }
            Toast.makeText(this, "Photo Saved!", Toast.LENGTH_SHORT).show()
        }
        if(SUCCESS) {
            this.visibleExploredData()
        }
    }

    private fun saveImage(myBitmap: Bitmap) : String {
        val bytes = ByteArrayOutputStream()
        val photoQuality = 90
        myBitmap.compress(Bitmap.CompressFormat.JPEG, photoQuality, bytes)
        val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.getPath()), arrayOf("photo/jpeg"), null)
            fo.close()
            Log.d("TAG", "Photo Saved: " + f.getAbsolutePath())
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    private fun isValidPassword(password: String):Boolean{
        return password.length >= this.PASS_LENGTH
    }

    private fun isValidEmail(email: String):Boolean{
//        val usersEntry = this._database.getReference("users")
//        if(usersEntry.child(email).key!!.isEmpty() && this.isCorrectEmailFormat(email)) {
//            return true
//        }
//        return false
        return false
    }

    fun isCorrectEmailFormat(email: String): Boolean {
        val pattern = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$".toRegex()
        val matchResult = pattern.matchEntire(email)
        return matchResult == null
    }

    fun writeDB(dbPath : String,map : HashMap<String,String>) {
//        val userEntry = this._database.getReference(dbPath.trim().replace("@","_").replace(".","_"))
//        userEntry.setValue(map)
    }
}