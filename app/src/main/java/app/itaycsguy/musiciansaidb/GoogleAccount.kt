package app.itaycsguy.musiciansaidb

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
    val SIGNEDIN : Int = 0
    val REQCODE = 201
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

    private fun setGoogleResult(signInResult : GoogleSignInResult) {
        this._signInResult = signInResult
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.google_sign_in_welcome_button -> {
                this.signIn()
            }
        }
    }

    private fun signIn() {
        val indent = Auth.GoogleSignInApi.getSignInIntent(this._googleApiClient)
        this._currentActivity.startActivityForResult(indent,this.REQCODE)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        val text = "Google connecting tunnel is corrupted!"
        Toast.makeText(this._currentActivity, text, Toast.LENGTH_LONG).show()
    }

    fun handleResults(data : Intent?) {
        val result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        this.setGoogleResult(result)
        if(result.isSuccess) {
            val account = (result.signInAccount as GoogleSignInAccount)
            val map : HashMap<String,String> = HashMap()
            map["user_name"] = account.displayName.toString()
            map["email"] = account.email.toString()
            map["photo"] = account.photoUrl.toString()
            map["given_name"] = account.givenName.toString()
            map["family_name"] = account.familyName.toString()
            this._user = User(map)
        }
    }

    fun updateUI(login_status : Int) {
        val text : String
        when(login_status){
            this.SIGNEDIN -> {
                text = "Successfully Signed-In!"
                Toast.makeText(this._currentActivity, text, Toast.LENGTH_LONG).show()
                moveActivities(ProfileActivity())
                }
        }
    }

    private fun moveActivities(newActivity : AppCompatActivity) {
        val intent = Intent(this._currentActivity, newActivity::class.java)
        // next activity issues:
        intent.putExtra("authentication_vendor","google")
        intent.putExtra("user_name",this._user.getUserName())
        intent.putExtra("email",this._user.getEmail())
        intent.putExtra("photo",this._user.getPhoto())
        intent.putExtra("given_name",this._user.getGivenName())
        intent.putExtra("family_name",this._user.getFamilyName())
        intent.putExtra("photo",this._user.getPhoto())
        intent.putExtra("permission","anonymous")

        // DB issues:
        val dbPath : String = "users/" + this._user.getEmail().trim().replace("@","_").replace(".","_")
        val map : HashMap<String,String> = HashMap()
        map["authentication_vendor"] = "google"
        map["user_name"] = this._user.getUserName()
        map["photo"] = this._user.getPhoto()
        map["given_name"] = this._user.getGivenName()
        map["family_name"] = this._user.getFamilyName()
        map["permission"] = "anonymous"
        this.writeDB(dbPath,map)

        this._currentActivity.startActivity(intent)
    }

    private fun writeDB(dbPath : String, map : HashMap<String,String>) {
        val userEntry = this._database.getReference(dbPath.trim())
        userEntry.setValue(map)
    }
}