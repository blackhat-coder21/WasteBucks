package com.example.wastebucks

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.play.core.integrity.e
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Objects

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)


        val register = findViewById<TextView>(R.id.nxtbtn)
        register.setOnClickListener {
            val name = findViewById<EditText>(R.id.name)
            val email = findViewById<EditText>(R.id.email)
            val password = findViewById<EditText>(R.id.pwd)
            val confirmPassword = findViewById<EditText>(R.id.cpwd)

            if(name.text.toString().isEmpty()) {
                name.error = "Please enter your name"
                name.requestFocus()
                return@setOnClickListener
            }
            else if(name.text.toString().contains("[0-9]".toRegex())) {
                name.error = "Name cannot contain digits"
                name.requestFocus()
                return@setOnClickListener
            }
            else if(name.text.toString().contains("[!@#$%^&*(),.?\":{}|<>]".toRegex())) {
                name.error = "Name cannot contain symbols"
                name.requestFocus()
                return@setOnClickListener
            }
            else if(email.text.toString().isEmpty()) {
                email.error = "Please enter your email"
                email.requestFocus()
                return@setOnClickListener
            }
            else if(!email.text.toString().contains("@")) {
                email.error = "Please enter a valid email"
                email.requestFocus()
                return@setOnClickListener
            }
            else if(password.text.toString().isEmpty()) {
                password.error = "Please enter your password"
                password.requestFocus()
                return@setOnClickListener
            }
            else if(password.text.toString().length < 8) {
                password.error = "Password should be at least 8 characters long"
                password.requestFocus()
                return@setOnClickListener
            }
            else if(confirmPassword.text.toString().isEmpty()) {
                confirmPassword.error = "Please confirm your password"
                confirmPassword.requestFocus()
                return@setOnClickListener
            }
            else if(password.text.toString() != confirmPassword.text.toString()) {
                confirmPassword.error = "Passwords do not match"
                confirmPassword.requestFocus()
                return@setOnClickListener
            }
            else{
                createUserAccount()
            }

        }
    }

    private fun createUserAccount() {
        progressDialog.setMessage("Creating account...")
        progressDialog.show()
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.pwd)
        val name = findViewById<EditText>(R.id.name).text.toString()

        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful) {
                    progressDialog.setMessage("Saving user info...")
                    val currentUser = auth.currentUser
                    val uid = currentUser?.uid
                    val email = findViewById<EditText>(R.id.email).text.toString()
                    val password = findViewById<EditText>(R.id.pwd).text.toString()
                    val timestamp: String = System.currentTimeMillis().toString()
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["uid"] = uid.toString()
                    hashMap["email"] = email
                    hashMap["password"] = password
                    hashMap["name"] = name
                    hashMap["profileImage"] = ""
                    hashMap["userType"] = "user"
                    hashMap["timestamp"] = timestamp

                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(uid.toString()).setValue(hashMap)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Account created...", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "SignIn Failed", Toast.LENGTH_SHORT).show()
                        }
                }
                else {
                    Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                    progressDialog.dismiss()
                    if (task.exception is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Account already exists", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        email.error = "Please enter a valid email"
                        email.requestFocus()
                    }
                }
            }
    }
}