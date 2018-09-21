package itaycsguy.rtchordslearningapk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginRecoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_recovery)

        val sendButton : Button = findViewById(R.id.send_recovery_button)
        sendButton.setOnClickListener {
            val emailAddrText = (findViewById(R.id.text_email_recovery) as EditText).text.toString()
            if(emailAddrText.isNotEmpty()) {
                this.sendEmail(emailAddrText)
                val text : String = "An email is send to " + emailAddrText + "!\nCheck your email inbox."
                Toast.makeText(this@LoginRecoveryActivity, text, Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val text = "Missing email address."
                Toast.makeText(this@LoginRecoveryActivity, text, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendEmail(addr: String){

    }
}