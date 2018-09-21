package itaycsguy.rtchordslearningapk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Firebase {
    private val _firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    fun connectByGoogle(acct : GoogleSignInAccount,activitySrc : AppCompatActivity, activityDest : AppCompatActivity) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        this._firebaseAuth.signInWithCredential(credential).addOnCompleteListener(activitySrc) {
            task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                activitySrc.startActivity(Intent(activitySrc,activityDest::class.java))
            } else {
                throw Exception(task.exception)
            }
        }
    }

    fun connectByLocal(email : String, password: String, activitySrc : AppCompatActivity, activityDest : AppCompatActivity) {
        this._firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activitySrc, OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful) {
                activitySrc.startActivity(Intent(activitySrc,activityDest::class.java))
                Toast.makeText(activitySrc, "Successfully Logged in :)", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activitySrc, "Error Logging in :(", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun disconnect() {
        this._firebaseAuth.signOut()
    }
}