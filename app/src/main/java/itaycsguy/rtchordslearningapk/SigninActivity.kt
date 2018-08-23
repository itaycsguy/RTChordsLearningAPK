package itaycsguy.rtchordslearningapk

import android.app.ProgressDialog.show
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.internal.BaseGmsClient
import kotlinx.android.synthetic.main.activity_main.*

class SigninActivity : AppCompatActivity() , View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{
    override fun onConnectionFailed(p0: ConnectionResult) {
        // need still to implement!
    }

    private lateinit var profile_section: ConstraintLayout
    private lateinit var sign_out_button: Button
    private lateinit var sign_in_button:  SignInButton
    private lateinit var email: TextView
    private lateinit var name: TextView
    private lateinit var image_profile: ImageView
    private lateinit var google_api_client: GoogleApiClient
    private var sign_in_options: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
    private val REQ_CODE = 200

    private fun init(){
        this.profile_section = findViewById(R.id.main_layout)
        this.sign_in_button = findViewById(R.id.google_sign_in) as SignInButton
        this.sign_out_button = findViewById(R.id.google_sign_out) as Button
        this.email = findViewById(R.id.text_username) as TextView
        this.sign_in_button.setOnClickListener(this)
        this.sign_out_button.setOnClickListener(this)
        this.google_api_client = GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,this.sign_in_options).build()
//        this.profile_section.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.init()
        val buttonSignIn = findViewById(R.id.sign_in_button) as Button
        buttonSignIn.setOnClickListener {
            val username = (findViewById(R.id.text_username) as EditText).text.toString()
            val password = (findViewById(R.id.text_password) as EditText).text.toString()
            this.local_validate(username,password)
        }
        val buttonSignUp = findViewById(R.id.sign_up_button) as Button
        buttonSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        val forgotButton = findViewById(R.id.forgot_button) as Button
        forgotButton.setOnClickListener {
            val intent = Intent(this, SigninRecoveryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(p0: View) {
//        need to check whether the 'id' of p0 is the same as for google button on the UI
        when(p0.id){
            R.id.google_sign_in -> this.signIn()
            R.id.google_sign_out -> this.signOut()
        }
//        Syntax Example:
//        val text = ""
//        Toast.makeText(this@SigninActivity, text, Toast.LENGTH_SHORT).show()
//        var str : String? = null
//        str?.let { System.err.println("str is not null") }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQ_CODE){
            var results: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            this.handleResults(results)
        }
    }

    fun signIn(){
        var indent = Auth.GoogleSignInApi.getSignInIntent(this.google_api_client)
        startActivityForResult(indent,REQ_CODE)
    }

    fun signOut(){
        Auth.GoogleSignInApi.signOut(this.google_api_client).setResultCallback {
            this.updateUI("sign_out")
            this.moveActivity(SigninActivity())
        }
    }

    fun handleResults(result: GoogleSignInResult) {
        if(result.isSuccess()) {
//            var googe_account = result.getSignInAccount()
//            var account = (googe_account as GoogleSignInAccount)
//            var name = account.displayName
//            var image_url = account.photoUrl
//            var email = account.email
//            this.email.setText(email)
            this.updateUI("sign_in")
        } else {
            this.updateUI("connect_error")
        }
    }

    fun updateUI(login_status:String){
        var text: String
        when(login_status){
            "sign_in" -> {
                text = "Successfully Sign-In!"
                Toast.makeText(this@SigninActivity, text, Toast.LENGTH_SHORT).show()
                this.moveActivity(MenuActivity())
            }
            "sign_out" -> {
                text = "Successfully Sign-Out!"
                Toast.makeText(this@SigninActivity, text, Toast.LENGTH_SHORT).show()
            }
            "connect_error" -> {
            }
            "all_ready_sign_out" -> {
            }
        }
    }

    private fun moveActivity(newActivity: AppCompatActivity){
        val intent = Intent(this, newActivity::class.java)
        startActivity(intent)
    }

    private fun local_validate(username: String,password: String){
        if(this.is_db_confirm(username,password)) {
            this.moveActivity(MenuActivity())
        } else {
            val text = "Wrong username/password. Try again."
            Toast.makeText(this@SigninActivity, text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun is_db_confirm(username: String,password: String):Boolean{
//        return (username == "admin" && password == "admin")
        return true
    }
}
