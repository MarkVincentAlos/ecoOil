package com.example.ecobank2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ecobank2.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class Login : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var binding: ActivityLoginBinding? = null
    private val db = FirebaseFirestore.getInstance()

    // Permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (!allGranted) {
            Toast.makeText(this, "Some features may not work without permissions", Toast.LENGTH_LONG).show()
        }
        proceedToMainActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()

        binding!!.Login.setOnClickListener { v ->
            val input = binding!!.number2.text.toString().trim() // Email or username
            val password = binding!!.Pass2.text.toString().trim()

            if (!isInternetAvailable) {
                showError("No internet connection")
                return@setOnClickListener
            }
            if (input.isEmpty()) {
                binding!!.number2.error = "Email or Username is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding!!.Pass2.error = "Password is required"
                return@setOnClickListener
            }

            showLoading(true)
            if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                loginWithEmail(input, password)
            } else {
                getEmailFromUsername(input, password)
            }
        }

        binding!!.Create.setOnClickListener { v ->
            startActivity(Intent(this, Register::class.java))
        }

        binding!!.forgot.setOnClickListener { v ->
            startActivity(Intent(this, Forgot::class.java))
        }
    }

    private fun getEmailFromUsername(username: String, password: String) {
        db.collection("usernames")
            .document(username)
            .get()
            .addOnSuccessListener { usernameDoc ->
                if (usernameDoc.exists()) {
                    val userId = usernameDoc.getString("userId")
                    if (userId != null) {
                        db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                if (userDoc.exists()) {
                                    val email = userDoc.getString("email")
                                    if (!email.isNullOrEmpty()) {
                                        loginWithEmail(email, password)
                                    } else {
                                        showError("Email not found for this username.")
                                    }
                                } else {
                                    showError("User data not found.")
                                }
                            }
                            .addOnFailureListener {
                                showError("Error fetching user data. Check your connection.")
                            }
                    } else {
                        showError("Invalid username.")
                    }
                } else {
                    showError("Username does not exist.")
                }
            }
            .addOnFailureListener {
                showError("Error retrieving username. Check your connection.")
            }
    }

    private fun loginWithEmail(email: String, password: String) {
        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                showLoading(false)
                if (task.isSuccessful) {
                    if (auth!!.currentUser != null && auth!!.currentUser!!.isEmailVerified) {
                        requestPermissions()
                    } else {
                        showError("Verify your email first!")
                    }
                } else {
                    try {
                        throw task.exception!!
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

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Add location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Add notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            proceedToMainActivity()
        }
    }

    private fun proceedToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private val isInternetAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }
            return false
        }

    private fun showLoading(show: Boolean) {
        binding!!.progressBar.bringToFront()
        binding!!.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding!!.dimBackground.visibility = if (show) View.VISIBLE else View.GONE
        binding!!.Login.isEnabled = !show
    }

    private fun showError(message: String) {
        showLoading(false)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM", "User FCM Token: $token")
                } else {
                    Log.e("FCM", "Fetching FCM token failed", task.exception)
                }
            }
    }
}