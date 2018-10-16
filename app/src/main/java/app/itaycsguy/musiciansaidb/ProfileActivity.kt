package app.itaycsguy.musiciansaidb

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.TextView


class ProfileActivity : FragmentActivity() {
    private lateinit var _user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        _user = User(intent.getSerializableExtra("user") as HashMap<String,String>)
        findViewById<TextView>(R.id.profile_username).append(" " + _user.getUserName())
        findViewById<TextView>(R.id.profile_email).append(" " + _user.getEmail())
        findViewById<TextView>(R.id.profile_givenname).append(" " + _user.getGivenName())
        findViewById<TextView>(R.id.profile_familyname).append(" " + _user.getFamilyName())
        findViewById<TextView>(R.id.profile_permission).append(" " + _user.getPermission())
        val image = Uri.parse(_user.getPhoto())
        if (image != null && image != Uri.EMPTY && image != Uri.parse("null")) {
//            findViewById<ImageView>(R.id.my_profile_photo).setImageURI(Uri.parse(_user.getPhoto()))
            // TODO: need to classify between google default photo to uploaded user photo
        }
    }
}