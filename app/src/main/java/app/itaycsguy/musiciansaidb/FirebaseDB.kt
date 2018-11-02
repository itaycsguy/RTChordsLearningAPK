package app.itaycsguy.musiciansaidb

import com.google.firebase.database.*

class FirebaseDB {
    private val _firebaseDB : FirebaseDatabase = FirebaseDatabase.getInstance()
    companion object {
        val TEMP_IMAGES = "temp_images"
        val VERIFIED_IMAGES = "verified_images"
        val IMAGES_DB = "Images_Database"

        fun encodeUserEmail(email : String) : String {
            val emailParts : List<String> = email.split("@")
            val firstPart = emailParts[0].replace(".","_")
            val secondPart = emailParts[1].replace(".","_")
            return firstPart + "_" + secondPart
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
}