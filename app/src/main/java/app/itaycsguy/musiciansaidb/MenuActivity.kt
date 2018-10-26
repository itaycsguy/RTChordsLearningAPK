package app.itaycsguy.musiciansaidb

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("NAME_SHADOWING")
@SuppressLint("ByteOrderMark", "Registered")
class MenuActivity() : AppCompatActivity(), Parcelable ,GoogleApiClient.OnConnectionFailedListener {
    /*
    Variables of the activity
     */
    private lateinit var currentImage : ImageView
    private lateinit var cordinatorView : View
    private lateinit var _user : User
    //Firebase
    private lateinit var _firebaseStorage : FirebaseStorage
    private lateinit var _storageReference : StorageReference
    private val _firebaseDB : FirebaseDB = FirebaseDB()

    lateinit var toolbar : Toolbar
    lateinit var uploadButton : ImageButton

    lateinit var file : File
    private var mFileTemp: File? = null
    private lateinit var cropIntent : Intent

    /*
    Itay additionals:
     */
    private var _imageHashPath : String? = null
    private var _placesLocation : String? = null
    private var _notesName : String? = null
    private lateinit var _alertDialog : AlertDialog

    /*
    Const values for result
     */
    private val PLACE_AUTOCOMPLETE_REQUEST_CODE = 201
    private val REQUEST_GALLERY_IMAGE = 100
    private val TAG = "Permissions"
    private val REQUEST_IMAGE_CAPTURE = 0
    private val REQUEST_PERMISSION_CODE = 2
    private val TEMP_PHOTO_FILE_NAME = "temp_photo.jpg"

    private var uri: Uri? = null
    private lateinit var takePictureIntent: Intent

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Firebase Init:
        _firebaseStorage = FirebaseStorage.getInstance()
        _storageReference = _firebaseStorage.reference

        //Get user data from Start Activity
        _user = User(intent.getSerializableExtra("user") as HashMap<String,String>)
        // Toast.makeText(this, "Logged in as ${_user.getUserName()}.", Toast.LENGTH_SHORT).show()

        // Setting temp file for cropping to external storage dir
        mFileTemp = File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME)

        setTempFile()

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        setContentView(R.layout.menu_activity)
        cordinatorView = findViewById(R.id.myCoordinatorLayout)
        uploadButton = findViewById(R.id.UploadButton)
        currentImage = findViewById(R.id.UploadedView)
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = ("My Operations")
        setSupportActionBar(toolbar)

        val permissionCameraCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val permissionWriteCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionReadCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if ((permissionCameraCheck == PackageManager.PERMISSION_DENIED)
                        .or(permissionReadCheck == PackageManager.PERMISSION_DENIED)
                        .or(permissionWriteCheck == PackageManager.PERMISSION_DENIED)

        ) {
            Log.i(TAG, "One of the Permission has been denied.")
            makeRequest()
        }


        uploadButton.setOnClickListener {
            uploadImage()
            val infoText: String = if (_user.getPermission().toLowerCase() == User.BASIC_PERMISSION){
                "The Image is pending for approval before entering our database, thanks for your support"
            } else {
                //TODO: change the user's permission to be an enum with the different permissions and address them all here.
                "The Image is automatically approved since you possess the right permission level"
            }
            Toast.makeText(this, infoText, Toast.LENGTH_LONG).show()
        }
        findViewById<FloatingActionButton>(R.id.metaDataButton).setOnClickListener { _ ->
            if(_imageHashPath == null) {
                Toast.makeText(this, "Upload an image at first!", Toast.LENGTH_LONG).show()
            } else {
                buildMetadataDialog()
            }
        }
    }

    private fun buildMetadataDialog() : AlertDialog{
        val currAct = this
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Current-Image-Metadata")
                .setView(layoutInflater.inflate(R.layout.activity_metadata,null))
                .setPositiveButton("OK") { _, _ ->
                    (_firebaseDB.getRef())?.let {
                        val progressBar = startProgressBar(currAct,R.id.progressBar)
                        it.child("temp_images_metadata/$_imageHashPath").ref.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists() && _placesLocation != null && _notesName != null) {
                                    val map = HashMap<String,String>()
                                    map["email"] = FirebaseDB.encodeUserEmail(_user.getEmail())
                                    map["writer"] = _alertDialog.findViewById<EditText>(R.id.metadata_writer).toString()
                                    map["note_name"] = _notesName.toString()
                                    map["location"] = _placesLocation.toString()
                                    map["upload_time"] = (System.currentTimeMillis()/1000).toString()
                                    try {
                                        _firebaseDB.writeTempImagesMetadata(_imageHashPath.toString(),map)
                                        Toast.makeText(currAct, "DB was updated successfully!", Toast.LENGTH_LONG).show()
                                    } catch(e : Exception){ Toast.makeText(currAct, "Could not meet an updating", Toast.LENGTH_LONG).show() }
                                } else { Log.i(TAG, "Weird problem is occurred.") }
                                stopProgressBar(progressBar)
                            }

                            override fun onCancelled(p0: DatabaseError) {
                                stopProgressBar(progressBar)
                                CustomSnackBar.make(currAct,  "Data corruption!")
                            }
                        }) }
                }
            .setNegativeButton("Cancel") { dialog, _ ->
                layoutInflater.inflate(R.layout.menu_activity,null)
                dialog.cancel()
        }
        _alertDialog = builder.create()
        _alertDialog.show()
