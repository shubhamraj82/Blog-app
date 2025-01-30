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

class MainActivity : AppCompatActivity() {
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginBtn: Button
    private lateinit var forgotPassword: TextView
    private lateinit var needAnAccount: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginBtn = findViewById(R.id.login_btn)
        loginEmail = findViewById(R.id.login_email)
        loginPassword = findViewById(R.id.login_password)
        forgotPassword = findViewById(R.id.forgot_password)
        needAnAccount = findViewById(R.id.need_an_account)
        auth= FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering User...")
        progressDialog.setCancelable(false)

        needAnAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        loginBtn.setOnClickListener {
            val email=loginEmail.text.toString()
            val password=loginPassword.text.toString()
            if (email.isEmpty()){
                loginEmail.error="Email Required"
        }else if (password.isEmpty()){
                loginPassword.error="Password Required"
            }else{
                login(email,password)
            }
        }
    }

    private fun login(email: String, password: String) {
        progressDialog.setTitle("Please wait..")
        progressDialog.show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
    }


    override fun onStart() {
        super.onStart()
        val user=auth.currentUser
        if(user!=null){
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}