package com.example.blogapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerEmail: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerBtn: Button
    private lateinit var alreadyHaveAnAccount: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //action bar
        val actionBar = supportActionBar
        actionBar?.title = "Register"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)

        mAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering User...")
        progressDialog.setCancelable(false)

        registerBtn = findViewById(R.id.register_btn)
        registerEmail = findViewById(R.id.register_email)
        registerPassword = findViewById(R.id.register_password)
        alreadyHaveAnAccount = findViewById(R.id.Already_have_an_account)

        alreadyHaveAnAccount.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        registerBtn.setOnClickListener {
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()
            if (email.isEmpty()) {
                registerEmail.error = "Email Required"
            } else if (password.isEmpty()) {
                registerPassword.error = "Password Required"
            } else if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email, password)
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        progressDialog.setTitle("Please wait..")
        progressDialog.show()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    startActivity(Intent(this, HomeActivity::class.java))
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}