package com.example.avtopark

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home_fragment_user.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home_fragment_user : Fragment() {
    var db: SQLiteDatabase? = null
    var userCursor: Cursor? = null
    private lateinit var recucle: RecyclerView//
    private lateinit var BackButton: ImageButton//
    private lateinit var databaseHelper: DBHelper//
    private var userAdapter: UserAdapter? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentLayout = inflater.inflate(R.layout.fragment_home_user, container, false)

        // Initialize databaseHelper
        databaseHelper = DBHelper(fragmentLayout.context)

        // Find and initialize the BackButton from fragmentLayout
        val BackButton: ImageButton = fragmentLayout.findViewById(R.id.ButtonBack)
        BackButton.setOnClickListener {
            val intent = Intent(fragmentLayout.context, MainActivity::class.java)
            startActivity(intent)
        }

        // Find and initialize recucle from fragmentLayout
        val recucle: RecyclerView = fragmentLayout.findViewById(R.id.recycle)

        // Call the printList function with recucle
        printList(recucle)

        return fragmentLayout
    }
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
}