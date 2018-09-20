package itaycsguy.rtchordslearningapk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val buttonSignUp = findViewById<Button>(R.id.sign_up_button)
        buttonSignUp.setOnClickListener {
            val username = (findViewById<EditText>(R.id.text_username)).text.toString()
            val password = (findViewById<EditText>(R.id.text_password)).text.toString()
            val password_confirm = (findViewById<EditText>(R.id.text_password_confirm)).text.toString()
            var text = ""
            if (username.isEmpty() || password.isEmpty() || password_confirm.isEmpty()) {
                text = "Missing details."
            } else if (this.is_exist_username(username)) {
                text = "Username is already exist."
            } else if (!this.is_valid_password(password)) {
                text = "Invalid password."
            } else if (password != password_confirm) {
                text = "Incorrect password."
            }
            if (text.isNotEmpty()) {
                Toast.makeText(this@SignupActivity, text, Toast.LENGTH_SHORT).show()
            } else {
                text = "Registered!"
                Toast.makeText(this@SignupActivity, text, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun is_valid_password(password: String):Boolean{
        return true
    }

    private fun is_exist_username(username: String):Boolean{
        return false
    }
}
