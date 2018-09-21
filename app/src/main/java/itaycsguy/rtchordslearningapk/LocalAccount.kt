package itaycsguy.rtchordslearningapk

import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase


class LocalAuthenticator {
    private val PASS_LENGTH = 8
    private lateinit var _user : User
    private val _database : FirebaseDatabase = FirebaseDatabase.getInstance()

    fun validateInputs(currentActivity : LoginActivity, email: String, password: String) {
        if(this.isValidEmail(email) && this.isValidPassword(password)) {
            val map: HashMap<String, String> = HashMap()
            map.put("email", email)
            map.put("password", password)
            this._user = User(map)
        } else {
            val text = "Invalid email/password that provided."
            Toast.makeText(currentActivity, text, Toast.LENGTH_LONG).show()
        }
    }

    private fun isValidPassword(password: String):Boolean{
        return password.length >= this.PASS_LENGTH
    }

    private fun isValidEmail(email: String):Boolean{
        val usersEntry = this._database.getReference("users")
        if(usersEntry.child(email).key!!.isEmpty() && this.isCorrectEmailFormat(email)) {
            return true
        }
        return false
    }

    fun isCorrectEmailFormat(email: String): Boolean {
        val pattern = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$".toRegex()
        val matchResult = pattern.matchEntire(email)
        return matchResult == null
    }
}