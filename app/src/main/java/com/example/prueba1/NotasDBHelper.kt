package com.example.prueba1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class NotasDBHelper(context: Context) : SQLiteOpenHelper(context, "notas.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE notas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dia TEXT,
                hora TEXT,
                contenido TEXT,
                completada INTEGER DEFAULT 0,
                importante INTEGER DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS notas")
        onCreate(db)
    }

    fun insertarNota(dia: String, hora: String, contenido: String, completada: Boolean = false, importante: Boolean = false) {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("dia", dia)
            put("hora", hora)
            put("contenido", contenido)
            put("completada", if (completada) 1 else 0)
            put("importante", if (importante) 1 else 0)
        }
        db.insert("notas", null, valores)
        db.close()
    }

    fun obtenerNotas(): List<NotaUI> {
        val db = readableDatabase
        val lista = mutableListOf<NotaUI>()
        val cursor = db.rawQuery("SELECT id, dia, hora, contenido, completada, importante FROM notas", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val dia = cursor.getString(1)
                val hora = cursor.getString(2)
                val contenido = cursor.getString(3)
                val completada = cursor.getInt(4) == 1
                val importante = cursor.getInt(5) == 1
                val texto = "$dia - $hora\n$contenido"
                lista.add(NotaUI(id, texto, completada, importante))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun eliminarNotaPorId(id: Int) {
        val db = writableDatabase
        db.delete("notas", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun actualizarEstadoPorId(id: Int, completada: Boolean, importante: Boolean) {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("completada", if (completada) 1 else 0)
            put("importante", if (importante) 1 else 0)
        }
        db.update("notas", valores, "id = ?", arrayOf(id.toString()))
        db.close()
    }
}




