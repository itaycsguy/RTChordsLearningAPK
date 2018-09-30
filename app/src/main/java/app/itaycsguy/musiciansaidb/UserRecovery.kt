package app.itaycsguy.musiciansaidb

import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class UserRecovery(act : AppCompatActivity) {
    private val REQUEST_CODE = 6
    private val _act : StartActivity = act as StartActivity

    fun initOperations() {
        _act.findViewById<EditText>(R.id.text_email_recovery).requestFocus()
        (_act.findViewById<Button>(R.id.send_recovery_button)).setOnClickListener {
            val emailAddrText = (_act.findViewById<EditText>(R.id.text_email_recovery)).text.toString()
            if(emailAddrText.isNotEmpty()) {
                if(this.sendEmail(emailAddrText)){
                    Toast.makeText(_act, "Email has been sent!", Toast.LENGTH_LONG).show()
                    _act.showLogin()
                }
            } else { Toast.makeText(_act, "Missing email address.", Toast.LENGTH_LONG).show() }
        }
        (_act.findViewById<Button>(R.id.cancel_recovery_button)).setOnClickListener { _act.showLogin() }
    }

    fun getReqCode() : Int {
        return REQUEST_CODE
    }

    private fun sendEmail(email: String) : Boolean {
        return true
    }
}