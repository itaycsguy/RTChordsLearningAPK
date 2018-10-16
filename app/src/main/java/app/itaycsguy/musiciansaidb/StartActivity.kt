package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast


class StartActivity : AppCompatActivity() {
    private var _currentLayout : Int = R.layout.activity_login
    private lateinit var _firebaseAuth : FirebaseAuth
    private lateinit var _firebaseDb : FirebaseDB
    private lateinit var _googleAccount : GoogleAuth
    private lateinit var _appAuth : AppAuth
    private lateinit var _signApp : SignApp
    private lateinit var _userRecovery : UserRecovery
    private lateinit var _userData : HashMap<String,String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_currentLayout)
        _firebaseAuth = FirebaseAuth(this)
        _firebaseDb = FirebaseDB()
        _googleAccount = GoogleAuth(this)
        _appAuth = AppAuth(this, _firebaseDb)
        _signApp = SignApp(this,_firebaseDb)
        _userRecovery = UserRecovery(this)
        showLogin(isInit = true)
    }

    override fun onStart() {
        super.onStart()
        if(intent.hasExtra("email") && intent.hasExtra("password")){
            _appAuth.validOnStart(intent.getStringExtra("email"),intent.getStringExtra("password"))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(_currentLayout != R.layout.activity_login) {
            showLogin()
        } else {
            finish()
            startActivity(intent)
        }
        Toast.makeText(this,"onBackPressed",Toast.LENGTH_LONG).show()
    }

    fun showRecovery() {
        _currentLayout = R.layout.activity_login_recovery
        setContentView(_currentLayout)
        _userRecovery.initOperations()
    }

    fun showSignUp() {
        _currentLayout = R.layout.activity_signup
        setContentView(_currentLayout)
        _signApp.initOperations()
    }

    fun showLogin(isInit : Boolean = false) {
        _currentLayout = R.layout.activity_login
        setContentView(_currentLayout)
        findViewById<EditText>(R.id.text_welcome_email).requestFocus()
        if(isInit) {
            _appAuth.initOperations()
            _googleAccount.initOperations()
        }
    }

    fun onActivityResultWrapper(reqCode : Int,data : Intent) {
        onActivityResult(reqCode,-1,data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == _googleAccount.getReqCode()) {
            _googleAccount.handleResults(data)
            _firebaseAuth.connectByGoogleAcct(_googleAccount.getGoogleResult().signInAccount!!)
            _userData = _googleAccount.getUserData()
            writeProfileOnTransaction()
        } else if(requestCode == _appAuth.getReqCode()) {
            _appAuth.handleResults(data)
            _userData = _appAuth.getUserData()
            if(_userData.containsKey("email") && _userData.containsKey("password")) {
                _firebaseAuth.connectByAppAcct(_userData["email"].toString(), _userData["password"].toString())
                writeProfileOnTransaction()
            }
        } else if(requestCode == _signApp.getReqCode() || requestCode == _signApp.getReqGalCode() || requestCode == _signApp.getReqCamCode()) {
            _signApp.handleResult(requestCode, data)
            if(requestCode != _signApp.getReqCode()) { return }
            _userData = _signApp.getUserData()
            if(_userData.containsKey("email") && _userData.containsKey("password")) {
                // TODO: put user email and password into fields and try to login for him automatically
            }
        } else if(requestCode == _userRecovery.getReqCode()) {
            // TODO: handle in somewhat way
        } else {
            Toast.makeText(this, "Some connection error was occur, try again.", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeProfileOnTransaction() {
        Toast.makeText(this, "Successfully Signed-In!", Toast.LENGTH_LONG).show()
        _firebaseDb.writeUser(_userData["email"].toString(), _userData)
        _userData.remove("email")
        userProfileActivityOnStart(User(_userData))
    }

    fun userProfileActivityOnStart(user : User){
        Toast.makeText(this, "goto profile activity", Toast.LENGTH_LONG).show()
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("user",user.getHashDetails())
        startActivity(intent)
    }
}
