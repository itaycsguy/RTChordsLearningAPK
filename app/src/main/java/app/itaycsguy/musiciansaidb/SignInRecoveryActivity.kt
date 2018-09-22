package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignInRecoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth)

        val sendButton = findViewById<Button>(R.id.send_button)
        sendButton.setOnClickListener {
            val emailAddressText = (findViewById<EditText>(R.id.email_address_text)).text.toString()
            if(emailAddressText.isNotEmpty()) {
                this.sendEmailWithDetailsTo(emailAddressText)
                val text = "An email is send to $emailAddressText!\nCheck you email inbox."
                Toast.makeText(this@SignInRecoveryActivity, text, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
            } else {
                val text = "Missing email address."
                Toast.makeText(this@SignInRecoveryActivity, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmailWithDetailsTo(emailAddr: String){
    //TODO: NOT IMPLEMENTED YET
    }
}
