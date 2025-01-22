package com.example.ecobank

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecobank.databinding.ActivityOnboardBinding

class onboard : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.c1.setOnClickListener(){
            val intent = Intent(this, deposit::class.java)
            startActivity(intent)
        }
    }
}