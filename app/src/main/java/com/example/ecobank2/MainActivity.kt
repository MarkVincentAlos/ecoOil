package com.example.ecobank2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigation.itemIconTintList = resources.getColorStateList(R.color.menu_item)
        bottomNavigation.itemTextColor = resources.getColorStateList(R.color.menu_item)

        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(Home())
                    true
                }
                R.id.stations -> {
                    replaceFragment(Stations())
                    true
                }
                R.id.rewards -> {
                    replaceFragment(Rewards())
                    true
                }
                R.id.settings -> {
                    replaceFragment(More())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            replaceFragment(Home())
            bottomNavigation.selectedItemId = R.id.home
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
