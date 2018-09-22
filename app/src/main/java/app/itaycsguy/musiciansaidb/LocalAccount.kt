package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LocalAuthenticator {
    private val PASSLENGTH = 8
    private lateinit var _user : User
    private val _database : FirebaseDatabase = FirebaseDatabase.getInstance()

    fun validateInputs(currentActivity : LoginActivity, email: String, password: String) {
        if(this.isCorrectEmailFormat(email) && this.isValidPassword(password)) {
            this.checkExistAccount(currentActivity,email,password)
        } else {
            val text = "Invalid email/password that provided."
            Toast.makeText(currentActivity, text, Toast.LENGTH_LONG).show()
        }
    }

    private fun isCorrectEmailFormat(email: String): Boolean {
        val pattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
        val match : MatchResult? = pattern.toRegex(setOf(RegexOption.IGNORE_CASE,RegexOption.DOT_MATCHES_ALL)).find(email)
        match?.let {
            return true
        }
        return false
    }

    private fun isValidPassword(password: String):Boolean{
        return password.length >= this.PASSLENGTH
    }

    private fun checkExistAccount(currentActivity : LoginActivity,email: String,password: String) {
        val emailParts : List<String> = email.split("@")
        val secondPart = emailParts[1].replace(".","_")
        val newEmail = emailParts[0] + "_" + secondPart
        this._database.reference.child("users/$newEmail").ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists() && p0.child("authentication_vendor").value == "local") {
                    if (p0.child("password").value == password) {

                        val map: HashMap<String, String> = HashMap()
                        map["authentication_vendor"] = p0.child("authentication_vendor").value.toString()
                        map["user_name"] = p0.child("user_name").value.toString()
                        map["email"] = email
                        map["password"] = password
                        map["photo"] = p0.child("photo").value.toString()
                        map["given_name"] = p0.child("given_name").value.toString()
                        map["family_name"] = p0.child("family_name").value.toString()
                        map["permission"] = p0.child("permission").value.toString()
                        _user = User(map)

                        val intent = Intent(currentActivity,ProfileActivity()::class.java)
                        intent.putExtra("authentication_vendor",p0.child("authentication_vendor").value.toString())
                        intent.putExtra("user_name",p0.child("user_name").value.toString())
                        intent.putExtra("email",email)
                        intent.putExtra("password",password)
                        intent.putExtra("photo",p0.child("photo").value.toString())
                        intent.putExtra("given_name",p0.child("given_name").value.toString())
                        intent.putExtra("family_name",p0.child("family_name").value.toString())
                        intent.putExtra("permission",p0.child("permission").value.toString())
                        currentActivity.startActivity(intent)
                    }
                } else {
                    val text = "The account does not exist!"
                    Toast.makeText(currentActivity, text, Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                val text = "Data corruption!"
                Toast.makeText(currentActivity, text, Toast.LENGTH_LONG).show()
            }
        })
    }
}