package app.itaycsguy.musiciansaidb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView


class ProfileActivity : AppCompatActivity() {
    private lateinit var _user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(findViewById(R.id.profile_toolbar))
        _user = User(intent.getSerializableExtra("user") as HashMap<String,String>)
        findViewById<TextView>(R.id.profile_username).append(" " + _user.getUserName())
        findViewById<TextView>(R.id.profile_email).append(" " + _user.getEmail())
        findViewById<TextView>(R.id.profile_givenname).append(" " + _user.getGivenName())
        findViewById<TextView>(R.id.profile_familyname).append(" " + _user.getFamilyName())
        findViewById<TextView>(R.id.profile_permission).append(" " + _user.getPermission())
        val image = Uri.parse(_user.getPhoto())
        if (image != null && image != Uri.EMPTY && image != Uri.parse("null")) {
            // TODO: need to classify between google default photo to uploaded user photo
            // findViewById<ImageView>(R.id.my_profile_photo).setImageURI(Uri.parse(_user.getPhoto()))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_management, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            val progressBar = startProgressBar(this,R.id.profile_progressBar)
            when(item.itemId) {
                R.id.to_operations_activity -> {
                    val intent = Intent(this,MenuActivity::class.java)
                    intent.putExtra("user",_user.getHashDetails())
                    stopProgressBar(progressBar)
                    startActivity(intent)
                }
                R.id.action_logout -> {
                    val intent = Intent(this,StartActivity::class.java)
                    stopProgressBar(progressBar)
                    startActivity(intent)
                }
                else -> { stopProgressBar(progressBar) }
            }
        }
        return true
    }
}