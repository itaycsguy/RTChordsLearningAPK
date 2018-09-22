package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast


class ProfileActivity : AppCompatActivity() {
    private val _firebase : Firebase = Firebase()
    private lateinit var _authenticationVendor : String
    private lateinit var _username: String
    private lateinit var _email: String
    private lateinit var _givenName: String
    private lateinit var _familyName: String
    private lateinit var _permission: String
    private var _image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this._authenticationVendor = this.intent.getStringExtra("authentication_vendor")
        this._username = this.intent.getStringExtra("user_name")
        this._email = this.intent.getStringExtra("email")
        this._image = Uri.parse(this.intent.getStringExtra("photo"))
        this._givenName = this.intent.getStringExtra("given_name")
        this._familyName = this.intent.getStringExtra("family_name")
        this._permission = this.intent.getStringExtra("permission")
        val localImage = findViewById<ImageView>(R.id.my_profile_photo)
        if(this._image != null && this._image != Uri.EMPTY && this._image != Uri.parse("null")) {
            localImage.setImageURI(this._image)
        }
        findViewById<TextView>(R.id.profile_username).append(" ${this._username}")
        findViewById<TextView>(R.id.profile_email).append(" ${this._email}")
        findViewById<TextView>(R.id.profile_givenname).append(" ${this._givenName}")
        findViewById<TextView>(R.id.profile_familyname).append(" ${this._familyName}")
        findViewById<TextView>(R.id.profile_authentication_vendor).append(" ${this._authenticationVendor}")
        findViewById<TextView>(R.id.profile_permission).append(" ${this._permission}")
        val continueButton : Button = findViewById(R.id.continue_profile_button)
        continueButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        val signOutButton : Button = findViewById(R.id.sign_out_profile_button)
        signOutButton.setOnClickListener {
            if(this._authenticationVendor == "google") {
                this._firebase.disconnect()
                val text = "Successfully Signed-Out!"
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            } else {
                this._firebase.disconnect()
                val text = "Successfully Signed-Out!"
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}