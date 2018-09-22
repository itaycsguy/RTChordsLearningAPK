package app.itaycsguy.musiciansaidb

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


@SuppressLint("Registered")
class LoginRecoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_recovery)

        val sendButton : Button = findViewById(R.id.send_recovery_button)
        sendButton.requestFocus()
        sendButton.setOnClickListener {
            val emailAddrText = (findViewById<EditText>(R.id.text_email_recovery)).text.toString()
            if(emailAddrText.isNotEmpty()) {
                if(this.sendEmail(emailAddrText)) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            } else {
                val text = "Missing email address."
                Toast.makeText(this@LoginRecoveryActivity, text, Toast.LENGTH_LONG).show()
            }
        }
        val cancelButton : Button = findViewById(R.id.cancel_recovery_button)
        cancelButton.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    private fun sendEmail(email: String) : Boolean{
        if(this.isCorrectEmailFormat(email)) {
            val success = EmailIntentBuilder.from(this)
                    .to(email)
                    .subject("MusiciansAIDB Recovery")
                    .body("")
                    .start()
            if (success) {
                val text = "A recovery email has been sent to $email!\nCheck your email inbox."
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                return true
            }
            val text = "Failed to send a recovery email."
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
        val text = "Wrong email string format, try again."
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        return false
    }

    private fun isCorrectEmailFormat(email: String): Boolean {
        val pattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
        val match : MatchResult? = pattern.toRegex(setOf(RegexOption.IGNORE_CASE,RegexOption.DOT_MATCHES_ALL)).find(email)
        match?.let {
            return true
        }
        return false
    }

}