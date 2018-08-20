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

        val buttonSignUp = findViewById(R.id.sign_up_button) as Button
        buttonSignUp.setOnClickListener {
            val username = (findViewById(R.id.text_username) as EditText).text.toString()
            val password = (findViewById(R.id.text_password) as EditText).text.toString()
            val password_confirm = (findViewById(R.id.text_password_confirm) as EditText).text.toString()
            var text = ""
            if (username.length == 0 || password.length == 0 || password_confirm.length == 0) {
                text = "Missing details."
            } else if (this.is_exist_username(username)) {
                text = "Username is already exist."
            } else if (!this.is_valid_password(password)) {
                text = "Invalid password."
            } else if (password != password_confirm) {
                text = "Incorrect password."
            }
            if (text.length > 0) {
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
