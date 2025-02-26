package com.example.ecobank2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nafis.bottomnavigation.NafisBottomNavigation

class MainActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<NafisBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(NafisBottomNavigation.Model(1, R.drawable.home))
        bottomNavigation.add(NafisBottomNavigation.Model(2, R.drawable.station))
        bottomNavigation.add(NafisBottomNavigation.Model(3, R.drawable.reward))
        bottomNavigation.add(NafisBottomNavigation.Model(4, R.drawable.settings2))

        bottomNavigation.setOnShowListener {
            when (it.id) {
                1 ->{
                    replaceFragment(Home())

                }
                2 ->{

                    replaceFragment(Stations())

                }
                3 ->{

                    replaceFragment(Rewards())

                }
                4 ->{

                    replaceFragment(More())

                }
            }
        }

        replaceFragment(Home())
        bottomNavigation.show(1)


    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}


