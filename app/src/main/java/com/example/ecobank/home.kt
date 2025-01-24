package com.example.ecobank

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nafis.bottomnavigation.NafisBottomNavigation

class home : AppCompatActivity() {

    private lateinit var bottomNavigation: NafisBottomNavigation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.add(NafisBottomNavigation.Model(1, R.drawable.home))
        bottomNavigation.add(NafisBottomNavigation.Model(2, R.drawable.credit_card))
        bottomNavigation.add(NafisBottomNavigation.Model(3, R.drawable.profile))
        bottomNavigation.add(NafisBottomNavigation.Model(4, R.drawable.settings))


    }

    }
