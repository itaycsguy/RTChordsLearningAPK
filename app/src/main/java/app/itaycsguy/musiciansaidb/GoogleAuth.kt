package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import app.itaycsguy.musiciansaidb.R.id.progressBar
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


class GoogleAuth(act : AppCompatActivity) : GoogleApiClient.OnConnectionFailedListener {
    private val REQUESTCODE = 1
    private val _act : AppCompatActivity = act
    private val _signInOptions : GoogleSignInOptions
    private val _googleApiClient : GoogleApiClient
    private lateinit var _signInResult : GoogleSignInResult
    private lateinit var _gBtn : SignInButton
    private lateinit var _userData : HashMap<String,String>

    init {
        _signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(_act.getString(R.string.request_client_id))
                .requestEmail()
                .build()
        _googleApiClient = GoogleApiClient.Builder(_act)
                .enableAutoManage(_act,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,_signInOptions)
                .build()
    }

    fun initOperations() {
        _gBtn = _act.findViewById(R.id.google_sign_in_welcome_button)
        _gBtn.setOnClickListener {
            val progressBar = startProgressBar(_act,R.id.login_progressBar)
            signIn()
        }
    }

    fun getUserData() : HashMap<String,String> {
        return _userData
    }

    fun getReqCode() : Int {
        return REQUESTCODE
    }

    fun getGoogleResult() : GoogleSignInResult {
        return _signInResult
    }

    private fun signIn() {
        val indent = Auth.GoogleSignInApi.getSignInIntent(_googleApiClient)
        _act.startActivityForResult(indent,REQUESTCODE)
    }

    fun handleResults(data : Intent?) {
        val result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        _signInResult = result
        if(result.isSuccess) {
            val account = (result.signInAccount as GoogleSignInAccount) // TODO: need to understand how to check if google image is uploaded by the user or picked randomly by the provider
            val map : HashMap<String,String> = HashMap()
            map["user_name"] = account.displayName.toString()
            val email = account.email.toString()
            map["email"] = email
            map["photo"] = account.photoUrl.toString()
            map["given_name"] = account.givenName.toString()
            map["family_name"] = account.familyName.toString()
            map["authentication_vendor"] = "Google"
            val ref = FirebaseDB().getRef()
            ref?.let {
                // TODO: change it to check the permission on fetching data time:
                val permissionValue = it
                        .child("users/${FirebaseDB.encodeUserEmail(email)}/permission")
                        .equalTo("Anonymous").toString()
                Toast.makeText(_act, permissionValue, Toast.LENGTH_LONG).show()
                if (permissionValue.isEmpty()) {
                    map["permission"] = "Anonymous"
                }
            }
            _userData = map
//            TODO: make this changing asynchronous:
//            (FirebaseDB().getRef())!!
//                    .child("users/${FirebaseDB.encodeUserEmail(email)}").addListenerForSingleValueEvent(object : ValueEventListener {
//                    .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onCancelled(p0: DatabaseError) {}
//
//                override fun onDataChange(p0: DataSnapshot) {
//                    if(p0.exists()) {
//                        map["permission"] = p0.value.toString()
//                    } else {
//                        map["permission"] = "Anonymous"
//                        _userData = map
//                    }
//                }
//
//            })
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) { CustomSnackBar.make(_act,"Google connecting tunnel is corrupted!") }
}