package com.example.prueba1.data

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SuenoDBHelper(context: Context) : SQLiteOpenHelper(context, "sueno_db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE sueno (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "inicio TEXT, " +
                    "fin TEXT, " +
                    "tipo TEXT, " +
                    "fecha TEXT, " +
                    "duracion REAL DEFAULT 0)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE sueno ADD COLUMN duracion REAL DEFAULT 0")
        }
    }

    // Inserta un nuevo registro
    fun insertarRegistro(inicio: String, fin: String, tipo: String, fecha: String, duracion: Float = 0f): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("inicio", inicio)
            put("fin", fin)
            put("tipo", tipo)
            put("fecha", fecha)
            put("duracion", duracion)
        }
        val id = db.insert("sueno", null, values)
        db.close()
        return id
    }

    // Actualiza el registro si el usuario había iniciado el sueño y luego termino
    fun actualizarFinYDuracion(id: Long, fin: String, duracion: Float) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("fin", fin)
            put("duracion", duracion)
        }
        db.update("sueno", values, "id = ?", arrayOf(id.toString()))
        db.close()
    }

    // Obtiene todos los registros (primerp mas recientes)
    fun obtenerTodosLosRegistros(): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM sueno ORDER BY fecha DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val registro = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")).toString(),
                    "fecha" to cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    "hora_inicio" to cursor.getString(cursor.getColumnIndexOrThrow("inicio")),
                    "hora_fin" to cursor.getString(cursor.getColumnIndexOrThrow("fin")),
                    "tipo" to cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                    "duracion" to cursor.getFloat(cursor.getColumnIndexOrThrow("duracion")).toString()
                )
                lista.add(registro)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    //  Promedio semanal
    fun obtenerPromedioSemanal(): Map<String, Any> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT fecha, AVG(duracion) AS promedio_horas
            FROM sueno
            WHERE fecha >= date('now', '-7 day')
            GROUP BY fecha
            ORDER BY fecha DESC
            """, null
        )

        var totalHoras = 0f
        var dias = 0
        var horaMediaAcumulada = 0f

        if (cursor.moveToFirst()) {
            do {
                val promedio = cursor.getFloat(cursor.getColumnIndexOrThrow("promedio_horas"))
                totalHoras += promedio
                dias++
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        val promedioSemanal = if (dias > 0) totalHoras / dias else 0f

        return mapOf(
            "promedio_horas" to promedioSemanal,
            "dias" to dias
        )
    }
}
