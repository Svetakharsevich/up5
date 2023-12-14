package com.example.avtopark

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var email:EditText
    private lateinit var login:EditText
    private lateinit var password:EditText
    private lateinit var spinner: Spinner
    private lateinit var entry:Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("LoginAndPassword", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("usernameforadmin", "admin12")
        editor.putString("passwordforadmin", "admin12")
        editor.putString("usernameforrab", "loginvod23")
        editor.putString("passwordforrab","loginvod23")
        editor.putString("username","ects")
        editor.putString("password", "ects")
        editor.putString("login","ects")

        editor.apply()
        val dbHelper = DBHelper(this)
        email=findViewById(R.id.email)
        login=findViewById(R.id.login)
        password=findViewById(R.id.password)
        spinner=findViewById(R.id.spinner)
        entry=findViewById(R.id.entry)
        val db = dbHelper.writableDatabase
        entry.setOnClickListener{
            var item:String
            item=spinner.selectedItem.toString()

            val em :String = email.text.toString()
            val pas:String = password.text.toString()
            val logins:String=login.text.toString()



            if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {

                Toast.makeText(this@MainActivity, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
            }
            else if(item=="Диспетчер") {

                val sharedPreferences = getSharedPreferences("LoginAndPassword", MODE_PRIVATE)

                val savedUsername = sharedPreferences.getString("usernameforadmin", "")
                val savedPassword = sharedPreferences.getString("passwordforadmin", "")


                if (em == savedUsername && pas == savedPassword) {
                    val intent = Intent(this@MainActivity, Search_admin::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()

                }

            }
            else if(item=="Водитель") {
                val email = email.text.toString()
                val logi = login.text.toString()
                val password = password.text.toString()
                val userList = dbHelper.getAllLOGINPASSData()
                Log.d("MyLog", "User List: $userList")

                // Получим информацию о водителе по email, login и password
                val driver = dbHelper.getDriverInfoByEmailAndPassword(email, logi, password)

                if (driver != null) {
                    // Данные введены правильно

                    // Шаг 5: Передать ID водителя в другой класс
                    dbHelper.saveDriverIdToSharedPreferences(this, driver.id)

                    // Пример: Переход в другой класс (замените на свой код)
                    val intent = Intent(this, Search_rab::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    // Ошибка ввода данных
                    // Показать сообщение об ошибке (замените на свой код)
                    Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
                }
               /* val sharedPreferences = getSharedPreferences("LoginAndPassword", MODE_PRIVATE)

                val savedUsername = sharedPreferences.getString("usernameforrab", "")
                val savedPassword = sharedPreferences.getString("passwordforrab", "")


                if (log == savedUsername && pas == savedPassword) {
                    val intent = Intent(this@MainActivity, Search_rab::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()

                }*/

            }
            else if(item=="Пассажир") {

                val sharedPreferences = getSharedPreferences("LoginAndPassword", MODE_PRIVATE)

                val savedUsername = sharedPreferences.getString("username", "")
                val savedPassword = sharedPreferences.getString("password", "")
                val savedLogin=sharedPreferences.getString("login","")


                if (em == savedUsername && pas == savedPassword&&savedLogin==logins) {
                    val intent = Intent(this@MainActivity, Search::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()

                }

            }

        }
    }
}