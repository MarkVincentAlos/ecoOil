package com.example.ecobank2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ecobank2.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Load user info from Firestore
        loadUserInfo()

        // Set up the back button to navigate to the More fragment
        binding.back.setOnClickListener { navigateToFragment(More()) }

        return binding.root
    }

    private fun loadUserInfo() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid

            // Fetch user data from Firestore
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        val email = document.getString("email")

                        // Update the UI with the fetched data
                        binding.pName.text = username ?: "No Name"
                        binding.pEmail2.text = email ?: "No Email"
                        binding.pNumber2.text = uid
                    } else {
                        Log.d("ProfileFragment", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ProfileFragment", "get failed with ", exception)
                }
        } else {
            Log.e("ProfileFragment", "User is not logged in")
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}