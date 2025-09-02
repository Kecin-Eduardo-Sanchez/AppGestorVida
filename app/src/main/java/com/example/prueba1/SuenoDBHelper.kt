package com.example.prueba1.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class SuenoDBHelper(context: Context) : SQLiteOpenHelper(context, "sueno_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE sueno (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "inicio TEXT, " +
                    "fin TEXT, " +
                    "tipo TEXT, " +
                    "fecha TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS sueno")
        onCreate(db)
    }

    fun insertarRegistro(inicio: String, fin: String, tipo: String, fecha: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("inicio", inicio)
            put("fin", fin)
            put("tipo", tipo)
            put("fecha", fecha)
        }
        return db.insert("sueno", null, values)
    }

    fun obtenerTodosLosRegistros(): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM sueno ORDER BY fecha DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val registro = mapOf(
                    "fecha" to cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    "hora_inicio" to cursor.getString(cursor.getColumnIndexOrThrow("inicio")),
                    "hora_fin" to cursor.getString(cursor.getColumnIndexOrThrow("fin")),
                    "tipo" to cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                )
                lista.add(registro)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}
