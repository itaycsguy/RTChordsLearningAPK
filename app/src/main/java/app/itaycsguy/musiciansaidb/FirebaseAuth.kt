package app.itaycsguy.musiciansaidb

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class FirebaseAuth(act : AppCompatActivity) {
    private val _act : AppCompatActivity = act
    private val _fbAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var _gso : GoogleSignInOptions? = null

    fun getInstance() : FirebaseAuth{
        return _fbAuth
    }

    fun connect() : Boolean {
        return try {
            if(_gso != null){
                return true
            }
            _gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(R.string.default_web_client_id.toString()).requestEmail().build()
            CustomSnackBar.make(_act,"Connected to firebase!")
            true
        } catch(e : java.lang.Exception){
            CustomSnackBar.make(_act,"Could not connect to firebase.")
            false
        }
    }

    fun connectByGoogleAcct(acct : GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        this._fbAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
            } else {
                throw Exception(task.exception)
            }
        }
    }

    fun connectByAppAcct(email : String, password: String) {
        this._fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(_act as Activity){
            task ->
            if (task.isSuccessful) {  CustomSnackBar.make(_act,  "Successfully Logged-in!") }
            else { CustomSnackBar.make(_act,  "Error in Login action.") }
        }
    }

    /*
    // TODO: Due to we do not using Firebase users' managememt service - to enhance the service we should use it!
    fun disconnect() {
        this._fbAuth.signOut()
    }
    */
}
