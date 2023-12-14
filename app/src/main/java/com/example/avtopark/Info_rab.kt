package com.example.avtopark

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Info_rab : Fragment() {
    var db: SQLiteDatabase? = null
    var userCursor: Cursor? = null
    private lateinit var recucle: RecyclerView//
    private lateinit var BackButton: ImageButton//
    private lateinit var databaseHelper: DBHelper//
    private var rabAdapter: RabAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentLayout = inflater.inflate(R.layout.fragment_info_rab, container, false)

        // Find and initialize the BackButton from fragmentLayout
        databaseHelper = DBHelper(requireContext())

        // Остальной код...
        val BackButton: ImageButton = fragmentLayout.findViewById(R.id.ButtonBack)
        BackButton.setOnClickListener {
            val intent = Intent(fragmentLayout.context, MainActivity::class.java)
            startActivity(intent)
        }
        val groupedData = databaseHelper.getGroupedDataByCondition()

        // Теперь groupedData содержит данные, сгруппированные по состоянию автобуса
        for ((condition, busList) in groupedData) {
            // Ваш код для обработки данных по группам
            Log.d("GroupedData", "Condition: $condition, BusList: $busList")
        }
        val recucle: RecyclerView = fragmentLayout.findViewById(R.id.recycle_rab)

        // Создадим список для отображения данных в порядке "рабочее", "стабильное", "ремонт"
        val orderedList = mutableListOf<Bus>()
        groupedData["Рабочее"]?.let { orderedList.addAll(it) }
        groupedData["Стабильное"]?.let { orderedList.addAll(it) }
        groupedData["На ремонте"]?.let { orderedList.addAll(it) }

        printList(recucle, orderedList)
        return fragmentLayout
    }
    fun printList(recucle:RecyclerView, orderedList: List<Bus>){
        val userList: MutableList<Bus> = orderedList.toMutableList()

        // Instantiate UserAdapter if not already done
        if (rabAdapter == null) {
            rabAdapter = RabAdapter(userList)
        } else {
            // Update data if adapter is already created
            rabAdapter!!.updateData(userList)
        }

        // Проверяем, есть ли данные для отображения
        if (userList.isNotEmpty()) {
            recucle.layoutManager = LinearLayoutManager(requireContext())

            // Используем адаптер для отображения данных в RecyclerView
            recucle.adapter = rabAdapter
        } else {
            // Обработка случая, когда база данных пуста
            Toast.makeText(requireContext(), "No routes found", Toast.LENGTH_SHORT).show()
        }
    }
/*   fun addButClick(view: View) {
      val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Введите данные")

        // Создайте контейнер LinearLayout для размещения двух EditText
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val EditID = EditText(requireContext())
        val EditCondition = EditText(requireContext())
        val EditPrecent = EditText(requireContext())
        val EditModel=EditText(requireContext())

        EditID.hint = "Введите номер автобуса"
        EditCondition.hint = "Введите состояние"
        EditPrecent.hint = "Введите процент амортизации"
        EditModel.hint="Введите модель автобуса"

        layout.addView(EditID)
        layout.addView(EditCondition)
        layout.addView(EditPrecent)
        layout.addView(EditModel)

        builder.setView(layout)

        // Установите кнопку "OK" для сохранения данных
        builder.setPositiveButton("OK") { dialog, _ ->
            var ID = EditID.text.toString()
            var condition = EditCondition.text.toString()
            var precent = EditPrecent.text.toString()
            var model=EditModel.text.toString()

            // Здесь можно обработать введенные данные (value1 и value2)
            //запись в бд
            if(ID.isEmpty() || condition.isEmpty() || precent.isEmpty()||model.isEmpty()){
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Не сохранено")
                    .setMessage("Необходимо заполнить все поля")
                    .setPositiveButton("ОК") {
                            dialog, id ->  dialog.cancel()
                    }
                builder.create()
            }
            else{
                //сама запись
                databaseHelper.addBusData(ID.toLong(), condition, precent ,model)
                printList(recucle)
            }

        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }*/
}