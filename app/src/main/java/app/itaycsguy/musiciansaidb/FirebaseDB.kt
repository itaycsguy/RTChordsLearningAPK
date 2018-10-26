package app.itaycsguy.musiciansaidb

import android.widget.ImageView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDB {
    private val _firebaseDB : FirebaseDatabase = FirebaseDatabase.getInstance()
    companion object {
        val TEMP_IMAGES = "temp_images"
        val VERIFIED_IMAGES = "verified_images"
        val IMAGES_DB = "Images_Database"

        fun encodeUserEmail(email : String) : String {
            val emailParts : List<String> = email.split("@")
            val secondPart = emailParts[1].replace(".","_")
            return emailParts[0] + "_" + secondPart
        }

        fun decodeUserEmail(email : String) : String {
            val emailParts : List<String> = email.split("_")
            val secondPart = emailParts[1].replace("_",".")
            return emailParts[0] + "@" + secondPart
        }
    }

    fun getRef(path : String? = null) : DatabaseReference? {
        if(path == null) {
            return _firebaseDB.reference
        }
        return _firebaseDB.getReference(path)
    }

    fun writeUser(email : String, map : HashMap<String,String>){
        val userEntry = _firebaseDB.getReference("users/${encodeUserEmail(email)}")
        userEntry.setValue(map)
    }

    fun writeTempImagesMetadata(key : String, map : HashMap<String,String>){
        val tempImgsMetadataEntry = _firebaseDB.getReference("temp_images_metadata/$key")
        tempImgsMetadataEntry.setValue(map)
    }

    fun writeVerifiedImagesMetadata(key : String, map : HashMap<String,String>){
        val verifiedImgsMetadataEntry = _firebaseDB.getReference("verified_images_metadata/$key")
        verifiedImgsMetadataEntry.setValue(map)
    }

    fun writeTempImg(img : ImageView){
        val userEntry = _firebaseDB.getReference("temp_img")
//        System.err.println("the drawable object looks like this: ${img.drawable}")
//        userEntry.setValue(img.drawable)
    }

    fun writeImg(img : ImageView){
        val userEntry = _firebaseDB.getReference("verified_img")
        userEntry.setValue(img.drawable)
    }
}