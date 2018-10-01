package app.itaycsguy.musiciansaidb

import android.widget.ImageView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDB {
    private val _fbDb : FirebaseDatabase = FirebaseDatabase.getInstance()
    companion object {
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
            return _fbDb.reference
        }
        return _fbDb.getReference(path)
    }

    fun writeUser(email : String, map : HashMap<String,String>){
        val userEntry = _fbDb.getReference("users/${encodeUserEmail(email)}")
        userEntry.setValue(map)
    }

    fun writeTempImg(img : ImageView){
        val userEntry = _fbDb.getReference("temp_img")
        userEntry.setValue(img)
    }

    fun writeImg(img : ImageView){
        val userEntry = _fbDb.getReference("verified_img")
        userEntry.setValue(img)
    }
}