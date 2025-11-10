package com.example.gestorgym.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GymDBHelper(context: Context) : SQLiteOpenHelper(context, "GestorGymDB.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE Ejercicios (
                id_ejercicio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                tipo TEXT NOT NULL,
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

        // TABLA DETALLE DE RUTINA
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
                FOREIGN KEY (id_rutina) REFERENCES Rutinas(id_rutina),
                FOREIGN KEY (id_ejercicio) REFERENCES Ejercicios(id_ejercicio)
            )
        """)

        insertarEjerciciosBase(db)
        insertarRutinasPredefinidas(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS RutinaDetallada")
        db.execSQL("DROP TABLE IF EXISTS Rutinas")
        db.execSQL("DROP TABLE IF EXISTS Ejercicios")
        onCreate(db)
    }

    private fun insertarEjerciciosBase(db: SQLiteDatabase) {
        val ejercicios = listOf(
            // Empuje
            listOf("Press banca", "empuje", "Pectoral", "Tríceps"),
            listOf("Press militar", "empuje", "Hombro", "Tríceps"),
            listOf("Fondos paralelas", "empuje", "Pectoral", "Tríceps"),
            listOf("Press inclinado mancuernas", "empuje", "Pectoral", "Hombro"),
            listOf("Elevaciones laterales", "empuje", "Hombro", null),
            listOf("Extensión de tríceps polea", "empuje", "Tríceps", null),

            // Jalón
            listOf("Jalón en polea", "jalón", "Espalda", "Bíceps"),
            listOf("Remo con barra", "jalón", "Espalda", "Bíceps"),
            listOf("Remo en máquina", "jalón", "Espalda", "Bíceps"),
            listOf("Curl bíceps barra", "jalón", "Bíceps", null),
            listOf("Curl bíceps mancuernas", "jalón", "Bíceps", null),

            // Pierna
            listOf("Sentadilla", "pierna", "Cuádriceps", "Glúteo"),
            listOf("Prensa", "pierna", "Cuádriceps", "Glúteo"),
            listOf("Peso muerto rumano", "pierna", "Isquiotibiales", "Glúteo"),
            listOf("Zancadas", "pierna", "Glúteo", "Cuádriceps"),
            listOf("Curl femoral", "pierna", "Isquiotibiales", null),
            listOf("Extensión cuadríceps", "pierna", "Cuádriceps", null),
            listOf("Elevación de talones", "pierna", "Pantorrilla", null),

            // Core / Cardio
            listOf("Plancha", "core", "Abdomen", null),
            listOf("Crunch abdominal", "core", "Abdomen", null),
            listOf("Cinta de correr", "cardio", "Resistencia", null),
            listOf("Bicicleta estática", "cardio", "Resistencia", null)
        )

        ejercicios.forEach {
            val cv = ContentValues().apply {
                put("nombre", it[0])
                put("tipo", it[1])
                put("estimulo_principal", it[2])
                put("estimulo_secundario", it[3])
            }
            db.insert("Ejercicios", null, cv)
        }
    }

    private fun insertarRutinasPredefinidas(db: SQLiteDatabase) {

        val mapEj = mutableMapOf<String, Int>()
        val cursor = db.rawQuery("SELECT id_ejercicio, nombre FROM Ejercicios", null)
        if (cursor.moveToFirst()) {
            do mapEj[cursor.getString(1)] = cursor.getInt(0) while (cursor.moveToNext())
        }
        cursor.close()

        // ========== RUTINA 1: GANAR MASA ==========
        val idMasa = insertarRutina(db, "Ganar Masa Muscular", 4, "Enfoque en fuerza y progresión de cargas.")

        agregar(db, idMasa, 1, mapEj["Sentadilla"], 4, 8)
        agregar(db, idMasa, 1, mapEj["Press banca"], 4, 6)
        agregar(db, idMasa, 1, mapEj["Remo con barra"], 4, 8)

        agregar(db, idMasa, 2, mapEj["Peso muerto rumano"], 4, 8)
        agregar(db, idMasa, 2, mapEj["Press militar"], 4, 6)
        agregar(db, idMasa, 2, mapEj["Elevaciones laterales"], 3, 12)

        agregar(db, idMasa, 3, mapEj["Prensa"], 4, 10)
        agregar(db, idMasa, 3, mapEj["Fondos paralelas"], 3, 10)
        agregar(db, idMasa, 3, mapEj["Curl bíceps barra"], 3, 10)

        agregar(db, idMasa, 4, mapEj["Cinta de correr"], 1, 20)

        // ========== RUTINA 2: DEFINICIÓN ==========
        val idDef = insertarRutina(db, "Definición / Tonificación", 5, "Mayor volumen + cardio.")

        agregar(db, idDef, 1, mapEj["Sentadilla"], 3, 15)
        agregar(db, idDef, 1, mapEj["Press inclinado mancuernas"], 3, 15)
        agregar(db, idDef, 1, mapEj["Remo en máquina"], 3, 15)

        agregar(db, idDef, 2, mapEj["Prensa"], 4, 12)
        agregar(db, idDef, 2, mapEj["Curl femoral"], 4, 12)
        agregar(db, idDef, 2, mapEj["Plancha"], 1, 60)

        agregar(db, idDef, 3, mapEj["Cinta de correr"], 1, 30)

        // ========== RUTINA 3: PUSH / PULL / LEGS (Cíclica) ==========
        val idPPL = insertarRutina(db, "Push Pull Legs (Avanzada)", 6, "Rotación para progresión continua.")

        // PUSH
        agregar(db, idPPL, 1, mapEj["Press banca"], 4, 8)
        agregar(db, idPPL, 1, mapEj["Press militar"], 4, 10)
        agregar(db, idPPL, 1, mapEj["Elevaciones laterales"], 3, 15)
        agregar(db, idPPL, 1, mapEj["Extensión de tríceps polea"], 3, 12)

        // PULL
        agregar(db, idPPL, 2, mapEj["Jalón en polea"], 4, 10)
        agregar(db, idPPL, 2, mapEj["Remo con barra"], 4, 8)
        agregar(db, idPPL, 2, mapEj["Curl bíceps mancuernas"], 3, 12)

        // LEGS
        agregar(db, idPPL, 3, mapEj["Sentadilla"], 4, 8)
        agregar(db, idPPL, 3, mapEj["Peso muerto rumano"], 4, 10)
        agregar(db, idPPL, 3, mapEj["Elevación de talones"], 4, 15)
    }

    private fun insertarRutina(db: SQLiteDatabase, nombre: String, dias: Int, descripcion: String): Int {
        val cv = ContentValues()
        cv.put("nombre", nombre)
        cv.put("dias", dias)
        cv.put("descripcion", descripcion)
        return db.insert("Rutinas", null, cv).toInt()
    }

    private fun agregar(db: SQLiteDatabase, idRutina: Int, dia: Int, idEjercicio: Int?, series: Int, repeticiones: Int) {
        if (idEjercicio == null) return
        val cv = ContentValues()
        cv.put("id_rutina", idRutina)
        cv.put("dia", dia)
        cv.put("id_ejercicio", idEjercicio)
        cv.put("series", series)
        cv.put("repeticiones", repeticiones)
        cv.put("peso_usado", 0.0)
        cv.put("descanso_segundos", 90)
        db.insert("RutinaDetallada", null, cv)
    }

    fun obtenerRutinas(): List<Triple<Int, String, Int>> {
        val lista = mutableListOf<Triple<Int, String, Int>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id_rutina, nombre, dias FROM Rutinas", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val dias = cursor.getInt(2)
                lista.add(Triple(id, nombre, dias))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }


    fun obtenerEjerciciosDeRutina(idRutina: Int): List<Map<String, Any>> {
        val lista = mutableListOf<Map<String, Any>>()
        val db = readableDatabase

        val query = """
        SELECT RD.dia, E.nombre, RD.series, RD.repeticiones
        FROM RutinaDetallada RD
        JOIN Ejercicios E ON RD.id_ejercicio = E.id_ejercicio
        WHERE RD.id_rutina = ?
        ORDER BY RD.dia
    """

        val cursor = db.rawQuery(query, arrayOf(idRutina.toString()))

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    mapOf(
                        "dia" to cursor.getInt(0),
                        "nombre" to cursor.getString(1),
                        "series" to cursor.getInt(2),
                        "reps" to cursor.getInt(3)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

}
