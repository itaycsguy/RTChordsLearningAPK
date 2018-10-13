package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.graphics.drawable.Icon
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.bumptech.glide.util.Util
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
    private val MIN_STRENGTH_PASSWORD = 0.6
    private val REQUEST_CODE = 2
    private val GALLERY = 3
    private val CAMERA = 4
    private val _act : StartActivity = act as StartActivity
    private val _fbDb : FirebaseDB = fbDb
    private lateinit var _signUpBtn : Button
    private var _imageView: ImageView? = null
    private var _photoPath : Uri? = null
    private lateinit var _signUpResult : HashMap<String,String>
    companion object {
        private val IMAGE_DIRECTORY = "/UserProfileImage"
    }

    fun initOperations() {
        val email = _act.findViewById<EditText>(R.id.registiration_email)
        email.addTextChangedListener(this)
        val givenName = _act.findViewById<EditText>(R.id.registiration_givenname)
        givenName.addTextChangedListener(this)
        val familyName = _act.findViewById<EditText>(R.id.registiration_family_name)
        familyName.addTextChangedListener(this)
        val username = _act.findViewById<EditText>(R.id.registiration_username)
        username.addTextChangedListener(this)
        val password = _act.findViewById<EditText>(R.id.registiration_password)
        password.addTextChangedListener(this)
        val confirmPassword = _act.findViewById<EditText>(R.id.registiration_confirm_password)
        confirmPassword.addTextChangedListener(this)
        // confirmPassword.setBackgroundResource(android.R.color.white)

        _act.findViewById<EditText>(R.id.registiration_email).requestFocus()
        _signUpBtn = _act.findViewById(R.id.registiration_signup_button)
        _signUpBtn.setOnClickListener {
            val email = (_act.findViewById<EditText>(R.id.registiration_email)).text.toString()
            val givenname = (_act.findViewById<EditText>(R.id.registiration_givenname)).text.toString()
            val familyname = (_act.findViewById<EditText>(R.id.registiration_family_name)).text.toString()
            val username = (_act.findViewById<EditText>(R.id.registiration_username)).text.toString()
            val password = (_act.findViewById<EditText>(R.id.registiration_password)).text.toString()
            if(     email.isNotEmpty() &&
                    givenname.isNotEmpty() &&
                    familyname.isNotEmpty() &&
                    username.isNotEmpty() &&
                    password.isNotEmpty() &&
                    _photoPath != null) {
                val photo = _photoPath.toString()
                val isValid: Boolean = isValidPassword(password) && isCorrectEmailFormat(email)
                if (!isValid) {
                    CustomSnackBar.make(_act, "Invalid details are provided!")
                } else {
                    _fbDb.getRef()!!.child("users/${FirebaseDB.encodeUserEmail(email)}").ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()) {
                                CustomSnackBar.make(_act, "Registered!")
                                val map: HashMap<String, String> = HashMap()
                                map["authentication_vendor"] = "app"
                                map["user_name"] = username
                                map["given_name"] = givenname
                                map["family_name"] = familyname
                                map["password"] = password
                                map["email"] = email
                                map["photo"] = photo
                                map["permission"] = "anonymous"
                                val intent = _act.intent
                                intent.putExtra("data", map)
                                _act.onActivityResultWrapper(REQUEST_CODE, intent)
                            } else {
                                CustomSnackBar.make(_act, "Account does already exist!")
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            CustomSnackBar.make(_act, "Data corruption!")
                        }
                    })
                }
            } else {
                CustomSnackBar.make(_act,"Some details are missing!")
            }
        }
        _act.findViewById<Button>(R.id.registiration_attach_photo_button).setOnClickListener { showPhotoDialog() }
        _act.findViewById<Button>(R.id.registiration_cancel_button).setOnClickListener { _act.showLogin() }
        this._imageView = ImageView(_act)
    }

    override fun afterTextChanged(p0: Editable?) {
        p0?.let {
            val successContent = ContextCompat.getDrawable(_act,R.drawable.success)
            val errorContent = ContextCompat.getDrawable(_act,R.drawable.error)
            when(_act.currentFocus) {
                _act.findViewById<EditText>(R.id.registiration_email) -> {
                    val email = _act.findViewById<EditText>(R.id.registiration_email)
                    if(isCorrectEmailFormat(email.text.toString())) {
                        email.setCompoundDrawablesWithIntrinsicBounds(null, null, successContent, null)
                        email.setBackgroundColor(Color.parseColor("#c1e7d2"))
                    } else {
                        email.setCompoundDrawablesWithIntrinsicBounds(null, null, errorContent, null)
                        email.setBackgroundColor(Color.parseColor("#f7bfbf"))
                    }
                }
                _act.findViewById<EditText>(R.id.registiration_givenname) -> {
                    val givenname = _act.findViewById<EditText>(R.id.registiration_givenname)
                    if(givenname.text.toString().length > 1){
                        givenname.setCompoundDrawablesWithIntrinsicBounds(null, null, successContent, null)
                        givenname.setBackgroundColor(Color.parseColor("#c1e7d2"))
                    } else {
                        givenname.setCompoundDrawablesWithIntrinsicBounds(null, null, errorContent, null)
                        givenname.setBackgroundColor(Color.parseColor("#f7bfbf"))
                    }
                }
                _act.findViewById<EditText>(R.id.registiration_family_name) -> {
                    val familyname = _act.findViewById<EditText>(R.id.registiration_family_name)
                    if(familyname.text.toString().length > 1){
                        familyname.setCompoundDrawablesWithIntrinsicBounds(null, null, successContent, null)
                        familyname.setBackgroundColor(Color.parseColor("#c1e7d2"))
                    } else {
                        familyname.setCompoundDrawablesWithIntrinsicBounds(null, null, errorContent, null)
                        familyname.setBackgroundColor(Color.parseColor("#f7bfbf"))
                    }
                }
                _act.findViewById<EditText>(R.id.registiration_username) -> {
                    val username = _act.findViewById<EditText>(R.id.registiration_username)
                    if(username.text.toString().isNotEmpty()){
                        username.setCompoundDrawablesWithIntrinsicBounds(null, null, successContent, null)
                        username.setBackgroundColor(Color.parseColor("#c1e7d2"))
                    } else {
                        username.setCompoundDrawablesWithIntrinsicBounds(null, null, errorContent, null)
                        username.setBackgroundColor(Color.parseColor("#f7bfbf"))
                    }
                }
                _act.findViewById<EditText>(R.id.registiration_password) -> {
                    val password = _act.findViewById<EditText>(R.id.registiration_password)
                    val passwordManager = PasswordManager()
                    val evaluation = passwordManager.evaluatePassword(password.text.toString())
                    if(evaluation >= MIN_STRENGTH_PASSWORD) {
                        password.setCompoundDrawablesWithIntrinsicBounds(null, null, successContent, null)
                        password.setBackgroundColor(Color.parseColor("#c1e7d2"))
                    } else {
                        password.setCompoundDrawablesWithIntrinsicBounds(null, null, errorContent, null)
                        password.setBackgroundColor(Color.parseColor("#f7bfbf"))
                    }
                }
                _act.findViewById<EditText>(R.id.registiration_confirm_password) -> {
                    val password = _act.findViewById<EditText>(R.id.registiration_password)
                    val confirmPassword = _act.findViewById<EditText>(R.id.registiration_confirm_password)
                    if(password.text.toString().isNotEmpty()) {
                        if(password.text.toString() == confirmPassword.text.toString()){
                            confirmPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, successContent, null)
                            confirmPassword.setBackgroundColor(Color.parseColor("#c1e7d2"))
                        } else {
                            confirmPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, errorContent, null)
                            confirmPassword.setBackgroundColor(Color.parseColor("#f7bfbf"))
                        }
                    } else {
                        password.setCompoundDrawablesWithIntrinsicBounds(null, null, errorContent, null)
                    }
                }
            }
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
                    CustomSnackBar.make(_act,"Saved!")
                    success = true
                    this._imageView!!.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    CustomSnackBar.make(_act,"Failed!")
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
            CustomSnackBar.make(_act,"Attached!")
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
            Log.d("TAG", "Image Attached: " + f.absolutePath)
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }
}