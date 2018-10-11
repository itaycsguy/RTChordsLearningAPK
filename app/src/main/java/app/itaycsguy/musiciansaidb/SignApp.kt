package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class SignApp(act : AppCompatActivity,fbDb : FirebaseDB) : AppCompatActivity(),TextWatcher {
    private val REQUEST_CODE = 2
    private val GALLERY = 3
    private val CAMERA = 4
    private val _act : StartActivity = act as StartActivity
    private val _fbDb : FirebaseDB = fbDb
    private lateinit var _signUpBtn : Button
    private var _imageView: ImageView? = null
    private lateinit var _photoPath : Uri
    private lateinit var _signUpResult : HashMap<String,String>
    companion object {
        private val IMAGE_DIRECTORY = "/UserProfileImage"
    }

    fun initOperations() {
        _act.findViewById<EditText>(R.id.registiration_email).addTextChangedListener(this)
        _act.findViewById<EditText>(R.id.registiration_givenname).addTextChangedListener(this)
        _act.findViewById<EditText>(R.id.registiration_family_name).addTextChangedListener(this)
        _act.findViewById<EditText>(R.id.registiration_username).addTextChangedListener(this)
        _act.findViewById<EditText>(R.id.registiration_password).addTextChangedListener(this)
        _act.findViewById<EditText>(R.id.registiration_confirm_password).addTextChangedListener(this)

        _act.findViewById<EditText>(R.id.registiration_email).requestFocus()
        _signUpBtn = _act.findViewById(R.id.registiration_signup_button)
        _signUpBtn.setOnClickListener {
            val email = (_act.findViewById<EditText>(R.id.registiration_email)).text.toString()
            val givenname = (_act.findViewById<EditText>(R.id.registiration_givenname)).text.toString()
            val familyname = (_act.findViewById<EditText>(R.id.registiration_family_name)).text.toString()
            val username = (_act.findViewById<EditText>(R.id.registiration_username)).text.toString()
            val password = (_act.findViewById<EditText>(R.id.registiration_password)).text.toString()
            val photo = _photoPath.toString()
            val isValid : Boolean = isValidPassword(password) && isCorrectEmailFormat(email)
            if (!isValid) { Toast.makeText(_act, "Invalid provided details.", Toast.LENGTH_LONG).show() }
            else {
                _fbDb.getRef()!!.child("users/${FirebaseDB.encodeUserEmail(email)}").ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if(!p0.exists()) {
                            Toast.makeText(_act, "Registered!", Toast.LENGTH_LONG).show()
                            val map : HashMap<String,String> = HashMap()
                            map["authentication_vendor"] = "app"
                            map["user_name"] = username
                            map["given_name"] = givenname
                            map["family_name"] = familyname
                            map["password"] = password
                            map["email"] = email
                            map["photo"] = photo
                            map["permission"] = "anonymous"
                            val intent = _act.intent
                            intent.putExtra("data",map)
                            _act.onActivityResultWrapper(REQUEST_CODE,intent)
                        } else {
                            Toast.makeText(_act, "The account does already exist!", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) { Toast.makeText(_act, "Data corruption!", Toast.LENGTH_LONG).show() }
                })
            }
        }
        _act.findViewById<Button>(R.id.registiration_attach_photo_button).setOnClickListener { showPhotoDialog() }
        _act.findViewById<Button>(R.id.registiration_cancel_button).setOnClickListener { _act.showLogin() }
        this._imageView = ImageView(_act)
    }

    override fun afterTextChanged(p0: Editable?) {
        p0?.let {
            // check spelling
            Toast.makeText(_act, p0, Toast.LENGTH_LONG).show()
        }
    }

    // not relevant but exist
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    fun getUserData() : HashMap<String,String> {
        return _signUpResult
    }

    fun getReqCode() : Int {
        return REQUEST_CODE
    }

    fun getReqGalCode() : Int {
        return GALLERY
    }

    fun getReqCamCode() : Int {
        return CAMERA
    }

    private fun showPhotoDialog() {
        val pictureDialog = AlertDialog.Builder(_act)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) {
            _, which ->
            when (which) {
                0 -> { choosePhotoFromGallary() }
                1 -> { takePhotoFromCamera() }
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        _act.startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        _act.startActivityForResult(intent, CAMERA)
    }

    private fun visibleExploredData(){
        val phtoPathView = _act.findViewById<TextView>(R.id.registiration_photo_location_view)
        phtoPathView.append("Photo Location: " + _photoPath.toString())
        phtoPathView.visibility = View.VISIBLE
        _signUpBtn.visibility = View.VISIBLE
    }

    fun handleResult(requestCode:Int, data: Intent?) {
        var success = false
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(_act.contentResolver, contentURI)
                    val retVals = saveImage(bitmap)
                    if(!retVals.isEmpty()) {
                        this._photoPath = Uri.parse(retVals)
                    }
                    Toast.makeText(_act, "Photo Saved!", Toast.LENGTH_SHORT).show()
                    success = true
                    this._imageView!!.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(_act, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            this._imageView!!.setImageBitmap(thumbnail)
            val retVals = saveImage(thumbnail)
            if(!retVals.isEmpty()) {
                this._photoPath = Uri.parse(retVals)
            }
            success = true
            Toast.makeText(_act, "Image Saved!", Toast.LENGTH_SHORT).show()
        } else if(requestCode == REQUEST_CODE) {
            if(data!!.hasExtra("data")) _signUpResult = data.getSerializableExtra("data") as HashMap<String, String>
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
            MediaScannerConnection.scanFile(_act, arrayOf(f.path), arrayOf("photo/jpeg"), null)
            fo.close()
            Log.d("TAG", "Image Saved: " + f.absolutePath)
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }
}