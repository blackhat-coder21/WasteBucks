package com.example.wastebucks

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.wastebucks.admin.AdminActivity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val register = findViewById<TextView>(R.id.login)
        register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val login = findViewById<TextView>(R.id.login_btn)
        login.setOnClickListener {
            val email = findViewById<EditText>(R.id.logemail)
            val password = findViewById<EditText>(R.id.log_pwd)

            if(email.text.toString().isEmpty())
            {
                email.error = "Please Enter an email"
                email.requestFocus()
                return@setOnClickListener
            }
            else if(!email.text.toString().contains("@")) {
                email.error = "Please enter a valid email"
                email.requestFocus()
                return@setOnClickListener
            }
            else if(password.text.toString().isEmpty())
            {
                password.error = "Please Enter a password"
                password.requestFocus()
                return@setOnClickListener
            }
            else if(password.text.toString().length < 8) {
                password.error = "Password should be at least 8 characters long"
                password.requestFocus()
                return@setOnClickListener
            }
            else if(! password.text.toString().matches("[a-zA-Z0-9]+".toRegex()))
            {
                password.error = "Password can only contain alphanumeric character"
                password.requestFocus()
                return@setOnClickListener
            }
            else{
                loginUser()
            }
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In...")
        progressDialog.show()
        val email = findViewById<EditText>(R.id.logemail)
        val password = findViewById<EditText>(R.id.log_pwd)

        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnSuccessListener(this) {
                checkUser()
            }
            .addOnFailureListener(this){
                progressDialog.dismiss()
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User...")

        val firebaseUser = auth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("Users")

        ref.child(firebaseUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                val userName = snapshot.child("name").getValue(String::class.java)
                val userType = snapshot.child("userType").getValue(String::class.java)

                if (userType == "user") {
                    Toast.makeText(this@LoginActivity, "Signed in as $userName", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if(userType == "admin"){
                    Toast.makeText(this@LoginActivity, "Welcome Admin\\nSigned in as $userName", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }
}