//        add dropdown field:
//        ===================
        val notesSpinner : Spinner = _alertDialog.findViewById(R.id.metadata_note_name)
        val chordsArray = resources.getStringArray(R.array.notes_array)
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, chordsArray)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        notesSpinner.adapter = dataAdapter
        _alertDialog.findViewById<Spinner>(R.id.metadata_note_name).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0?.let {
                    _notesName = it.getItemAtPosition(p2).toString()
                    when {
                        _notesName.equals("Choose Note Type...") -> {
                            _notesName = null
                            it.setSelection(0)
                        }
                        _notesName.equals("Special Note") -> {
                            _notesName = null
                            val freeInput = EditText(currAct)
                            freeInput.hint = "Type Special Note Name"
                            freeInput.inputType = InputType.TYPE_CLASS_TEXT
                            val builder = AlertDialog.Builder(currAct)
                            builder.setTitle("Special Note:")
                                .setView(freeInput)
                                .setPositiveButton("OK") { _, _ ->
                                    _notesName = freeInput.text.toString()
                                    Toast.makeText(currAct,"$_notesName",Toast.LENGTH_LONG).show()
                            }
                            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                            builder.create().show()
                        }
                    }
                }

            }
        }
//        add google places search field:
//        ===============================
        _alertDialog.findViewById<Button>(R.id.google_maps).setOnClickListener { _ ->
            try{
                intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(this)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e : GooglePlayServicesRepairableException) {
                Toast.makeText(currAct,"${e.connectionStatusCode}",Toast.LENGTH_LONG).show()
            } catch (e : GooglePlayServicesNotAvailableException) {
                Toast.makeText(currAct,"${e.errorCode}",Toast.LENGTH_LONG).show()
            }
        }
        return _alertDialog
    }

    private fun setTempFile() {
        //Taken from simple crop image api example.
        val state = Environment.getExternalStorageState()
        mFileTemp = if (Environment.MEDIA_MOUNTED == state) {
            File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME)
        } else {
            File(filesDir, TEMP_PHOTO_FILE_NAME)
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_CODE
        )
    }


    constructor(parcel: Parcel) : this() {
        cropIntent = parcel.readParcelable(Intent::class.java.classLoader)!!
        uri = parcel.readParcelable(Uri::class.java.classLoader)
        takePictureIntent = parcel.readParcelable(Intent::class.java.classLoader)!!
    }

    @SuppressLint("RtlHardcoded")
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GALLERY_IMAGE -> {
                uri = data?.data
                if (uri != null) {
                    val path = getImagePathFromInputStreamUri(this, uri!!)
                    uri = Uri.fromFile(File(path))
                    currentImage.setImageURI(uri)
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if ((resultCode == Activity.RESULT_OK)
                                .and(data != null).or(uri != null) ) {
                    openCrop()
                } else{
                    //Reaching here means that the uri is pointing into an empty file location.
                    uri = null
                    currentImage.setImageResource(R.drawable.common_full_open_on_phone)
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    uri = UCrop.getOutput(data!!)
                    if (uri != null) {
                        // Forcing a refresh of the currentImage by changing the image.
                        currentImage.setImageResource(R.drawable.common_full_open_on_phone)
                        MediaScannerConnection.scanFile(this, listOf(uri?.path).toTypedArray(), listOf("image/jpeg").toTypedArray(), null)
                        currentImage.setImageURI(uri)
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    throw UCrop.getError(data!!)!!
                }
            }
            PLACE_AUTOCOMPLETE_REQUEST_CODE -> {
                if(resultCode == RESULT_OK) {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    _placesLocation = "${place.address}"
                    val locationField = _alertDialog.findViewById<TextView>(R.id.google_location_name)
                    locationField.text = _placesLocation
                    locationField.gravity = Gravity.LEFT
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when {
                it.itemId == R.id.btn_camera -> openCamera()
                it.itemId == R.id.btn_gallery -> openGallery()
                it.itemId == R.id.btn_crop -> openCrop()
                it.itemId == R.id.btn_back -> backToProfile()
            }
        }
        return true
    }

    private fun backToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("user",_user.getHashDetails())
        startActivity(intent)
    }


    private fun uploadImage() {
        if (uri != null){
            val progressBar : ProgressBar = findViewById(R.id.progressBar)
            progressBar.indeterminateDrawable.setColorFilter(Color.DKGRAY, android.graphics.PorterDuff.Mode.MULTIPLY)
            progressBar.visibility = View.VISIBLE  //To show ProgressBar
            val database : String = if (_user.getPermission().toLowerCase() == User.BASIC_PERMISSION) FirebaseDB.TEMP_IMAGES else FirebaseDB.VERIFIED_IMAGES
            _imageHashPath = "${_user.getUserName()}_${UUID.randomUUID()}"
            val ref = _storageReference.child(
                    "${FirebaseDB.IMAGES_DB}/" +
                            "$database/" +
                            _imageHashPath)
            ref.putFile(uri!!)
                    .addOnSuccessListener {
                        progressBar.visibility = View.GONE     // To Hide ProgressBar
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed to Upload", Toast.LENGTH_SHORT).show()

                    }
                    .addOnProgressListener{
                        progressBar.progress = (100.0*it.bytesTransferred/it.totalByteCount).toInt()
                    }
        }
        else{ Toast.makeText(this,"In order to upload you need to first pick a picture", Toast.LENGTH_SHORT).show() }
    }

    private fun openCrop(){
        try {
            //First we check if there was a picture picked and the wanted crop isn't on the filler.
            if (uri == null) {
              Toast.makeText(this,"In order to crop you need to first pick a picture", Toast.LENGTH_SHORT).show()
            }
            else {
                UCrop.of(uri!!, uri!!)
                        .withAspectRatio(16.toFloat(), 9.toFloat())
                        .start(this)
            }
        }
        catch (exception : ActivityNotFoundException){
            Toast.makeText(this, "couldn't crop", Toast.LENGTH_SHORT  ).show()
        }
    }

    private fun openCamera() {
        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = File(Environment.getExternalStorageDirectory(),
                "chord_${System.currentTimeMillis()}.jpg")

        uri = Uri.fromFile(file)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        takePictureIntent.putExtra("return-data", true)
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun openGallery(){
        val gallery = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        gallery.type = "image/*"
        startActivityForResult(Intent.createChooser(gallery, "Select Image from the gallery"), REQUEST_GALLERY_IMAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty()).and(grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, "Permission Canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(cropIntent, flags)
        parcel.writeParcelable(uri, flags)
        parcel.writeParcelable(takePictureIntent, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuActivity> {
        override fun createFromParcel(parcel: Parcel): MenuActivity {
            return MenuActivity(parcel)
        }

        override fun newArray(size: Int): Array<MenuActivity?> {
            return arrayOfNulls(size)
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {}
}