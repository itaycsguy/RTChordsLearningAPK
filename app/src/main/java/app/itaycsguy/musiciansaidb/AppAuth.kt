package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AppAuth(act : AppCompatActivity,fbDb : FirebaseDB) : TextWatcher {
    private val REQUEST_CODE = 0
    private val _act : StartActivity = act as StartActivity
    private val _fbDb = fbDb
    private lateinit var _signInResult : HashMap<String,String>


    fun initOperations() {
        _act.findViewById<TextView>(R.id.text_welcome_email).addTextChangedListener(this)
        _act.findViewById<TextView>(R.id.text_welcome_password).addTextChangedListener(this)

        _act.findViewById<Button>(R.id.sign_in_welcome_button).setOnClickListener {
            val email =_act.findViewById<TextView>(R.id.text_welcome_email).text.toString()
            val password = _act.findViewById<TextView>(R.id.text_welcome_password).text.toString()
            validOnStart(email, password)
        }
        (_act.findViewById<Button>(R.id.sign_up_welcome_button)).setOnClickListener { _act.showSignUp() }
        (_act.findViewById<Button>(R.id.forgot_welcome_button)).setOnClickListener { _act.showRecovery() }
    }

    override fun afterTextChanged(p0: Editable?) {
        p0?.let {
            // check spelling
            Toast.makeText(_act, p0, Toast.LENGTH_LONG).show()
        }
    }

    // not relevant but exist
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    fun getUserData() : HashMap<String,String> {
        return _signInResult
    }

    fun getReqCode() : Int {
        return REQUEST_CODE
    }

    fun validOnStart(email: String, password: String) {
        if(isCorrectEmailFormat(email) && isValidPassword(password)) {
            checkExistAccount(email,password)
        } else {
            Toast.makeText(_act, "Invalid email/password that provided.", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkExistAccount(email: String,password: String) {
        (_fbDb.getRef())!!.child("users/${FirebaseDB.encodeUserEmail(email)}").ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists() && p0.child("authentication_vendor").value == "app") {
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
                        val intent = _act.intent
                        intent.putExtra("data",map)
                        _act.onActivityResultWrapper(REQUEST_CODE,intent)
                    }
                } else { Toast.makeText(_act, "The account does not exist!", Toast.LENGTH_LONG).show() }
            }

            override fun onCancelled(p0: DatabaseError) { Toast.makeText(_act, "Data corruption!", Toast.LENGTH_LONG).show() }
        })
    }

    fun handleResults(data : Intent?){
        if(data!!.hasExtra("data")) _signInResult = data.getSerializableExtra("data") as HashMap<String, String>
    }
}