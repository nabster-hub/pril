//package com.example.tateknew
//
//import android.content.ContentValues
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import android.util.Log
//import com.example.tateknew.ui.Tps.TPsItem
//import com.example.tateknew.ui.getData.MtrItem
//import org.json.JSONObject
//
//class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
//
//    companion object {
//        private const val DATABASE_NAME = "app_database.db"
//        private const val DATABASE_VERSION = 1
//        private const val TABLE_OBJECTS = "objects"
//        private const val TABLE_MTRS = "mtrs"
//        private const val TABLE_ABONENTS = "abonents"
//    }
//
//    override fun onCreate(db: SQLiteDatabase) {
//        val createObjectsTable = """
//            CREATE TABLE $TABLE_OBJECTS (
//                id INTEGER PRIMARY KEY,
//                name TEXT,
//                base_id INTEGER,
//                full_name TEXT,
//                created_at TEXT,
//                updated_at TEXT
//            )
//        """.trimIndent()
//
//        val createMtrsTable = """
//            CREATE TABLE $TABLE_MTRS (
//                id TEXT PRIMARY KEY,
//                abonent_id TEXT,
//                nobj_id TEXT,
//                base_id INTEGER,
//                name TEXT,
//                pu_name TEXT,
//                item_no TEXT,
//                status INTEGER,
//                ecap_id INTEGER,
//                sredrashod INTEGER,
//                vl TEXT,
//                ktt INTEGER,
//                created_at TEXT,
//                updated_at TEXT
//            )
//        """.trimIndent()
//
//        val createAbonentsTable = """
//            CREATE TABLE $TABLE_ABONENTS (
//                client_id TEXT PRIMARY KEY,
//                ctt TEXT,
//                ct INTEGER,
//                name TEXT,
//                client_no TEXT,
//                address TEXT,
//                base_id INTEGER,
//                client_gr TEXT,
//                street TEXT,
//                home TEXT,
//                flat TEXT,
//                created_at TEXT,
//                updated_at TEXT
//            )
//        """.trimIndent()
//
//        db.execSQL(createObjectsTable)
//        db.execSQL(createMtrsTable)
//        db.execSQL(createAbonentsTable)
//        val cursor = db.rawQuery("SELECT sqlite_version()", null)
//        if (cursor.moveToFirst()) {
//            val sqliteVersion = cursor.getString(0)
//            Log.d("DatabaseHelper", "SQLite version: $sqliteVersion")
//        }
//        cursor.close()
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS $TABLE_OBJECTS")
//        db.execSQL("DROP TABLE IF EXISTS $TABLE_MTRS")
//        db.execSQL("DROP TABLE IF EXISTS $TABLE_ABONENTS")
//        onCreate(db)
//    }
//
//    fun getAllObjects(): List<TPsItem> {
//        val objects = mutableListOf<TPsItem>()
//        val db = readableDatabase
//        val cursor = db.query(TABLE_OBJECTS, null, null, null, null, null, null)
//        if (cursor.moveToFirst()) {
//            do {
//                val tpsItem = TPsItem(
//                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
//                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
//                    baseId = cursor.getInt(cursor.getColumnIndexOrThrow("base_id")),
//                    fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
//                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
//                    updatedAt = cursor.getString(cursor.getColumnIndexOrThrow("updated_at"))
//                )
//                objects.add(tpsItem)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        return objects
//    }
//
//    fun getMtrsByObjectId(objectId: Int): List<MtrItem> {
//        val mtrs = mutableListOf<MtrItem>()
//        val db = readableDatabase
//        val cursor = db.query(TABLE_MTRS, null, "nobj_id = ?", arrayOf(objectId.toString()), null, null, null)
//        if (cursor.moveToFirst()) {
//            do {
//                val mtrItem = MtrItem(
//                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
//                    abonentId = cursor.getLong(cursor.getColumnIndexOrThrow("abonent_id")),
//                    nobjId = cursor.getInt(cursor.getColumnIndexOrThrow("nobj_id")),
//                    baseId = cursor.getInt(cursor.getColumnIndexOrThrow("base_id")),
//                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
//                    puName = cursor.getString(cursor.getColumnIndexOrThrow("pu_name")),
//                    itemNo = cursor.getString(cursor.getColumnIndexOrThrow("item_no")),
//                    status = cursor.getInt(cursor.getColumnIndexOrThrow("status")),
//                    ecapId = cursor.getInt(cursor.getColumnIndexOrThrow("ecap_id")),
//                    sredrashod = cursor.getInt(cursor.getColumnIndexOrThrow("sredrashod")),
//                    vl = cursor.getString(cursor.getColumnIndexOrThrow("vl")),
//                    ktt = cursor.getInt(cursor.getColumnIndexOrThrow("ktt")),
//                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
//                    updatedAt = cursor.getString(cursor.getColumnIndexOrThrow("updated_at"))
//                )
//                mtrs.add(mtrItem)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        return mtrs
//    }
//
//
//    fun insertObject(obj: JSONObject) {
//        val db = this.writableDatabase
//        val values = ContentValues().apply {
//            put("id", obj.optInt("id"))
//            put("name", obj.optString("name"))
//            put("base_id", obj.optInt("base_id"))
//            put("full_name", obj.optString("full_name"))
//            put("created_at", obj.optString("created_at"))
//            put("updated_at", obj.optString("updated_at"))
//        }
//        try {
//            val id = obj.optInt("id")
//            val cursor = db.query(TABLE_OBJECTS, arrayOf("id"), "id = ?", arrayOf(id.toString()), null, null, null)
//            if (cursor.moveToFirst()) {
//                // Запись существует, обновляем её
//                db.update(TABLE_OBJECTS, values, "id = ?", arrayOf(id.toString()))
//            } else {
//                // Записи нет, вставляем новую
//                db.insertOrThrow(TABLE_OBJECTS, null, values)
//            }
//            cursor.close()
//            val mtrsArray = obj.optJSONArray("mtrs")
//            if (mtrsArray != null) {
//                for (i in 0 until mtrsArray.length()) {
//                    insertMtr(mtrsArray.getJSONObject(i))
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("DatabaseHelper", "Error inserting object: $values", e)
//        }
//    }
//
//    fun insertMtr(mtr: JSONObject) {
//        val db = this.writableDatabase
//        Log.d("DatabaseManager", "abonent_id: ${mtr.getLong("abonent_id")}")
//        val values = ContentValues().apply {
//            put("id", mtr.optLong("id"))
//            put("abonent_id", mtr.optLong("abonent_id"))
//            put("nobj_id", mtr.optInt("nobj_id"))
//            put("base_id", mtr.optInt("base_id"))
//            put("name", mtr.optString("name"))
//            put("pu_name", mtr.optString("pu_name"))
//            put("item_no", mtr.optString("item_no"))
//            put("status", mtr.optInt("status"))
//            put("ecap_id", mtr.optInt("ecap_id"))
//            put("sredrashod", if (mtr.isNull("sredrashod")) null else mtr.optInt("sredrashod"))
//            put("vl", mtr.optString("vl"))
//            put("ktt", mtr.optInt("ktt"))
//            put("created_at", mtr.optString("created_at"))
//            put("updated_at", mtr.optString("updated_at"))
//        }
//
//        try {
//            val id = mtr.optLong("id")
//            val cursor = db.query(TABLE_MTRS, arrayOf("id"), "id = ?", arrayOf(id.toString()), null, null, null)
//            if (cursor.moveToFirst()) {
//                // Запись существует, обновляем её
//                db.update(TABLE_MTRS, values, "id = ?", arrayOf(id.toString()))
//            } else {
//                // Записи нет, вставляем новую
//                db.insertOrThrow(TABLE_MTRS, null, values)
//            }
//            cursor.close()
//
//            val abonent = mtr.optJSONObject("abonents")
//            if (abonent != null) {
//                insertAbonent(abonent)
//            }
//        } catch (e: Exception) {
//            Log.e("DatabaseHelper", "Error inserting MTR: $values", e)
//        }
//    }
//
//    fun insertAbonent(abonent: JSONObject) {
//        val db = this.writableDatabase
//        val values = ContentValues().apply {
//            put("client_id", abonent.optLong("client_id"))
//            put("ctt", abonent.optString("ctt"))
//            put("ct", abonent.optInt("ct"))
//            put("name", abonent.optString("name"))
//            put("client_no", abonent.optLong("client_no"))
//            put("address", abonent.optString("address"))
//            put("base_id", abonent.optInt("base_id"))
//            put("client_gr", abonent.optString("client_gr"))
//            put("street", abonent.optString("street"))
//            put("home", abonent.optString("home"))
//            put("flat", abonent.optString("flat"))
//            put("created_at", abonent.optString("created_at"))
//            put("updated_at", abonent.optString("updated_at"))
//        }
//        try {
//            val id = abonent.optLong("client_id")
//            val cursor = db.query(TABLE_ABONENTS, arrayOf("client_id"), "client_id = ?", arrayOf(id.toString()), null, null, null)
//            if (cursor.moveToFirst()) {
//                // Запись существует, обновляем её
//                db.update(TABLE_ABONENTS, values, "client_id = ?", arrayOf(id.toString()))
//            } else {
//                // Записи нет, вставляем новую
//                db.insertOrThrow(TABLE_ABONENTS, null, values)
//            }
//            cursor.close()
//
//        } catch (e: Exception) {
//            Log.e("DatabaseHelper", "Error inserting abonent: $values", e)
//        }
//    }
//}
