package itaycsguy.rtchordslearningapk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SigninActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSignIn = findViewById(R.id.sign_in_button) as Button
        buttonSignIn.setOnClickListener {
            val username = (findViewById(R.id.text_username) as EditText).text.toString()
            val password = (findViewById(R.id.text_password) as EditText).text.toString()
            this.validate(username,password)
        }
        val buttonSignUp = findViewById(R.id.sign_up_button) as Button
        buttonSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        val forgotButton = findViewById(R.id.forgot_button) as Button
        forgotButton.setOnClickListener {
            val intent = Intent(this, SigninRecoveryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validate(username: String,password: String){
        if(this.is_db_confirm(username,password)) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        } else {
            val text = "Wrong username/password. Try again."
            Toast.makeText(this@SigninActivity, text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun is_db_confirm(username: String,password: String):Boolean{
        return (username == "admin" && password == "admin")
    }
}
