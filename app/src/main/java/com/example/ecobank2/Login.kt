package com.example.ecobank2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ecobank2.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Sign.setOnClickListener {
            var isValid = true
            val name = binding.number2.text.toString()
            val pass = binding.Pass2.text.toString()

            if (name.isEmpty()) {
                binding.number2.error = "Card number is required"
            } else {
                binding.number2.error = null
            }

            if (pass.isEmpty()) {
                binding.Pass2.error = "Password is required"
                isValid = false
            } else {
                binding.Pass2.error = null
            }

            if (name == "1" && pass == "1") {
                isValid = true
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                isValid = false
                binding.number2.error = "Invalid Card Number"
                binding.Pass2.error = "Invalid Password"
            }
        }
        binding.Register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        binding.forgot.setOnClickListener{
            val intent = Intent(this, Forgot::class.java)
            startActivity(intent)
        }
    }
}
