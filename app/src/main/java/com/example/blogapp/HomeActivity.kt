package com.example.blogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.action_add_post -> {
                startActivity(Intent(this, AddPostActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}