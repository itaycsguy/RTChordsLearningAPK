package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView


class ProfileActivity : AppCompatActivity() {
    private val _fbAuth : FirebaseAuth = FirebaseAuth(this)
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
        findViewById<Button>(R.id.continue_profile_button).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("user",_user.getHashDetails())
            startActivity(intent)
        }
        findViewById<Button>(R.id.sign_out_profile_button).setOnClickListener {
            _fbAuth.disconnect()
            val intent = Intent(this, StartActivity::class.java)
            intent.putExtra("user",_user.getHashDetails())
            startActivity(intent)
        }
    }
}