package com.example.ecobank

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecobank.databinding.ActivityDepositBinding

class deposit : AppCompatActivity() {
    private lateinit var binding: ActivityDepositBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}