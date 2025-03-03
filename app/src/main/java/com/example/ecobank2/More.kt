package com.example.ecobank2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ecobank2.databinding.FragmentMoreBinding

class More : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        binding.profile.setOnClickListener(){
            val Profile = Profile()

            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment_container, Profile)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}