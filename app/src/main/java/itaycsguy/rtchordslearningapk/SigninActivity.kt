package itaycsguy.rtchordslearningapk

import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice.CONNECTED
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.widget.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*


class SigninActivity : AppCompatActivity() , View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{
//    private lateinit var profile_section: ConstraintLayout
    private lateinit var signInButton:  SignInButton
    private lateinit var localSignInButton:  Button
    private lateinit var localSignUpButton:  Button
    private lateinit var localForgotButton: Button
    private lateinit var username: EditText
    private lateinit var text_username: String
    private lateinit var password: EditText
    private lateinit var googleApiClient: GoogleApiClient
    private var googleName: String? = null
    private var googleEmail: String? = null
    private var googleImage: String? = null
    private var signInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
    private val REQ_CODE = 201
    private val SIGNED_IN: Int = 0
    private val SIGNED_OUT: Int = 1
    private val ERROR: Int = 2
    private val CONNECTED: Int = 3

    override fun onConnectionFailed(p0: ConnectionResult) {
        // need still to implement!
    }

    private fun init(){
        this.username = findViewById(R.id.text_username) as EditText
        this.password = findViewById(R.id.text_password) as EditText
        this.localSignInButton = findViewById(R.id.sign_in_button) as Button
        this.localSignUpButton = findViewById(R.id.sign_up_button) as Button
        this.localForgotButton = findViewById(R.id.forgot_button) as Button
        this.signInButton = findViewById(R.id.google_sign_in) as SignInButton
        this.signInButton.setOnClickListener(this)
        this.googleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,this.signInOptions).build()
        this.localSignInButton.setOnClickListener {
            this.text_username = this.username.text.toString()
            this.localValidate(this.username.text.toString(),this.password.text.toString())
        }
        this.localSignUpButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        this.localForgotButton.setOnClickListener {
            val intent = Intent(this, SigninRecoveryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.init()
        if(intent.hasExtra("sign_out")){
            val isSignedOut = intent.getBooleanExtra("sign_out",false)
            if (isSignedOut) {
//                temporary msg:
                this.signOut()
//                this.localSignOut() // need still to implement
            }
        } else if(intent.hasExtra("username") && intent.hasExtra("password")){
            val username = intent.getCharSequenceExtra("username").toString()
            val password = intent.getCharSequenceExtra("password").toString()
            this.fillDetails(username,password)

        }
    }

    private fun fillDetails(username: String,password: String){
        this.username.setText(username)
        this.password.setText(password)
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.google_sign_in -> this.signIn()
//            R.id.google_sign_out -> this.signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQ_CODE){
            val results: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            this.handleResults(results)
        }
    }

    fun signIn(){
        val indent = Auth.GoogleSignInApi.getSignInIntent(this.googleApiClient)
        startActivityForResult(indent,REQ_CODE)
    }

    fun signOut(){
//        Auth.GoogleSignInApi.signOut(this.googleApiClient).setResultCallback {
//            this.updateUI(SIGNED_OUT)
//            this.moveActivity(SigninActivity())
//        }
        this.updateUI(SIGNED_OUT)
    }

    fun handleResults(result: GoogleSignInResult) {
        if(result.isSuccess()) {
            val googe_account = result.getSignInAccount()
            val account = (googe_account as GoogleSignInAccount)
            this.googleName = account.displayName.toString()
            this.googleEmail = account.email.toString()
            this.googleImage = account.photoUrl.toString()
            this.updateUI(SIGNED_IN)
        } else {
            this.updateUI(ERROR)
        }
    }

    fun updateUI(login_status:Int){
        var text: String
        when(login_status){
            SIGNED_IN -> {
                text = "Successfully Signed-In!"
                Toast.makeText(this@SigninActivity, text, Toast.LENGTH_SHORT).show()
                this.moveActivity(ProfileActivity(),true)
            }
            SIGNED_OUT -> {
                text = "Successfully Signed-Out!"
                Toast.makeText(this@SigninActivity, text, Toast.LENGTH_SHORT).show()
            }
            ERROR -> {
            }
            CONNECTED -> {
            }
        }
    }

    private fun moveActivity(newActivity: AppCompatActivity,fromOut:Boolean = false){
        val intent = Intent(this, newActivity::class.java)
        if(fromOut && newActivity is ProfileActivity){
            intent.putExtra("username",this.googleName)
            intent.putExtra("email",this.googleEmail)
            intent.putExtra("image",this.googleImage)
        } else {
            intent.putExtra("username",this.text_username)
            intent.putExtra("email",this.text_username)
            intent.putExtra("image","")
        }
        startActivity(intent)
    }

    private fun localValidate(username: String,password: String){
        if(this.isDbConfirmed(username,password)) {
            this.moveActivity(ProfileActivity(),false)
        } else {
            val text = "Wrong username/password. Try again."
            Toast.makeText(this@SigninActivity, text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isDbConfirmed(username: String,password: String):Boolean{
//        return (username == "admin" && password == "admin")
        return true
    }
}