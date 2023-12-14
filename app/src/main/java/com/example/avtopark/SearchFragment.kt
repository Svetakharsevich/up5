package com.example.avtopark

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class SearchFragment : Fragment() {
    var db: SQLiteDatabase? = null
    var userCursor: Cursor? = null
    private lateinit var Nav: BottomNavigationView
    private lateinit var EditTextInfo: EditText
    private lateinit var ImageSity: ImageView//
    private lateinit var recucle: RecyclerView//
    private lateinit var SearchButton: Button
    private lateinit var BackButton: ImageButton//
    private lateinit var databaseHelper: DBHelper//
    private var userAdapter: UserAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentLayout=inflater.inflate(R.layout.fragment_search, container, false)
        databaseHelper = DBHelper(fragmentLayout.context)
        BackButton=fragmentLayout.findViewById(R.id.ButtonBack)
        BackButton.setOnClickListener{
            var intent= Intent(fragmentLayout.context,MainActivity::class.java)
            startActivity(intent)
        }
        EditTextInfo=fragmentLayout.findViewById(R.id.editText)
        recucle = fragmentLayout.findViewById(R.id.recycle)
        ImageSity = fragmentLayout.findViewById(R.id.SityImage)
        SearchButton=fragmentLayout.findViewById(R.id.button)
        SearchButton.setOnClickListener {
            printList(recucle)
            val routeId = EditTextInfo.text.toString()
            searchById(routeId)
        }
        /*Nav=fragmentLayout.findViewById(R.id.Navigation)
        val navController= NavHostFragment.findNavController(this)
        Nav.setOnNavigationItemSelectedListener { menuItem ->
            try {
                when (menuItem.itemId) {
                    R.id.Home -> {
                        navController.navigate(R.id.Home)
                        true
                    }
                    R.id.search -> {
                        navController.navigate(R.id.search)
                        true
                    }
                    else -> false
                }
            } catch (e: Exception) {
                Log.e("Navigation", "Error handling menu item click", e)
                false
            }
        }*/

        val sharedPreferences = requireActivity().getSharedPreferences("loc", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("a", "60.597474")
        editor.putString("b", "56.838011")
        editor.apply()
        return fragmentLayout
    }
    @SuppressLint("Range")
    fun printList(recucle:RecyclerView){
        val userList: MutableList<Routes> = databaseHelper.getAllRoutesData().toMutableList()

        // Instantiate UserAdapter if not already done
        if (userAdapter == null) {
            userAdapter = UserAdapter(userList)
        } else {
            // Update data if adapter is already created
            userAdapter?.updateData(userList)
        }

        // Проверяем, есть ли данные для отображения
        if (userList.isNotEmpty()) {
            recucle.layoutManager = LinearLayoutManager(requireContext())

            // Используем адаптер для отображения данных в RecyclerView
            recucle.adapter = userAdapter
        } else {
            // Обработка случая, когда база данных пуста
            Toast.makeText(requireContext(), "No routes found", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Range")
    fun searchById(routeId: String) {
        try {
            // Ensure that the database is initialized
            if (db == null) {
                db = databaseHelper.writableDatabase
            }

            // Define the columns you want to retrieve
            val columns = arrayOf(
                DBHelper.COLUMN_ID_ROUTES,
                DBHelper.COLUMN_Stoping,
                DBHelper.COLUMN_Sity_Routes
            )

            // Specify the WHERE clause to filter based on route ID
            val selection = "${DBHelper.COLUMN_ID_ROUTES} = ?"

            // Specify the values for the WHERE clause
            val selectionArgs = arrayOf(routeId)

            // Query the database
            userCursor = db?.query(
                DBHelper.TABLE_ROUTES,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            // Check if the cursor is not null
            userCursor?.let {
                if (it.moveToFirst()) {
                    // Retrieve data from the cursor
                    val routeId = it.getLong(it.getColumnIndex(DBHelper.COLUMN_ID_ROUTES))
                    val stopping = it.getString(it.getColumnIndex(DBHelper.COLUMN_Stoping))
                    val sity = it.getString(it.getColumnIndex(DBHelper.COLUMN_Sity_Routes))

                    zapros(sity)
                    // Now you have the data for the specified route ID
                    // Update the UserAdapter data list with the new data
                    val newDataList = mutableListOf<Routes>() // Assuming RouteData is your data model
                    newDataList.add(Routes(routeId, stopping, sity))

                    // Update the adapter with the new data
                    userAdapter?.updateData(newDataList)

                    // Notify the adapter that the data has changed
                    userAdapter?.notifyDataSetChanged()
                } else {
                    // Handle the case where no matching route was found
                    Toast.makeText(
                        requireContext(),
                        "Route not found for ID: $routeId",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            // Handle any exceptions that might occur
            Toast.makeText(
                requireContext(),
                "Error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            // Close the cursor when you're done with it
            userCursor?.close()
        }
    }
    private fun zapros(sity: String){
        Log.d("MyLog", "zapros() method called")

        // Проверка наличия разрешения на использование интернета


        val url = "https://geocode-maps.yandex.ru/1.x/?apikey=3ee7e538-e94a-42c4-ac69-00f6160dfd34&geocode=$sity&format=json"

        val queue = Volley.newRequestQueue(requireContext())

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Обработка успешного ответа
                try {
                    val obj = JSONObject(response)
                    val featureMember = obj.getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")

                    if (featureMember.length() > 0) {
                        val firstObject = featureMember.getJSONObject(0)
                        val point = firstObject.getJSONObject("GeoObject")
                            .getJSONObject("Point")
                            .getString("pos")
                        val tochki = point.toString().split(" ")
                        searchKarta(tochki)

                        Log.d("MyLog", "Coordinates: $point")
                    } else {
                        Log.d("MyLog", "No features found in the response")
                    }
                } catch (e: JSONException) {
                    Log.d("MyLog", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                // Обработка ошибки
                val statusCode = error.networkResponse.statusCode
                Log.d("MyLog", "Volley error status code: $statusCode")


            })

        // Добавление запроса в очередь
        queue.add(stringRequest)
    }
    fun searchKarta(array: List<String>){
        val imageUrl = "https://static-maps.yandex.ru/v1?ll=${array[0]},${array[1]}&size=450,450&z=13&pt=${array[0]},${array[1]},pmwtm1~${array[0]},${array[1]},pmwtm99&apikey=f9ce7b23-8786-44b7-8308-864c74bf640a"

        Picasso.get().load(imageUrl).into(ImageSity)

    }
}