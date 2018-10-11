package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast


class StartActivity : AppCompatActivity() {
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
        showLogin(isInit = true)
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
        Toast.makeText(this,"onBackPressed",Toast.LENGTH_LONG).show()
    }

    fun showRecovery() {
        _currLayout = R.layout.activity_login_recovery
        setContentView(_currLayout)
        _userRecovery.initOperations()
    }

    fun showSignUp() {
        _currLayout = R.layout.activity_signup
        setContentView(_currLayout)
        _signApp.initOperations()
    }

    fun showLogin(isInit : Boolean = false) {
        _currLayout = R.layout.activity_login
        setContentView(_currLayout)
        findViewById<EditText>(R.id.text_welcome_email).requestFocus()
        if(isInit) {
            _aAuth.initOperations()
            _gAcct.initOperations()
        }
    }

    fun onActivityResultWrapper(reqCode : Int,data : Intent) {
        onActivityResult(reqCode,-1,data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == _gAcct.getReqCode()) {
            _gAcct.handleResults(data)
            _fbAuth.connectByGoogleAcct(_gAcct.getGoogleResult().signInAccount!!)
            _userData = _gAcct.getUserData()
            writeProfileOnTransaction()
        } else if(requestCode == _aAuth.getReqCode()) {
            _aAuth.handleResults(data)
            _userData = _aAuth.getUserData()
            if(_userData.containsKey("email") && _userData.containsKey("password")) {
                _fbAuth.connectByAppAcct(_userData["email"].toString(), _userData["password"].toString())
                findViewById<EditText>(R.id.text_welcome_email).setText(_userData["email"])
                findViewById<EditText>(R.id.text_welcome_password).setText(_userData["password"])
                writeProfileOnTransaction()
            }
        } else if(requestCode == _signApp.getReqCode() || requestCode == _signApp.getReqGalCode() || requestCode == _signApp.getReqCamCode()) {
            _signApp.handleResult(requestCode, data)
            if(requestCode != _signApp.getReqCode()) { return }
            _userData = _signApp.getUserData()
            if(_userData.containsKey("email") && _userData.containsKey("password")) {
                showLogin()
                findViewById<EditText>(R.id.text_welcome_email).setText(_userData["email"])
                findViewById<EditText>(R.id.text_welcome_password).setText(_userData["password"])
                writeProfileOnTransaction()
            }
        } else if(requestCode == _userRecovery.getReqCode()) {
            Toast.makeText(this, "waiting to recovery email...", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Some connection error was occur, try again.", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeProfileOnTransaction() {
        Toast.makeText(this, "Successfully Signed-In!", Toast.LENGTH_LONG).show()
        _fbDb.writeUser(_userData["email"].toString(), _userData)
        userProfileActivityOnStart(User(_userData))
    }

    fun userProfileActivityOnStart(user : User){
        Toast.makeText(this, "goto profile activity", Toast.LENGTH_LONG).show()
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("user",user.getHashDetails())
        startActivity(intent)
    }
}
