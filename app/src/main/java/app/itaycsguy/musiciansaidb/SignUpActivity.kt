package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val buttonSignUp = findViewById<Button>(R.id.sign_up_button)
        buttonSignUp.setOnClickListener {
            val username = (findViewById<EditText>(R.id.text_username)).text.toString()
            val password = (findViewById<EditText>(R.id.text_password)).text.toString()
            val passwordConfirm = (findViewById<EditText>(R.id.text_password_confirm)).text.toString()
            var text = ""
            if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                text = "Missing details."
            } else if (this.isExistUsername(username)) {
                text = "Username is already exist."
            } else if (!this.isValidPassword(password)) {
                text = "Invalid password."
            } else if (password != passwordConfirm) {
                text = "Incorrect password."
            }
            if (text.isNotEmpty()) {
                Toast.makeText(this@SignUpActivity, text, Toast.LENGTH_SHORT).show()
            } else {
                text = "Registered!"
                Toast.makeText(this@SignUpActivity, text, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isValidPassword(password: String):Boolean{
        return true
    }

    private fun isExistUsername(username: String):Boolean{
        return false
    }
}
