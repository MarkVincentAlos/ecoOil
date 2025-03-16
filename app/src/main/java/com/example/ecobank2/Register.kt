package com.example.ecobank2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ecobank2.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.registerButton.setOnClickListener {
            val email = binding.number2.text.toString().trim()
            val username = binding.username.text.toString().trim()
            val password = binding.Pass1.text.toString().trim()
            val confirmPassword = binding.Pass2.text.toString().trim()

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Please fill in all fields")
                return@setOnClickListener
            }
            if (password.length < 6) {
                showToast("Password must be at least 6 characters")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                showToast("Passwords do not match")
                return@setOnClickListener
            }

            checkUsernameExists(username) { exists ->
                if (exists) {
                    showToast("Username is already taken")
                } else {
                    registerUser (email, username, password)
                }
            }
        }

        binding.Login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    // Check if the username already exists in 'usernames' collection
    private fun checkUsernameExists(username: String, callback: (Boolean) -> Unit) {
        db.collection("usernames").document(username).get()
            .addOnSuccessListener { document ->
                callback(document.exists())
            }
            .addOnFailureListener {
                showToast("Error checking username")
                callback(false)
            }
    }

    // Register User in Firestore
    private fun registerUser (email: String, username: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser ?.uid

                if (userId != null) {
                    // Add username -> userId in 'usernames' collection
                    val usernameData = hashMapOf(
                        "userId" to userId
                    )
                    db.collection("usernames").document(username)
                        .set(usernameData)
                        .addOnSuccessListener {
                            // Add user details in 'users' collection
                            val user = hashMapOf(
                                "username" to username,
                                "email" to email,
                                "points" to 0
                            )
                            db.collection("users").document(userId).set(user)
                                .addOnSuccessListener {
                                    auth.currentUser ?.sendEmailVerification()
                                    showToast("Registration successful! Please verify your email.")
                                    startActivity(Intent(this, Login::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    showToast("Error saving user data: ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            showToast("Error saving username: ${e.message}")
                        }
                }
            } else {
                showToast("Registration failed: ${task.exception?.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}