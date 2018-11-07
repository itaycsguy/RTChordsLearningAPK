package app.itaycsguy.musiciansaidb

import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import app.itaycsguy.musiciansaidb.R.id.progressBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session


@Suppress("NAME_SHADOWING")
class UserRecovery(act : AppCompatActivity, fbDb: FirebaseDB) : TextWatcher {
    private val REQUESTCODE = 6
    private val _act : StartActivity = act as StartActivity
    private val _fbDb = fbDb

    fun initOperations() {
        _act.findViewById<EditText>(R.id.text_email_recovery).addTextChangedListener(this)
        _act.findViewById<EditText>(R.id.text_email_recovery).requestFocus()
        (_act.findViewById<Button>(R.id.send_recovery_button)).setOnClickListener {
            val emailAddrText = (_act.findViewById<EditText>(R.id.text_email_recovery)).text.toString()
            if(emailAddrText.isNotEmpty()) {
                hideKeyboard(_act)
                this.sendEmail(emailAddrText)
            } else {
                _act.findViewById<ProgressBar>(R.id.recovery_progressBar).visibility = View.INVISIBLE
                CustomSnackBar.make(_act,  "Email address is missing!")
            }
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

    /*
    smtp domain: smtp.gmail.com
    smtp email: "musical.lab100@gmail.com"
    smtp password: "@123mlab"
     */
    private fun sendEmail(email : String) {
        try {
            val progressBar = startProgressBar(_act,R.id.recovery_progressBar)
            val fromEmail = "musical.lab100@gmail.com" //requires valid gmail id
            val password = "@123mlab" // correct password for gmail id

            System.out.println("TLSEmail Start")
            val props = Properties()
            props["mail.smtp.host"] = "smtp.gmail.com" //SMTP Host
            props["mail.smtp.port"] = "587" //TLS Port
            props["mail.smtp.auth"] = "true" //enable authentication
            props["mail.smtp.starttls.enable"] = "true" //enable STARTTLS

            //create Authenticator object to pass in Session.getInstance argument
            val auth = object : Authenticator() {
                //override the getPasswordAuthentication method
                 override fun getPasswordAuthentication() : PasswordAuthentication {
                    return PasswordAuthentication(fromEmail, password)
                }
            }
            val session = Session.getInstance(props, auth)
            _fbDb.getRef("users/${FirebaseDB.encodeUserEmail(email)}")?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()) {
                        val provider = p0.child("authentication_vendor").value.toString().toLowerCase()
                        var password = "Gmail account"
                        if (provider == "app") {
                            password = p0.child("password").value.toString()
                        }
                        EmailUtil.sendEmail(session, email, "Musical DB - Password Remainder", "Your password is: $password")
                        CustomSnackBar.make(_act, "Email is sent! Check your mail-box...")
                        stopProgressBar(progressBar)
                        _act.showLogin()
                    } else {
                        CustomSnackBar.make(_act,  "Email address is incorrect/cannot be reached!")
                    }
                    stopProgressBar(progressBar)
                }
            })
        } catch (e: Exception) {
            Log.e("SendEmail", e.message, e)
            throw e
        }

    }
}