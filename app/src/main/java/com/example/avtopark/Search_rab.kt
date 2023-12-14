package com.example.avtopark

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class Search_rab : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_rab)
        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.navFragment) as NavHostFragment? ?: return
        val navController = host.navController
        val sideBar = findViewById<NavigationView>(R.id.nav_view)
        sideBar?.setupWithNavController(navController)
        sideBar.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home_rab -> {
                    navController.navigate(R.id.searchFragment)
                    true
                }

                R.id.search_rab -> {
                    navController.navigate(R.id.action_searchFragment2_to_info_rab)
                    true
                }
                R.id.Drivers -> {
                    navController.navigate(R.id.driver_rab)
                    true
                }
                // Добавьте обработку других элементов меню, если необходимо
                else -> false
            }
        }
    }
}