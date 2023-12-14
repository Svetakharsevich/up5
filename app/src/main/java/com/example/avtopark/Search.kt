package com.example.avtopark

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.RouteInfo
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.avtopark.DBHelper.Companion.COLUMN_ID_ROUTES
import com.example.avtopark.DBHelper.Companion.COLUMN_Sity_Routes
import com.example.avtopark.DBHelper.Companion.COLUMN_Stoping
import com.example.avtopark.DBHelper.Companion.TABLE_ROUTES
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import android.media.MediaRoute2Info
import android.media.MediaRouter
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView


class Search : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.navFragment) as NavHostFragment? ?: return
        val navController = host.navController
        val sideBar = findViewById<NavigationView>(R.id.nav_view)
        sideBar?.setupWithNavController(navController)
        sideBar.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    navController.navigate(R.id.searchFragment)
                    true
                }

                R.id.search -> {
                    navController.navigate(R.id.home_fragment_user)
                    true
                }
                // Добавьте обработку других элементов меню, если необходимо
                else -> false
            }
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerlayout)
    }

}