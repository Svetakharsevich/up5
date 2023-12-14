package com.example.avtopark

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class DBHelper(private val appContext: Context) : SQLiteOpenHelper(appContext,DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "avto.db"
        const val DATABASE_VERSION = 1

        const val TABLE_BUS = "Автобусы"
        const val COLUMN_ID = "Номер_автобуса"
        const val COLUMN_Condition = "Состояние"
        const val COLUMN_Percent = "Процент амортизации автобуса"
        const val COLUMN_Model = "Модель автобуса"

        const val TABLE_DRIVER = "Водители"
        const val COLUMN_ID_DRIVER = "Идентификатор водителя"
        const val COLUMN_FIO = "ФИО водителя"
        const val COLUMN_Price = "Зарплата"
        const val COLUMN_Premia = "Дополнительная_премия"
        const val COLUMN_Sity = "Город"

        const val TABLE_ROUTES = "Маршруты"
        const val COLUMN_ID_ROUTES = "ID_маршрута"
        const val COLUMN_Stoping = "Остановки"
        const val COLUMN_Sity_Routes = "Город"

        const val TABLE_ROUTES_DRIVER = "Маршруты-Водители"
        const val COLUMN_ID_ROUTES_DRIVER = "Маршрут"
        const val COLUMN_ID_DRIVER_R = "Водитель"
        const val COLUMN_TIME_DRIVER="Время"


        const val TABLE_BUS_DRIVER = "Автобусы-Водители"
        const val COLUMN_ID_BUS_DRIVER = "Автобус"
        const val COLUMN_ID_DRIVER_B = "Водитель"


        const val TABLE_REGISTRDRIVER="Регистрация_Водители"
        const val COLUMN_ID_DRIVER_REGISTR="id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_LOGIN= "login"
        const val COLUMN_PASSWORD="password"

    }
    init {
        // Переместите вызов копирования базы данных сюда
        copyDatabaseFromAssets(appContext)
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        
        try {
            Log.d("MyLog", "Creating Horoscope table...")

            // Проверяем, существует ли таблица
            val checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_ROUTES'"
           val cursor= p0?.rawQuery(checkTableQuery,null)
            val tableExists = cursor!!.count > 0
            cursor.close()

            if (!tableExists) {
                // Если таблица не существует, вызываем метод для копирования базы данных из assets
                copyDatabaseFromAssets(appContext)
                Log.d("MyLog", "Users table does not exist. Copied from assets.")
            } else {
                Log.d("MyLog", "Users table already exists.")
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Error checking Users table: ${e.message}")
            e.printStackTrace()
        }
    }
    private fun checkDatabaseExists(context: Context): Boolean {
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        return dbPath.exists()
    }
    private fun copyDatabaseFromAssets(context: Context) {
        val dbPath = context.getDatabasePath(DATABASE_NAME).absolutePath
        if (!checkDatabaseExists(context)) {
            try {
                val inputStream: InputStream = context.assets.open(DATABASE_NAME)
                val outputStream = FileOutputStream(dbPath)
                val buffer = ByteArray(1024)
                var length: Int

                while ((inputStream.read(buffer)).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                Log.d("MyLog", "Database copied from assets successfully.")
            } catch (e: IOException) {
                Log.e("MyLog", "Error copying database from assets: ${e.message}")
            }
        }
        val database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
        val cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)

        while (cursor.moveToNext()) {
            val tableName = cursor.getString(0)
            Log.d("MyLog", "Table Name: $tableName")
        }

        cursor.close()
        database.close()
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
    //Маршруты
    fun getAllRoutesData(): List<Routes> {
        val routesDataList = mutableListOf<Routes>()
        val db = readableDatabase

        // Перед выполнением запроса, проверим снова, существует ли таблица
        val checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_ROUTES'"
        val cursor = db.rawQuery(checkTableQuery, null)
        val tableExists = cursor.count > 0
        cursor.close()

        if (!tableExists) {
            // Если таблица не существует, вызываем метод для копирования базы данных из assets
            copyDatabaseFromAssets(appContext)
            Log.d("MyLog", "Маршруты table does not exist. Copied from assets.")
        } else {
            Log.d("MyLog", "Маршруты table already exists.")
        }

        val selectQuery = "SELECT * FROM $TABLE_ROUTES"
        val selectCursor: Cursor = db.rawQuery(selectQuery, null)

        try {
            while (selectCursor.moveToNext()) {
                val idColumnIndex = selectCursor.getColumnIndex(COLUMN_ID_ROUTES)
                val StopingColumnIndex = selectCursor.getColumnIndex(COLUMN_Stoping)
                val SityColumnIndex = selectCursor.getColumnIndex(COLUMN_Sity_Routes)

                if (idColumnIndex != -1 && StopingColumnIndex != -1 && SityColumnIndex != -1) {
                    val id = selectCursor.getLong(idColumnIndex)
                    val login = selectCursor.getString(StopingColumnIndex)
                    val email = selectCursor.getString(SityColumnIndex)

                    val horoscopeData = com.example.avtopark.Routes(id, login, email)
                    routesDataList.add(horoscopeData)
                } else {
                    Log.e("MyLog", "Column index not found")
                }
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Error while reading data from the database: ${e.message}")
        } finally {
            selectCursor.close()
            db.close()
        }

        return routesDataList
    }

    fun addRoutesData(stoping: String, sity_routes: String) {

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_Stoping, stoping)
            put(COLUMN_Sity_Routes, sity_routes)
        }
        db.insert(TABLE_ROUTES, null, values)
        db.close()
    }
    fun deleteRoutesData(userId: Long) {
        val db = writableDatabase

        try {
            // Условие для удаления записи с определенным ID
            val whereClause = "$COLUMN_ID_ROUTES = ?"
            val whereArgs = arrayOf(userId.toString())

            // Выполняем запрос на удаление
            db.delete(TABLE_ROUTES, whereClause, whereArgs)
            Log.d("MyLog", "Маршрут with ID $userId deleted successfully.")
        } catch (e: Exception) {
            Log.e("MyLog", "Error while deleting routes: ${e.message}")
        } finally {
            db.close()
        }
    }
    fun updateRoutesData(userId: Long, stoping: String, sity_routes: String) {
        val db = writableDatabase

        try {
            // Условие для обновления записи с определенным ID
            val whereClause = "$COLUMN_ID_ROUTES = ?"
            val whereArgs = arrayOf(userId.toString())

            // Создаем объект ContentValues с обновленными данными
            val values = ContentValues().apply {
                put(COLUMN_Stoping, stoping)
                put(COLUMN_Sity_Routes, sity_routes)
            }

            // Выполняем запрос на обновление
            db.update(TABLE_ROUTES, values, whereClause, whereArgs)
            Log.d("MyLog", "Маршрут with ID $userId updated successfully.")
        } catch (e: Exception) {
            Log.e("MyLog", "Error while updating routes: ${e.message}")
        } finally {
            db.close()
        }
    }




    //Водители
    fun getAllDriversData(): List<Driver> {
        val DriverDataList = mutableListOf<Driver>()
        val db = readableDatabase

        // Перед выполнением запроса, проверим снова, существует ли таблица
        val checkTableQuery = "SELECT name FROM sqlite_sequence WHERE type='table' AND name='$TABLE_DRIVER'"
        val cursor = db.rawQuery(checkTableQuery, null)
        val tableExists = cursor.count > 0
        cursor.close()

        if (!tableExists) {
            // Если таблица не существует, вызываем метод для копирования базы данных из assets
            copyDatabaseFromAssets(appContext)
            Log.d("MyLog", "Водители table does not exist. Copied from assets.")
        } else {
            Log.d("MyLog", "Водители table already exists.")
        }

        val selectQuery = "SELECT * FROM $TABLE_DRIVER"
        val selectCursor: Cursor = db.rawQuery(selectQuery, null)

        try {
            while (selectCursor.moveToNext()) {
                val idColumnIndex = selectCursor.getColumnIndex(COLUMN_ID_DRIVER)
                val fioColumnIndex = selectCursor.getColumnIndex(COLUMN_FIO)
                val priceColumnIndex = selectCursor.getColumnIndex(COLUMN_Price)
                val premaiColumnIndex = selectCursor.getColumnIndex(COLUMN_Premia)
                val sityColumnIndex = selectCursor.getColumnIndex(COLUMN_Sity)

                if (idColumnIndex != -1 && fioColumnIndex != -1 && premaiColumnIndex != -1
                    && priceColumnIndex != -1 && sityColumnIndex != -1) {
                    val id = selectCursor.getLong(idColumnIndex)
                    val fio = selectCursor.getString(fioColumnIndex)
                    val premia = selectCursor.getInt(premaiColumnIndex)
                    val sity = selectCursor.getString(sityColumnIndex)
                    val price = selectCursor.getInt(priceColumnIndex)

                    val turData = com.example.avtopark.Driver(id, fio, price, premia, sity)
                    DriverDataList.add(turData)
                } else {
                    Log.e("MyLog", "Column index not found")
                }
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Error while reading data from the database: ${e.message}")
        } finally {
            selectCursor.close()
            db.close()
        }

        return DriverDataList
    }


    fun addDriverData(FIO: String, price: Int,premia:Int,sity:String) {

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FIO, FIO)
            put(COLUMN_Price, price)
            put(COLUMN_Premia,premia)
            put(COLUMN_Sity,sity)
        }
        db.insert(TABLE_DRIVER, null, values)
        db.close()
    }
    fun deleteDriverData(driverId: Long) {
        val db = writableDatabase

        try {
            // Условие для удаления записи с определенным ID
            val whereClause = "$COLUMN_ID_DRIVER = ?"
            val whereArgs = arrayOf(driverId.toString())

            // Выполняем запрос на удаление
            db.delete(TABLE_DRIVER, whereClause, whereArgs)
            Log.d("MyLog", "Водитель with ID $driverId deleted successfully.")
        } catch (e: Exception) {
            Log.e("MyLog", "Error while deleting driver: ${e.message}")
        } finally {
            db.close()
        }
    }
    fun updateDriverData(driverId: Long, FIO: String, price: Int,premia:Int,sity:String) {
        val db = writableDatabase

        try {
            // Условие для обновления записи с определенным ID
            val whereClause = "$COLUMN_ID_DRIVER = ?"
            val whereArgs = arrayOf(driverId.toString())

            // Создаем объект ContentValues с обновленными данными
            val values = ContentValues().apply {
                put(COLUMN_FIO, FIO)
                put(COLUMN_Price, price)
                put(COLUMN_Premia,premia)
                put(COLUMN_Sity,sity)
            }

            // Выполняем запрос на обновление
            db.update(TABLE_DRIVER, values, whereClause, whereArgs)
            Log.d("MyLog", "Водитель with ID $driverId updated successfully.")
        } catch (e: Exception) {
            Log.e("MyLog", "Error while updating driver: ${e.message}")
        } finally {
            db.close()
        }
    }


    @SuppressLint("Range")
    fun getGroupedDataByCondition(): Map<String, List<Bus>> {
        val groupedData = mutableMapOf<String, List<Bus>>()

        val query = "SELECT * FROM ${TABLE_BUS} ORDER BY ${COLUMN_Condition}"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val bus = Bus(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_Condition)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_Percent)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_Model))
                )

                val condition = bus.condition
                if (groupedData.containsKey(condition)) {
                    // Если ключ (состояние) уже существует, добавляем автобус в существующий список
                    val busList = groupedData[condition] as MutableList
                    busList.add(bus)
                } else {
                    // Если ключ (состояние) отсутствует, создаем новый список и добавляем его в карту
                    val newBusList = mutableListOf(bus)
                    groupedData[condition] = newBusList
                }
            }
        } finally {
            cursor.close()
            db.close()
        }

        return groupedData
    }
    //Автобусы
    fun getAllBusData(): List<Bus> {
        val BusDataList = mutableListOf<Bus>()
        val db = readableDatabase

        // Перед выполнением запроса, проверим снова, существует ли таблица
        val checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_BUS'"
        val cursor = db.rawQuery(checkTableQuery, null)
        val tableExists = cursor.count > 0
        cursor.close()

        if (!tableExists) {
            // Если таблица не существует, вызываем метод для копирования базы данных из assets
            copyDatabaseFromAssets(appContext)
            Log.d("MyLog", "Автобусы table does not exist. Copied from assets.")
        } else {
            Log.d("MyLog", "Автобусы table already exists.")
        }

        val selectQuery = "SELECT * FROM $TABLE_BUS"
        val selectCursor: Cursor = db.rawQuery(selectQuery, null)

        try {
            while (selectCursor.moveToNext()) {
                val idColumnIndex = selectCursor.getColumnIndex(COLUMN_ID)
                val conditionColumnIndex = selectCursor.getColumnIndex(COLUMN_Condition)
                val precentColumnIndex = selectCursor.getColumnIndex(COLUMN_Percent)
                val modelColumnIndex = selectCursor.getColumnIndex(COLUMN_Model)

                if (idColumnIndex != -1 && conditionColumnIndex != -1 && precentColumnIndex != -1
                    && modelColumnIndex != -1) {
                    val id = selectCursor.getLong(idColumnIndex)
                    val condition = selectCursor.getString(conditionColumnIndex)
                    val precent = selectCursor.getInt(precentColumnIndex)
                    val model = selectCursor.getString(modelColumnIndex)

                    val busData = com.example.avtopark.Bus(id, condition, precent, model)
                    BusDataList.add(busData)
                } else {
                    Log.e("MyLog", "Column index not found")
                }
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Error while reading data from the database: ${e.message}")
        } finally {
            selectCursor.close()
            db.close()
        }

        return BusDataList
    }


    fun addBusData(condition: String, precent: String, model:String) {

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_Condition, condition)
            put(COLUMN_Percent, precent)
            put(COLUMN_Model,model)
        }
        db.insert(TABLE_BUS, null, values)
        db.close()
    }
    fun deleteBusData(busId: Long) {
        val db = writableDatabase

        try {
            // Условие для удаления записи с определенным ID
            val whereClause = "$COLUMN_ID = ?"
            val whereArgs = arrayOf(busId.toString())

            // Выполняем запрос на удаление
            db.delete(TABLE_BUS, whereClause, whereArgs)
            Log.d("MyLog", "Автобусы with ID $busId deleted successfully.")
        } catch (e: Exception) {
            Log.e("MyLog", "Error while deleting bus: ${e.message}")
        } finally {
            db.close()
        }
    }
    fun updateBusData(busId: Long, condition: String, precent: String, model:String) {
        val db = writableDatabase

        try {
            // Условие для обновления записи с определенным ID
            val whereClause = "$COLUMN_ID = ?"
            val whereArgs = arrayOf(busId.toString())

            // Создаем объект ContentValues с обновленными данными
            val values = ContentValues().apply {
                put(COLUMN_Condition, condition)
                put(COLUMN_Percent, precent)
                put(COLUMN_Model,model)
            }

            // Выполняем запрос на обновление
            db.update(TABLE_BUS, values, whereClause, whereArgs)
            Log.d("MyLog", "Автобус with ID $busId updated successfully.")
        } catch (e: Exception) {
            Log.e("MyLog", "Error while updating bus: ${e.message}")
        } finally {
            db.close()
        }
    }


    fun getAllROUTESDRIVERData(): List<RoutesDriver> {
        val routesDataList = mutableListOf<RoutesDriver>()
        val db = readableDatabase

        // Перед выполнением запроса, проверим снова, существует ли таблица
        val checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_ROUTES_DRIVER'"
        val cursor = db.rawQuery(checkTableQuery, null)
        val tableExists = cursor.count > 0
        cursor.close()

        if (!tableExists) {
            // Если таблица не существует, вызываем метод для копирования базы данных из assets
            copyDatabaseFromAssets(appContext)
            Log.d("MyLog", "Маршруты table does not exist. Copied from assets.")
        } else {
            Log.d("MyLog", "Маршруты table already exists.")
        }

        val selectQuery = "SELECT * FROM $TABLE_ROUTES_DRIVER"
        val selectCursor: Cursor = db.rawQuery(selectQuery, null)

        try {
            while (selectCursor.moveToNext()) {
                val routesColumnIndex = selectCursor.getColumnIndex(COLUMN_ID_ROUTES_DRIVER)
                val driverColumnIndex = selectCursor.getColumnIndex(COLUMN_ID_DRIVER_R)
                val driverColumnIndexTime=selectCursor.getColumnIndex(COLUMN_TIME_DRIVER)

                if (routesColumnIndex != -1 && driverColumnIndex != -1) {
                    val login = selectCursor.getString(routesColumnIndex)
                    val email = selectCursor.getString(driverColumnIndex)
                    val time = selectCursor.getString(driverColumnIndexTime)

                    val horoscopeData = com.example.avtopark.RoutesDriver(login, email,time)
                    routesDataList.add(horoscopeData)
                } else {
                    Log.e("MyLog", "Column index not found")
                }
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Error while reading data from the database: ${e.message}")
        } finally {
            selectCursor.close()
            db.close()
        }

        return routesDataList
    }

    fun getAllBUSDRIVERData(): List<Bus_Driver> {
        val routesDataList = mutableListOf<Bus_Driver>()
        val db = readableDatabase

        // Перед выполнением запроса, проверим снова, существует ли таблица
        val checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_BUS_DRIVER'"
        val cursor = db.rawQuery(checkTableQuery, null)
        val tableExists = cursor.count > 0
        cursor.close()

        if (!tableExists) {
            // Если таблица не существует, вызываем метод для копирования базы данных из assets
            copyDatabaseFromAssets(appContext)
            Log.d("MyLog", "Маршруты table does not exist. Copied from assets.")
        } else {
            Log.d("MyLog", "Маршруты table already exists.")
        }

        val selectQuery = "SELECT * FROM $TABLE_BUS_DRIVER"
        val selectCursor: Cursor = db.rawQuery(selectQuery, null)

        try {
            while (selectCursor.moveToNext()) {
                val routesColumnIndex = selectCursor.getColumnIndex(COLUMN_ID_BUS_DRIVER)
                val driverColumnIndex = selectCursor.getColumnIndex(COLUMN_ID_DRIVER_B)

                if (routesColumnIndex != -1 && driverColumnIndex != -1) {
                    val login = selectCursor.getLong(routesColumnIndex)
                    val email = selectCursor.getString(driverColumnIndex)

                    val horoscopeData = com.example.avtopark.Bus_Driver(login, email)
                    routesDataList.add(horoscopeData)
                } else {
                    Log.e("MyLog", "Column index not found")
                }
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Error while reading data from the database: ${e.message}")
        } finally {
            selectCursor.close()
            db.close()
        }

        return routesDataList
    }
    fun getAllLOGINPASSData(): List<InfoAboutDrivers> {
        val routesDataList = mutableListOf<InfoAboutDrivers>()
        val db = readableDatabase

        // Перед выполнением запроса, проверим снова, существует ли таблица
        val checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='$TABLE_REGISTRDRIVER'"
        val cursor = db.rawQuery(checkTableQuery, null)
        val tableExists = cursor.count > 0
        cursor.close()

        if (!tableExists) {
            // Если таблица не существует, вызываем метод для копирования базы данных из assets
            copyDatabaseFromAssets(appContext)
            Log.d("MyLog", "Маршруты table does not exist. Copied from assets.")

            // После копирования базы данных, переоткрываем ее
            db.close()
            return emptyList()
        } else {
            Log.d("MyLog", "Маршруты table already exists.")
        }

        val selectQuery = "SELECT * FROM $TABLE_REGISTRDRIVER"
        val selectCursor: Cursor = db.rawQuery(selectQuery, null)

        try {
            while (selectCursor.moveToNext()) {
                val IDColumnIndex = selectCursor.getColumnIndex(COLUMN_ID_DRIVER_REGISTR)
                val emailColumnIndex = selectCursor.getColumnIndex(COLUMN_EMAIL)
                val loginColumnIndex = selectCursor.getColumnIndex(COLUMN_LOGIN)
                val passwordColumnIndex = selectCursor.getColumnIndex(COLUMN_PASSWORD)

                if (IDColumnIndex != -1 && emailColumnIndex != -1&&loginColumnIndex != -1&&passwordColumnIndex != -1) {
                    val id = selectCursor.getInt(IDColumnIndex)
                    val email=selectCursor.getString(emailColumnIndex)
                    val login = selectCursor.getString(loginColumnIndex)
                    val password=selectCursor.getString(passwordColumnIndex)

                    val horoscopeData = com.example.avtopark.InfoAboutDrivers(id,email,login, email)
                    routesDataList.add(horoscopeData)
                } else {
                    Log.e("MyLog", "Column index not found")
                }
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Error while reading data from the database: ${e.message}")
        } finally {
            selectCursor.close()
            db.close()
        }

        return routesDataList
    }
    fun saveDriverIdToSharedPreferences(context: Context, driverId: Int) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("driverId", driverId)
        editor.apply()
    }
    @SuppressLint("Range")
    fun getDriverInfoByEmailAndPassword(email: String, login:String, password: String): InfoAboutDrivers? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_REGISTRDRIVER,
            arrayOf(COLUMN_ID_DRIVER_REGISTR, COLUMN_EMAIL, COLUMN_LOGIN, COLUMN_PASSWORD),
            "$COLUMN_EMAIL=? AND $COLUMN_PASSWORD=?",
            arrayOf(email, password),
            null, null, null, null
        )

        var driver: InfoAboutDrivers? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_DRIVER_REGISTR))
            val onelogin = cursor.getString(cursor.getColumnIndex(COLUMN_LOGIN))
            val oneemail = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
            val onepassword = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))

            // Добавим проверку, что login не является пустым
            if (email == oneemail && password == onepassword && login.isNotEmpty() && login == onelogin) {
                driver = InfoAboutDrivers(id, oneemail, onelogin, onepassword)
            }
        }

        cursor.close()
        db.close()
        return driver
    }
    @SuppressLint("Range")
    fun getAllBusRoutesata(): List<BusRouteItem> {
        val routesDataList = mutableListOf<BusRouteItem>()

        val query = """
            SELECT ${TABLE_ROUTES_DRIVER}.${COLUMN_ID_ROUTES_DRIVER}, ${TABLE_ROUTES_DRIVER}.${COLUMN_ID_DRIVER_R},
                   ${TABLE_BUS_DRIVER}.${COLUMN_ID_BUS_DRIVER}, ${TABLE_BUS_DRIVER}.${COLUMN_ID_DRIVER_B}
            FROM ${TABLE_ROUTES_DRIVER}
            INNER JOIN ${TABLE_BUS_DRIVER} ON ${TABLE_ROUTES_DRIVER}.${COLUMN_ID_ROUTES_DRIVER} = ${TABLE_BUS_DRIVER}.${COLUMN_ID_BUS_DRIVER}
        """.trimIndent()

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val idRouteDriver = cursor.getLong(cursor.getColumnIndex(COLUMN_ID_ROUTES_DRIVER))
            val idDriverRoute = cursor.getString(cursor.getColumnIndex(COLUMN_ID_DRIVER_R))
            val idBusDriver = cursor.getLong(cursor.getColumnIndex(COLUMN_ID_BUS_DRIVER))
            val idDriverBus = cursor.getString(cursor.getColumnIndex(COLUMN_ID_DRIVER_B))

            routesDataList.add(com.example.avtopark.BusRouteItem(idRouteDriver, idDriverRoute, idBusDriver, idDriverBus))
        }

        cursor.close()
        db.close()

        return routesDataList
    }

}