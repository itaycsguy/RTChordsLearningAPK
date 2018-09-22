package app.itaycsguy.musiciansaidb

import android.annotation.SuppressLint
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


@SuppressLint("Registered")
@Suppress("NAME_SHADOWING")
class SignupActivity : AppCompatActivity() , View.OnClickListener {
    private val _database : FirebaseDatabase = FirebaseDatabase.getInstance()
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

        findViewById<EditText>(R.id.registiration_email).requestFocus()
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
                val isValid : Boolean = this.isValidPassword(password) && this.isCorrectEmailFormat(email)
                val text : String
                if (!isValid) {
                    text = "Invalid provided details."
                    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                } else {
                    val emailParts : List<String> = email.split("@")
                    val secondPart = emailParts[1].replace(".","_")
                    val newEmail = emailParts[0] + "_" + secondPart
                    this._database.reference.child("users/$newEmail").ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if(!p0.exists()) {
                                val map : HashMap<String,String> = HashMap()
                                map["authentication_vendor"] = "local"
                                map["user_name"] = username
                                map["given_name"] = givenname
                                map["family_name"] = familyname
                                map["password"] = password
                                map["email"] = email
                                map["photo"] = photo
                                map["permission"] = "anonymous"
                                writeDB("users/$email",map)

                                val text = "Registered!"
                                Toast.makeText(this@SignupActivity, text, Toast.LENGTH_LONG).show()

                                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                                intent.putExtra("authentication_vendor","local")
                                intent.putExtra("user_name","username")
                                intent.putExtra("email",email)
                                intent.putExtra("password",password)
                                intent.putExtra("photo",photo)
                                intent.putExtra("given_name",givenname)
                                intent.putExtra("family_name",familyname)
                                intent.putExtra("permission","anonymous")
                                startActivity(intent)
                            } else {
                                val text = "The account does already exist!"
                                Toast.makeText(this@SignupActivity, text, Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onCancelled(p0: DatabaseError) {
                            val text = "Data corruption!"
                            Toast.makeText(this@SignupActivity, text, Toast.LENGTH_LONG).show()
                        }
                    })
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
        val phtoPathView = findViewById<TextView>(R.id.registiration_photo_location_view)
        phtoPathView.append("Photo Location: " + this._photoPath.toString())
        phtoPathView.visibility = View.VISIBLE
        this._buttonSignUp.visibility = View.VISIBLE
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var success = true
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
                    success = true
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
        if(success) {
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
            val f = File(wallpaperDirectory, ((Calendar.getInstance().timeInMillis).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("photo/jpeg"), null)
            fo.close()
            Log.d("TAG", "Photo Saved: " + f.absolutePath)
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    private fun isValidPassword(password: String):Boolean{
        return password.length >= this.PASS_LENGTH
    }

    private fun isCorrectEmailFormat(email: String): Boolean {
        val pattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
        val match : MatchResult? = pattern.toRegex(setOf(RegexOption.IGNORE_CASE,RegexOption.DOT_MATCHES_ALL)).find(email)
        match?.let {
            return true
        }
        return false
    }

    private fun writeDB(dbPath : String, map : HashMap<String,String>) {
        val userEntry = this._database.getReference(dbPath.trim().replace("@","_").replace(".","_"))
        userEntry.setValue(map)
    }
}

private fun String.toRegex(s: String) {

}
