package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import java.io.Serializable

class StartActivity : AppCompatActivity(), Serializable {
    private var _currLayout : Int = R.layout.activity_login
    private lateinit var _fbAuth : FirebaseAuth
    private lateinit var _fbDb : FirebaseDB
    private lateinit var _gAcct : GoogleAuth
    private lateinit var _aAuth : AppAuth
    private lateinit var _signApp : SignApp
    private lateinit var _userRecovery : UserRecovery
    private lateinit var _userData : HashMap<String,String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_currLayout)
        _fbAuth = FirebaseAuth(this)
        _fbDb = FirebaseDB()
        _gAcct = GoogleAuth(this)
        _aAuth = AppAuth(this, _fbDb)
        _signApp = SignApp(this,_fbDb)
        _userRecovery = UserRecovery(this,_fbAuth)
        showLogin()
        _fbAuth.connect()
    }

    override fun onStart() {
        super.onStart()
        if(intent.hasExtra("email") && intent.hasExtra("password")){
            _aAuth.validOnStart(intent.getStringExtra("email"),intent.getStringExtra("password"))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(_currLayout != R.layout.activity_login) {
            showLogin()
        } else {
            finish()
            startActivity(intent)
        }
    }

    fun showRecovery() {
        val progressBar = startProgressBar(this,R.id.login_progressBar)
        _currLayout = R.layout.activity_login_recovery
        setContentView(_currLayout)
        _userRecovery.initOperations()
        stopProgressBar(progressBar)
    }

    fun showSignUp() {
        val progressBar = startProgressBar(this,R.id.login_progressBar)
        _currLayout = R.layout.activity_signup
        setContentView(_currLayout)
        _signApp.initOperations()
        stopProgressBar(progressBar)
    }

    fun showLogin() {
        _currLayout = R.layout.activity_login
        setContentView(_currLayout)
        val email = findViewById<EditText>(R.id.text_welcome_email)
        email.requestFocus()
        email.setBackgroundColor(Color.WHITE)
        val password = findViewById<EditText>(R.id.text_welcome_password)
        password.setBackgroundColor(Color.WHITE)
        _aAuth.initOperations()
        _gAcct.initOperations()
    }

    fun onActivityResultWrapper(reqCode : Int,data : Intent) {
        onActivityResult(reqCode,-1,data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(_fbAuth.connect() && requestCode == _gAcct.getReqCode()) {
            _gAcct.handleResults(data)
            _fbAuth.connectByGoogleAcct(_gAcct.getGoogleResult().signInAccount!!)
            _userData = _gAcct.getUserData()
            writeProfileOnTransaction()
        } else if(_fbAuth.connect() && requestCode == _aAuth.getReqCode()) {
            _aAuth.handleResults(data)
            _userData = _aAuth.getUserData()
            if(_userData.containsKey("email") && _userData.containsKey("password")) {
                _fbAuth.connectByAppAcct(_userData["email"].toString(), _userData["password"].toString())
                findViewById<EditText>(R.id.text_welcome_email).setText(_userData["email"])
                findViewById<EditText>(R.id.text_welcome_password).setText(_userData["password"])
                writeProfileOnTransaction()
            }
        } else if(_fbAuth.connect() &&
                (requestCode == _signApp.getReqCode() || requestCode == _signApp.getReqGalCode() || requestCode == _signApp.getReqCamCode())) {
            _signApp.handleResult(requestCode, data)
            if(requestCode != _signApp.getReqCode()) { return }
            _userData = _signApp.getUserData()
            if(_userData.containsKey("email") && _userData.containsKey("password")) {
                showLogin()
                findViewById<EditText>(R.id.text_welcome_email).setText(_userData["email"])
                findViewById<EditText>(R.id.text_welcome_password).setText(_userData["password"])
                writeProfileOnTransaction()
            }
        } else if(_fbAuth.connect() && requestCode == _userRecovery.getReqCode()) {
            CustomSnackBar.make(this, "Waiting to recovery email...")
        }
    }

    private fun writeProfileOnTransaction() {
        CustomSnackBar.make(this, "Successfully Signed-In!")
        _fbDb.writeUser(_userData["email"].toString(), _userData)
        userProfileActivityOnStart(User(_userData))
    }

    private fun userProfileActivityOnStart(user : User){
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("user",user.getHashDetails())
        findViewById<ProgressBar>(R.id.login_progressBar).visibility = View.INVISIBLE
        startActivity(intent)
    }
}
