package app.itaycsguy.musiciansaidb

import android.graphics.Color
import android.inputmethodservice.Keyboard
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.app.Activity
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast


class UserRecovery(act : AppCompatActivity,fbAuth : FirebaseAuth) : TextWatcher {
    private val REQUESTCODE = 6
    private val _act : StartActivity = act as StartActivity
    private val _fbAuth = fbAuth

    fun initOperations() {
        _act.findViewById<EditText>(R.id.text_email_recovery).addTextChangedListener(this)
        _act.findViewById<EditText>(R.id.text_email_recovery).requestFocus()
        (_act.findViewById<Button>(R.id.send_recovery_button)).setOnClickListener {
            val emailAddrText = (_act.findViewById<EditText>(R.id.text_email_recovery)).text.toString()
            if(emailAddrText.isNotEmpty()) {
                hideKeyboard(_act)
                this.sendEmail(emailAddrText)
            } else { CustomSnackBar.make(_act,  "Email address is missing!") }
        }
        (_act.findViewById<Button>(R.id.cancel_recovery_button)).setOnClickListener { _act.showLogin() }
    }

    fun getReqCode() : Int {
        return REQUESTCODE
    }

    // not relevant but exist
    override fun afterTextChanged(p0: Editable?) {}
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    private fun sendEmail(email: String) {
        val progressBar = startProgressBar(_act,R.id.recovery_progressBar)
        _fbAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    task ->
                    // TODO: need to callback this method for update his new password determination
                    if (task.isSuccessful) {
                        CustomSnackBar.make(_act,   "Email has been sent already.. Check your Inbox!,${task.result}")
                        stopProgressBar(progressBar)
                        _act.showLogin()
                    }
                    else {
                        CustomSnackBar.make(_act,   "Had failure by sending.")
                        stopProgressBar(progressBar)
                    }
                }
    }
}