package itaycsguy.rtchordslearningapk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class LoginActivity : AppCompatActivity() {
    val SIGN_UP_RET_CODE = 0
    val FORGOT_RET_CODE = 0
    // google
    private lateinit var _googleAccount : GoogleAccount
    // firebase
    private lateinit var _firebase : Firebase
    // local
    private lateinit var _localAuthenticator : LocalAuthenticator
    private lateinit var _emailField : EditText
    private lateinit var _passwordField : EditText
    private lateinit var _localSignInButton: Button
    private lateinit var _localSignUpButton: Button
    private lateinit var _localForgotButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        // page building
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // initialize variables
        this._localSignInButton = findViewById(R.id.sign_in_welcome_button)
        this._localSignUpButton = findViewById(R.id.sign_up_welcome_button)
        this._localForgotButton = findViewById(R.id.forgot_welcome_button)
        // variables
        this._emailField = findViewById(R.id.text_welcome_email)
        this._passwordField = findViewById(R.id.text_welcome_password)
        this.fillDetails(this.intent)
        // firebase
        this._firebase = Firebase()
        // google
        this._googleAccount = GoogleAccount(this)
        // local
        this._localAuthenticator = LocalAuthenticator()

        this._localSignInButton.setOnClickListener {
            this._localAuthenticator.validateInputs(this,this._emailField.text.toString(), this._passwordField.text.toString())
        }
        this._localSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            this.startActivityForResult(intent,this.SIGN_UP_RET_CODE)
        }
        this._localForgotButton.setOnClickListener {
            val intent = Intent(this, LoginRecoveryActivity::class.java)
            this.startActivityForResult(intent,this.FORGOT_RET_CODE)
        }
    }

    private fun fillDetails(savedInstanceState : Intent) {
        val email = savedInstanceState.getStringExtra("email")
        if(email != null) {
            this._emailField.append(email.toString())
        }
        val password = savedInstanceState.getStringExtra("password")
        if(password != null) {
            this._passwordField.append(password.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == this._googleAccount.REQ_CODE){
            this._googleAccount.handleResults(data)
            this._firebase.connectByGoogle(this._googleAccount.getGoogleResult().signInAccount!!,this, ProfileActivity())
            this._googleAccount.updateUI(this._googleAccount.SIGNED_IN)
        } else if(requestCode == this.SIGN_UP_RET_CODE) {
            this._firebase.connectByLocal(this._emailField.text.toString(),this._passwordField.text.toString(),this,ProfileActivity())
        } else if(requestCode == this.FORGOT_RET_CODE) {
            val text = "Find your details through your email account!"
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        } else {
            val text = "Some connection error was occur, try again."
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
    }
}