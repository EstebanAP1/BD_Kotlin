package com.example.bd_kotlin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "users.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_LASTNAME = "lastname"
        const val COLUMN_AGE = "age"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"

        private const val CREATE_TABLE="""
        CREATE TABLE $TABLE_NAME(
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT,
            $COLUMN_LASTNAME TEXT,
            $COLUMN_AGE INTEGER,
            $COLUMN_GENDER TEXT NOT NULL,
            $COLUMN_PHONE TEXT NOT NULL,
            $COLUMN_EMAIL TEXT NOT NULL
            )    
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertUser(name: String, lastname: String, age: Int, gender: String, phone: String, email: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_LASTNAME, lastname)
            put(COLUMN_AGE, age)
            put(COLUMN_GENDER, gender)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
        }

        try {
            val result = db.insert(TABLE_NAME, null, values)
            db.close()
            return result != -1L
        } catch (e: Exception) {
            db.close()
            return false
        }
    }

    fun getAllUsers(): List<Map<String, String>> {
        val usersList = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_LASTNAME, COLUMN_AGE, COLUMN_GENDER, COLUMN_PHONE, COLUMN_EMAIL), null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val user = mapOf(
                    COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    COLUMN_NAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    COLUMN_LASTNAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LASTNAME)),
                    COLUMN_AGE to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)).toString(),
                    COLUMN_GENDER to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                    COLUMN_PHONE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    COLUMN_EMAIL to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                )
                usersList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return usersList
    }
}
