package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AppAuth(act : AppCompatActivity,fbDb : FirebaseDB) : TextWatcher {
    private val STD_PASSWORD_TYPE = 129 // TODO: need to look for code which is presenting the same style without any kind of coding such as points
    private val REQUEST_CODE = 0
    private val _act : StartActivity = act as StartActivity
    private val _fbDb = fbDb
    private lateinit var _signInResult : HashMap<String,String>


    fun initOperations() {
        _act.findViewById<TextView>(R.id.text_welcome_email).addTextChangedListener(this)
        _act.findViewById<TextView>(R.id.text_welcome_password).addTextChangedListener(this)

        _act.findViewById<Button>(R.id.sign_in_welcome_button).setOnClickListener {
            hideKeyboard(_act)
            val email =_act.findViewById<TextView>(R.id.text_welcome_email).text.toString()
            val password = _act.findViewById<TextView>(R.id.text_welcome_password).text.toString()
            validOnStart(email, password)
        }
        (_act.findViewById<Button>(R.id.sign_up_welcome_button)).setOnClickListener { _act.showSignUp() }
        (_act.findViewById<Button>(R.id.forgot_welcome_button)).setOnClickListener { _act.showRecovery() }
        _act.findViewById<Button>(R.id.welcome_visible_password).setOnClickListener {
            Toast.makeText(_act,"${InputType.TYPE_TEXT_VARIATION_PASSWORD}",Toast.LENGTH_LONG).show()
            val visibleBtn = _act.findViewById<Button>(R.id.welcome_visible_password)
            val realPassword = _act.findViewById<EditText>(R.id.text_welcome_password)
            when(realPassword.inputType) {
                STD_PASSWORD_TYPE -> {
                    visibleBtn.background = _act.getDrawable(R.drawable.invisible)
                    realPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD // TODO: need to find the same style to 129 font code
                }
                InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                    visibleBtn.background = _act.getDrawable(R.drawable.visible)
                    realPassword.inputType = STD_PASSWORD_TYPE
                }
            }
        }
    }

    // not relevant but exist
    override fun afterTextChanged(p0: Editable?) { p0?.let {} }
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
            CustomSnackBar.make(_act,  "Invalid Details are provided!")
        }
    }

    private fun checkExistAccount(email: String,password: String) {
        (_fbDb.getRef())?.let {
            val progressBar = startProgressBar(_act,R.id.login_progressBar)
        it.child("users/${FirebaseDB.encodeUserEmail(email)}").ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists() && p0.child("authentication_vendor").value.toString().toLowerCase() == "app") {
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
                } else {
                    stopProgressBar(progressBar)
                    CustomSnackBar.make(_act,  "Account does not exist!")
                }
            }

            override fun onCancelled(p0: DatabaseError) { CustomSnackBar.make(_act,  "Data corruption!") }
        }) }
    }

    fun handleResults(data : Intent?){
        data?.let{
            if(data.hasExtra("data")) _signInResult = data.getSerializableExtra("data") as HashMap<String, String>
        }
    }
}