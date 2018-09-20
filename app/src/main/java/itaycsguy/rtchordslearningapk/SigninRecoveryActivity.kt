package itaycsguy.rtchordslearningapk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SigninRecoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth)

        val sendButton = findViewById<Button>(R.id.send_button)
        sendButton.setOnClickListener {
            val emailAddrText = (findViewById<EditText>(R.id.email_address_text)).text.toString()
            if(emailAddrText.isNotEmpty()) {
                this.send_email_with_details_to(emailAddrText)
                val text = "An email is send to $emailAddrText!\nCheck you email inbox."
                Toast.makeText(this@SigninRecoveryActivity, text, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
            } else {
                val text = "Missing email address."
                Toast.makeText(this@SigninRecoveryActivity, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun send_email_with_details_to(emailAddr: String){
    //TODO: NOT IMPLEMENTED YET
    }
}
