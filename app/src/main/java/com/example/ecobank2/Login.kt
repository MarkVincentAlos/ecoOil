package com.example.ecobank2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ecobank2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

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
            val input = binding.number2.text.toString().trim() // Email or username
            val password = binding.Pass2.text.toString().trim()

            if (!isInternetAvailable()) {
                showError("No internet connection")
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

            showLoading(true)

            if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                loginWithEmail(input, password)
            } else {
                getEmailFromUsername(input, password)
            }
        }

        binding.Create.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        binding.forgot.setOnClickListener {
            startActivity(Intent(this, Forgot::class.java))
        }
    }

    // 🔹 Fetch Email from Username (Handles Non-Existent Username)
    private fun getEmailFromUsername(username: String, password: String) {
        db.collection("users")
            .whereEqualTo("username", username)
            .limit(1) // Optimize query to return at most one document
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val email = documents.documents[0].getString("email")
                    if (email != null) {
                        loginWithEmail(email, password)
                    } else {
                        showError("No email found for this username")
                    }
                } else {
                    showError("Incorrect username. Please try again.")
                }
            }
            .addOnFailureListener {
                showError("Error retrieving username. Check your connection.")
            }
    }

    // 🔹 Login with Email & Password (Handles Incorrect Password)
    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        checkAdminStatus(user.uid)
                    } else {
                        showError("Verify your email first!")
                    }
                } else {
                    // Handling incorrect credentials
                    try {
                        throw task.exception ?: Exception("Invalid credentials. Try again.")
                    } catch (e: FirebaseAuthInvalidUserException) {
                        showError("Email not found. Please register first.")
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        showError("Incorrect password. Please try again.")
                    } catch (e: Exception) {
                        showError("Login failed. Check your credentials and try again.")
                    }
                }
            }
    }

    // 🔹 Check if User is Admin
    private fun checkAdminStatus(userId: String) {
        db.collection("admins").document(userId).get()
            .addOnSuccessListener { document ->
                val intent = if (document.exists() && document.getString("role") == "admin") {
                    Intent(this, AdminActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                showError("Failed to check admin status")
            }
    }

    // 🔹 Check Internet Connection
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // 🔹 Show or Hide Loading State
    private fun showLoading(show: Boolean) {
        binding.progressBar.bringToFront()
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.dimBackground.visibility = if (show) View.VISIBLE else View.GONE
        binding.Login.isEnabled = !show
    }

    // 🔹 Display Toast Error Message
    private fun showError(message: String) {
        showLoading(false)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM", "User FCM Token: $token")
                } else {
                    Log.e("FCM", "Fetching FCM token failed", task.exception)
                }
            }
    }
}
