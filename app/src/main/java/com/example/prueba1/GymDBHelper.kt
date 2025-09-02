package com.example.gestorgym.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GymDBHelper(context: Context) :
    SQLiteOpenHelper(context, "GestorGymDB.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tablas
        db.execSQL("""
            CREATE TABLE Ejercicios (
                id_ejercicio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                tipo TEXT NOT NULL CHECK(tipo IN ('jalón','empuje','pierna','cardio')),
                estimulo_principal TEXT NOT NULL,
                estimulo_secundario TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE Rutinas (
                id_rutina INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                dias INTEGER NOT NULL,
                descripcion TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE RutinaDetallada (
                id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
                id_rutina INTEGER NOT NULL,
                dia INTEGER NOT NULL,
                id_ejercicio INTEGER NOT NULL,
                series INTEGER,
                repeticiones INTEGER,
                peso_usado REAL,
                descanso_segundos INTEGER,
                realizado INTEGER DEFAULT 0,
                FOREIGN KEY (id_rutina) REFERENCES Rutinas(id_rutina),
                FOREIGN KEY (id_ejercicio) REFERENCES Ejercicios(id_ejercicio)
            )
        """)

        db.execSQL("""
            CREATE TABLE Reportes (
                id_reporte INTEGER PRIMARY KEY AUTOINCREMENT,
                id_rutina INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                progreso TEXT,
                FOREIGN KEY (id_rutina) REFERENCES Rutinas(id_rutina)
            )
        """)


    }
    fun insertarEjercicio(nombre: String, tipo: String, estimuloPrincipal: String, estimuloSecundario: String?): Long {
        val db = writableDatabase
        val values = android.content.ContentValues().apply {
            put("nombre", nombre)
            put("tipo", tipo)
            put("estimulo_principal", estimuloPrincipal)
            put("estimulo_secundario", estimuloSecundario)
        }
        val id = db.insert("Ejercicios", null, values)
        db.close()
        return id
    }

    fun insertarRutina(nombre: String, dias: Int, descripcion: String?): Long {
        val db = writableDatabase
        val values = android.content.ContentValues().apply {
            put("nombre", nombre)
            put("dias", dias)
            put("descripcion", descripcion)
        }
        val id = db.insert("Rutinas", null, values)
        db.close()
        return id
    }

    fun insertarDetalleRutina(
        idRutina: Int,
        dia: Int,
        idEjercicio: Int,
        series: Int,
        repeticiones: Int,
        pesoUsado: Double,
        descansoSegundos: Int
    ): Long {
        val db = writableDatabase
        val values = android.content.ContentValues().apply {
            put("id_rutina", idRutina)
            put("dia", dia)
            put("id_ejercicio", idEjercicio)
            put("series", series)
            put("repeticiones", repeticiones)
            put("peso_usado", pesoUsado)
            put("descanso_segundos", descansoSegundos)
        }
        val id = db.insert("RutinaDetallada", null, values)
        db.close()
        return id
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Reportes")
        db.execSQL("DROP TABLE IF EXISTS RutinaDetallada")
        db.execSQL("DROP TABLE IF EXISTS Rutinas")
        db.execSQL("DROP TABLE IF EXISTS Ejercicios")
        onCreate(db)
    }

    fun obtenerRutinaPorId(id: Int): Map<String, Any>? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Rutinas WHERE id_rutina = ?",
            arrayOf(id.toString())
        )

        var rutina: Map<String, Any>? = null
        if (cursor.moveToFirst()) {
            rutina = mapOf(
                "id_rutina" to cursor.getInt(cursor.getColumnIndexOrThrow("id_rutina")),
                "nombre" to cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                "dias" to cursor.getInt(cursor.getColumnIndexOrThrow("dias")),
                "descripcion" to cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
            )
        }

        cursor.close()
        db.close()
        return rutina
    }
    fun obtenerRutinaDetalladaPorId(idRutina: Int): List<Map<String, Any>> {
        val db = readableDatabase
        val cursor = db.rawQuery("""
        SELECT rd.id_detalle, rd.dia, e.nombre AS ejercicio, rd.series, rd.repeticiones, 
               rd.peso_usado, rd.descanso_segundos, rd.realizado
        FROM RutinaDetallada rd
        INNER JOIN Ejercicios e ON rd.id_ejercicio = e.id_ejercicio
        WHERE rd.id_rutina = ?
        ORDER BY rd.dia
    """, arrayOf(idRutina.toString()))

        val detalles = mutableListOf<Map<String, Any>>()
        if (cursor.moveToFirst()) {
            do {
                detalles.add(
                    mapOf(
                        "id_detalle" to cursor.getInt(cursor.getColumnIndexOrThrow("id_detalle")),
                        "dia" to cursor.getInt(cursor.getColumnIndexOrThrow("dia")),
                        "ejercicio" to cursor.getString(cursor.getColumnIndexOrThrow("ejercicio")),
                        "series" to cursor.getInt(cursor.getColumnIndexOrThrow("series")),
                        "repeticiones" to cursor.getInt(cursor.getColumnIndexOrThrow("repeticiones")),
                        "peso_usado" to cursor.getDouble(cursor.getColumnIndexOrThrow("peso_usado")),
                        "descanso_segundos" to cursor.getInt(cursor.getColumnIndexOrThrow("descanso_segundos")),
                        "realizado" to (cursor.getInt(cursor.getColumnIndexOrThrow("realizado")) == 1)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return detalles
    }

    fun obtenerListadoRutinas(db: SQLiteDatabase): List<Pair<Int, String>> {
        val listado = mutableListOf<Pair<Int, String>>()
        val cursor = db.rawQuery("SELECT id, nombre FROM rutinas", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                listado.add(Pair(id, nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listado
    }


}
