package com.example.tateknew

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONObject

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_database.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_OBJECTS = "objects"
        private const val TABLE_MTRS = "mtrs"
        private const val TABLE_ABONENTS = "abonents"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createObjectsTable = """
            CREATE TABLE $TABLE_OBJECTS (
                id INTEGER PRIMARY KEY,
                name TEXT,
                base_id INTEGER,
                full_name TEXT,
                created_at TEXT,
                updated_at TEXT
            )
        """.trimIndent()

        val createMtrsTable = """
            CREATE TABLE $TABLE_MTRS (
                id INTEGER PRIMARY KEY,
                abonent_id INTEGER,
                nobj_id INTEGER,
                base_id INTEGER,
                name TEXT,
                pu_name TEXT,
                item_no TEXT,
                status INTEGER,
                ecap_id INTEGER,
                sredrashod INTEGER,
                vl TEXT,
                ktt INTEGER,
                created_at TEXT,
                updated_at TEXT
            )
        """.trimIndent()

        val createAbonentsTable = """
            CREATE TABLE $TABLE_ABONENTS (
                client_id INTEGER PRIMARY KEY,
                ctt TEXT,
                ct INTEGER,
                name TEXT,
                client_no INTEGER,
                address TEXT,
                base_id INTEGER,
                client_gr TEXT,
                street TEXT,
                home TEXT,
                flat TEXT,
                created_at TEXT,
                updated_at TEXT
            )
        """.trimIndent()

        db.execSQL(createObjectsTable)
        db.execSQL(createMtrsTable)
        db.execSQL(createAbonentsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OBJECTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MTRS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ABONENTS")
        onCreate(db)
    }


    fun insertObject(obj: JSONObject) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id", obj.optInt("id"))
            put("name", obj.optString("name"))
            put("base_id", obj.optInt("base_id"))
            put("full_name", obj.optString("full_name"))
            put("created_at", obj.optString("created_at"))
            put("updated_at", obj.optString("updated_at"))
        }
        try {
            val id = obj.optInt("id")
            val cursor = db.query(TABLE_OBJECTS, arrayOf("id"), "id = ?", arrayOf(id.toString()), null, null, null)
            if (cursor.moveToFirst()) {
                // Запись существует, обновляем её
                db.update(TABLE_OBJECTS, values, "id = ?", arrayOf(id.toString()))
            } else {
                // Записи нет, вставляем новую
                db.insertOrThrow(TABLE_OBJECTS, null, values)
            }
            cursor.close()
            val mtrsArray = obj.optJSONArray("mtrs")
            if (mtrsArray != null) {
                for (i in 0 until mtrsArray.length()) {
                    insertMtr(mtrsArray.getJSONObject(i))
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting object: $values", e)
        }
    }

    fun insertMtr(mtr: JSONObject) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id", mtr.optInt("id"))
            put("abonent_id", mtr.optInt("abonent_id"))
            put("nobj_id", mtr.optInt("nobj_id"))
            put("base_id", mtr.optInt("base_id"))
            put("name", mtr.optString("name"))
            put("pu_name", mtr.optString("pu_name"))
            put("item_no", mtr.optString("item_no"))
            put("status", mtr.optInt("status"))
            put("ecap_id", mtr.optInt("ecap_id"))
            put("sredrashod", if (mtr.isNull("sredrashod")) null else mtr.optInt("sredrashod"))
            put("vl", mtr.optString("vl"))
            put("ktt", mtr.optInt("ktt"))
            put("created_at", mtr.optString("created_at"))
            put("updated_at", mtr.optString("updated_at"))
        }
        try {
            val id = mtr.optInt("id")
            val cursor = db.query(TABLE_MTRS, arrayOf("id"), "id = ?", arrayOf(id.toString()), null, null, null)
            if (cursor.moveToFirst()) {
                // Запись существует, обновляем её
                db.update(TABLE_MTRS, values, "id = ?", arrayOf(id.toString()))
            } else {
                // Записи нет, вставляем новую
                db.insertOrThrow(TABLE_MTRS, null, values)
            }
            cursor.close()
            db.insertOrThrow(TABLE_MTRS, null, values)
            val abonent = mtr.optJSONObject("abonents")
            if (abonent != null) {
                insertAbonent(abonent)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting MTR: $values", e)
        }
    }

    fun insertAbonent(abonent: JSONObject) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("client_id", abonent.optInt("client_id"))
            put("ctt", abonent.optString("ctt"))
            put("ct", abonent.optInt("ct"))
            put("name", abonent.optString("name"))
            put("client_no", abonent.optInt("client_no"))
            put("address", abonent.optString("address"))
            put("base_id", abonent.optInt("base_id"))
            put("client_gr", abonent.optString("client_gr"))
            put("street", abonent.optString("street"))
            put("home", abonent.optString("home"))
            put("flat", abonent.optString("flat"))
            put("created_at", abonent.optString("created_at"))
            put("updated_at", abonent.optString("updated_at"))
        }
        try {
            val id = abonent.optInt("client_id")
            val cursor = db.query(TABLE_ABONENTS, arrayOf("client_id"), "client_id = ?", arrayOf(id.toString()), null, null, null)
            if (cursor.moveToFirst()) {
                // Запись существует, обновляем её
                db.update(TABLE_ABONENTS, values, "client_id = ?", arrayOf(id.toString()))
            } else {
                // Записи нет, вставляем новую
                db.insertOrThrow(TABLE_ABONENTS, null, values)
            }
            cursor.close()
            db.insertOrThrow(TABLE_ABONENTS, null, values)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting abonent: $values", e)
        }
    }
}
