package itaycsguy.rtchordslearningapk

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
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var givenName: String
    private lateinit var familyName: String
    private var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this._authenticationVendor = this.intent.getStringExtra("authentication_vendor")
        this.name = this.intent.getStringExtra("user_name")
        if(this._authenticationVendor == "google") {
            this.email = this.intent.getStringExtra("email")
            this.image = Uri.parse(this.intent.getStringExtra("photo"))
            this.givenName = this.intent.getStringExtra("given_name")
            this.familyName = this.intent.getStringExtra("family_name")
        }
        val localImage = findViewById<ImageView>(R.id.my_profile_photo)
        if(this.image != null && this.image != Uri.EMPTY && this.image != Uri.parse("null")) {
            localImage.setImageURI(this.image)
        }
        findViewById<TextView>(R.id.profile_username).append(" ${this.name}")
        findViewById<TextView>(R.id.profile_email).append(" ${this.email}")
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