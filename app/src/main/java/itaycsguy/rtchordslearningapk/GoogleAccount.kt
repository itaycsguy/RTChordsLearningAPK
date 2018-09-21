package itaycsguy.rtchordslearningapk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.database.*


class GoogleAccount(currentActivity : AppCompatActivity) :  GoogleApiClient.OnConnectionFailedListener , View.OnClickListener {
    val SIGNED_IN : Int = 0
    val REQ_CODE = 201
    private val _currentActivity : AppCompatActivity = currentActivity
    private val _signInOptions : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(currentActivity.getString(R.string.request_client_id)).requestEmail().build()
    private val _googleApiClient : GoogleApiClient = GoogleApiClient.Builder(currentActivity).enableAutoManage(currentActivity,this).addApi(Auth.GOOGLE_SIGN_IN_API,this._signInOptions).build()
    private lateinit var _user : User
    private lateinit var _signInResult : GoogleSignInResult
    private var _googleButton : SignInButton = currentActivity.findViewById(R.id.google_sign_in_welcome_button) as SignInButton
    private val _database : FirebaseDatabase = FirebaseDatabase.getInstance()
    init {
        this._googleButton.setOnClickListener(this)
    }

    fun getGoogleResult() : GoogleSignInResult {
        return this._signInResult
    }

    fun setGoogleResult(signInResult : GoogleSignInResult) {
        this._signInResult = signInResult
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.google_sign_in_welcome_button -> {
                this.signIn()
            }
        }
    }

    fun signIn() {
        val indent = Auth.GoogleSignInApi.getSignInIntent(this._googleApiClient)
        this._currentActivity.startActivityForResult(indent,this.REQ_CODE)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun handleResults(data : Intent?) {
        val result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        this.setGoogleResult(result)
        if(result.isSuccess()) {
            val account = (result.getSignInAccount() as GoogleSignInAccount)
            val map : HashMap<String,String> = HashMap()
            map.put("user_name",account.displayName.toString())
            map.put("email",account.email.toString())
            map.put("photo",account.photoUrl.toString())
            map.put("given_name",account.givenName.toString())
            map.put("family_name",account.familyName.toString())
            this._user = User(map)
        }
    }

    fun updateUI(login_status : Int) {
        val text : String
        when(login_status){
            this.SIGNED_IN -> {
                text = "Successfully Signed-In!"
                Toast.makeText(this._currentActivity, text, Toast.LENGTH_LONG).show()
                moveActivities(ProfileActivity())
                }
        }
    }

    fun moveActivities(newActivity : AppCompatActivity) {
        val intent = Intent(this._currentActivity, newActivity::class.java)
        // next activity issues:
        intent.putExtra("authentication_vendor","google")
        intent.putExtra("user_name",this._user.getUserName())
        intent.putExtra("email",this._user.getEmail())
        intent.putExtra("photo",this._user.getPhoto())
        intent.putExtra("given_name",this._user.getGivenName())
        intent.putExtra("family_name",this._user.getFamilyName())

        // DB issues:
        val dbPath : String = "users/" + this._user.getEmail().trim().replace("@","_").replace(".","_")
        val map : HashMap<String,String> = HashMap()
        map.put("authentication_vendor","google")
        map.put("user_name",this._user.getUserName())
        map.put("photo",this._user.getPhoto())
        map.put("given_name",this._user.getGivenName())
        map.put("family_name",this._user.getFamilyName())
        this.writeDB(dbPath,map)

        this._currentActivity.startActivity(intent)
    }

    fun writeDB(dbPath : String,map : HashMap<String,String>) {
        val userEntry = this._database.getReference(dbPath.trim())
        userEntry.setValue(map)
    }
}