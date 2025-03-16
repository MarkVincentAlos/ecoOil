package com.example.ecobank2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ecobank2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Patterns

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.Login.setOnClickListener {
            val input = binding.number2.text.toString().trim() // Can be email or username
            val password = binding.Pass2.text.toString().trim()

            if (!isInternetAvailable()) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (input.isEmpty()) {
                binding.number2.error = "Email or Username is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.Pass2.error = "Password is required"
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.dimBackground.visibility = View.VISIBLE
            binding.Login.isEnabled = false

            if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                loginWithEmail(input, password)
            } else {
                getEmailFromUsername(input, password)
            }
        }

        binding.Create.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        binding.forgot.setOnClickListener {
            val intent = Intent(this, Forgot::class.java)
            startActivity(intent)
        }
    }

    // ✅ Fetch email from Firestore using username
    private fun getEmailFromUsername(username: String, password: String) {
        db.collection("users")
            .whereEqualTo("username", username) // Query by username field
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDocument = documents.documents[0]
                    val email = userDocument.getString("email")
                    if (!email.isNullOrEmpty()) {
                        loginWithEmail(email, password)
                    } else {
                        showError("No email found for this username")
                    }
                } else {
                    showError("Username not found")
                }
            }
            .addOnFailureListener {
                showError("Failed to retrieve username")
            }
    }

    // ✅ Log in with Email and Password
    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                binding.Login.isEnabled = true

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified) {
                            checkAdminStatus(user.uid)
                        } else {
                            showError("Verify your email first!")
                        }
                    }
                } else {
                    showError("Invalid credentials. Try again.")
                }
            }
    }

    // ✅ Check if the user is an admin in Firestore
    private fun checkAdminStatus(userId: String) {
        db.collection("admins").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.getString("role") == "admin") {
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener {
                showError("Failed to check admin status")
            }
    }

    // ✅ Check Internet Connection
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.dimBackground.visibility = View.GONE
        binding.Login.isEnabled = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}