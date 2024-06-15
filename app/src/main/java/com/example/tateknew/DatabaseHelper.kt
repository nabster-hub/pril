package com.example.tateknew

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONObject

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_OBJECTS_TABLE)
        db.execSQL(CREATE_MTRS_TABLE)
        db.execSQL(CREATE_ABONENTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OBJECTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MTRS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ABONENTS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_OBJECTS = "Objects"
        const val TABLE_MTRS = "Mtrs"
        const val TABLE_ABONENTS = "Abonents"

        private const val CREATE_OBJECTS_TABLE = """
            CREATE TABLE $TABLE_OBJECTS (
                id INTEGER PRIMARY KEY,
                name TEXT,
                base_id INTEGER,
                full_name TEXT,
                created_at TEXT,
                updated_at TEXT
            )
        """

        private const val CREATE_MTRS_TABLE = """
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
                updated_at TEXT,
                FOREIGN KEY(nobj_id) REFERENCES $TABLE_OBJECTS(id)
            )
        """

        private const val CREATE_ABONENTS_TABLE = """
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
        """
    }

    fun insertObject(db: SQLiteDatabase, obj: JSONObject) {
        val values = ContentValues().apply {
            put("id", obj.getInt("id"))
            put("name", obj.getString("name"))
            put("base_id", obj.getInt("base_id"))
            put("full_name", obj.getString("full_name"))
            put("created_at", obj.getString("created_at"))
            put("updated_at", obj.getString("updated_at"))
        }
        db.insert(DatabaseHelper.TABLE_OBJECTS, null, values)

        val mtrsArray = obj.getJSONArray("mtrs")
        for (i in 0 until mtrsArray.length()) {
            insertMtr(db, mtrsArray.getJSONObject(i))
        }
    }

    fun insertMtr(db: SQLiteDatabase, mtr: JSONObject) {
        val values = ContentValues().apply {
            put("id", mtr.getInt("id"))
            put("abonent_id", mtr.getInt("abonent_id"))
            put("nobj_id", mtr.getInt("nobj_id"))
            put("base_id", mtr.getInt("base_id"))
            put("name", mtr.getString("name"))
            put("pu_name", mtr.getString("pu_name"))
            put("item_no", mtr.getString("item_no"))
            put("status", mtr.getInt("status"))
            put("ecap_id", mtr.getInt("ecap_id"))
            put("sredrashod", mtr.getInt("sredrashod"))
            put("vl", mtr.getString("vl"))
            put("ktt", mtr.getInt("ktt"))
            put("created_at", mtr.getString("created_at"))
            put("updated_at", mtr.getString("updated_at"))
        }
        db.insert(DatabaseHelper.TABLE_MTRS, null, values)

        val abonent = mtr.getJSONObject("abonents")
        insertAbonent(db, abonent)
    }

    fun insertAbonent(db: SQLiteDatabase, abonent: JSONObject) {
        val values = ContentValues().apply {
            put("client_id", abonent.getInt("client_id"))
            put("ctt", abonent.getString("ctt"))
            put("ct", abonent.getInt("ct"))
            put("name", abonent.getString("name"))
            put("client_no", abonent.getInt("client_no"))
            put("address", abonent.getString("address"))
            put("base_id", abonent.getInt("base_id"))
            put("client_gr", abonent.getString("client_gr"))
            put("street", abonent.getString("street"))
            put("home", abonent.getString("home"))
            put("flat", abonent.getString("flat"))
            put("created_at", abonent.getString("created_at"))
            put("updated_at", abonent.getString("updated_at"))
        }
        db.insert(DatabaseHelper.TABLE_ABONENTS, null, values)
    }

    fun getObject(db: SQLiteDatabase, objectId: Int): Cursor {
        return db.query(
            DatabaseHelper.TABLE_OBJECTS,
            null,
            "id = ?",
            arrayOf(objectId.toString()),
            null,
            null,
            null
        )
    }

    fun getMtrsForObject(db: SQLiteDatabase, objectId: Int): Cursor {
        return db.query(
            DatabaseHelper.TABLE_MTRS,
            null,
            "nobj_id = ?",
            arrayOf(objectId.toString()),
            null,
            null,
            null
        )
    }

    fun getAbonent(db: SQLiteDatabase, clientId: Int): Cursor {
        return db.query(
            DatabaseHelper.TABLE_ABONENTS,
            null,
            "client_id = ?",
            arrayOf(clientId.toString()),
            null,
            null,
            null
        )
    }
}
