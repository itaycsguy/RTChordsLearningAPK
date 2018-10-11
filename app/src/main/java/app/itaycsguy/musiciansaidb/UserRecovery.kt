package app.itaycsguy.musiciansaidb

import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class UserRecovery(act : AppCompatActivity,fbAuth : FirebaseAuth) : TextWatcher {
    private val REQUEST_CODE = 6
    private val _act : StartActivity = act as StartActivity
    private val _fbAuth = fbAuth

    fun initOperations() {
        _act.findViewById<EditText>(R.id.text_email_recovery).addTextChangedListener(this)
        _act.findViewById<EditText>(R.id.text_email_recovery).requestFocus()
        (_act.findViewById<Button>(R.id.send_recovery_button)).setOnClickListener {
            val emailAddrText = (_act.findViewById<EditText>(R.id.text_email_recovery)).text.toString()
            if(emailAddrText.isNotEmpty()) {
                this.sendEmail(emailAddrText)
                _act.showLogin()
            } else { Toast.makeText(_act, "Missing email address.", Toast.LENGTH_LONG).show() }
        }
        (_act.findViewById<Button>(R.id.cancel_recovery_button)).setOnClickListener { _act.showLogin() }
    }

    fun getReqCode() : Int {
        return REQUEST_CODE
    }

    override fun afterTextChanged(p0: Editable?) {
        p0?.let {
            // check spelling
        }
    }

    // not relevant but exist
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    private fun sendEmail(email: String) {
        _fbAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    task ->
                    // TODO: need to callback this method for update his new password determination
                    if (task.isSuccessful) { Toast.makeText(_act, "Email has been sent already.. Check you Inbox!", Toast.LENGTH_LONG).show() }
                    else { Toast.makeText(_act, "Email had failure in sending.", Toast.LENGTH_LONG).show() }
                }
    }
}