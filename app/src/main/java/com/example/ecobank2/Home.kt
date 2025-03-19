package com.example.ecobank2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecobank2.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Home : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val NOTIFICATION_PERMISSION_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.gostation.bringToFront()
        loadUserPoints()

        binding.pointsText.setOnClickListener {
            val fragment = Qrr()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.gostation.setOnClickListener {
            val fragment = Stations()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val fragment = DieselGas()
        parentFragmentManager.beginTransaction()
            .replace(R.id.dieselGasContainer, fragment)
            .commit()

        return binding.root
    }

    private fun loadUserPoints() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(user.uid)

            userRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    if (_binding != null) {
                        binding.pointsText.text = "Error loading points"
                        binding.cName.text = "Error loading name"
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val points = snapshot.getLong("points") ?: 0
                    val username = snapshot.getString("username") ?: "Unknown"

                    if (_binding != null) {
                        binding.pointsText.text = "$points points"
                        binding.cName.text = "Welcome back, $username"
                    }
                } else {
                    // If user document doesn't exist, create it with default values
                    val newUser = hashMapOf("points" to 0, "username" to "New User")
                    userRef.set(newUser).addOnSuccessListener {
                        if (_binding != null) {
                            binding.pointsText.text = "0 points"
                            binding.cName.text = "Welcome, New User"
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
