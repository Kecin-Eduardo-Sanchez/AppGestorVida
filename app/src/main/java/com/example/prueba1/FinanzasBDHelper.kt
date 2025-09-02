package com.example.prueba1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanzasBDHelper(context: Context) :
    SQLiteOpenHelper(context, "Finanzas.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE categorias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE,
                es_fija INTEGER NOT NULL DEFAULT 0,
                saldo REAL NOT NULL DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE movimientos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                categoria_id INTEGER NOT NULL,
                tipo TEXT NOT NULL CHECK(tipo IN ('ingreso','gasto')),
                monto REAL NOT NULL,
                fecha TEXT NOT NULL,
                descripcion TEXT,
                FOREIGN KEY(categoria_id) REFERENCES categorias(id)
            )
        """.trimIndent())

        insertarCategoriaInicial(db, "Total", true)
        insertarCategoriaInicial(db, "Ahorro", true)
        insertarCategoriaInicial(db, "Comida", false)
        insertarCategoriaInicial(db, "Transporte", false)
    }

    private fun insertarCategoriaInicial(db: SQLiteDatabase, nombre: String, fija: Boolean) {
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("es_fija", if (fija) 1 else 0)
            put("saldo", 0f)
        }
        db.insert("categorias", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS movimientos")
        db.execSQL("DROP TABLE IF EXISTS categorias")
        onCreate(db)
    }

    fun agregarMovimiento(categoria: String, tipo: String, monto: Float, descripcion: String) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            var categoriaId = obtenerIdCategoria(categoria, db)
            if (categoriaId == -1) {
                val cv = ContentValues().apply {
                    put("nombre", categoria)
                    put("es_fija", 0)
                    put("saldo", 0f)
                }
                val rowId = db.insert("categorias", null, cv)
                categoriaId = rowId.toInt()
            }

            val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val values = ContentValues().apply {
                put("categoria_id", categoriaId)
                put("tipo", tipo)
                put("monto", monto)
                put("fecha", fecha)
                put("descripcion", descripcion)
            }
            db.insert("movimientos", null, values)

            actualizarSaldo(categoriaId, tipo, monto, db)

            val totalId = obtenerIdCategoria("Total", db)
            if (totalId != -1) {
                actualizarSaldo(totalId, tipo, monto, db)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    private fun actualizarSaldo(categoriaId: Int, tipo: String, monto: Float, writableDb: SQLiteDatabase? = null) {
        val db = writableDb ?: writableDatabase
        val signo = if (tipo == "ingreso") 1 else -1
        db.execSQL("UPDATE categorias SET saldo = saldo + ? WHERE id = ?", arrayOf<Any>(signo * monto, categoriaId))
        if (writableDb == null) db.close()
    }

    fun obtenerIdCategoria(nombre: String, dbArg: SQLiteDatabase? = null): Int {
        val db = dbArg ?: readableDatabase
        var cursor: Cursor? = null
        return try {
            cursor = db.rawQuery("SELECT id FROM categorias WHERE nombre = ?", arrayOf(nombre))
            if (cursor.moveToFirst()) cursor.getInt(0) else -1
        } finally {
            cursor?.close()
            if (dbArg == null) db.close()
        }
    }

    fun agregarCategoria(nombre: String): Boolean {
        val db = writableDatabase
        return try {
            val exists = obtenerIdCategoria(nombre, db) != -1
            if (!exists) {
                val values = ContentValues().apply {
                    put("nombre", nombre)
                    put("es_fija", 0)
                    put("saldo", 0f)
                }
                db.insert("categorias", null, values) != -1L
            } else {
                false
            }
        } finally {
            db.close()
        }
    }

    fun obtenerCategorias(): List<String> {
        val lista = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM categorias", null)
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return lista
    }

    fun obtenerCategoriasConSaldos(): List<Pair<String, Float>> {
        val lista = mutableListOf<Pair<String, Float>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nombre, saldo FROM categorias ORDER BY es_fija DESC, nombre ASC", null)
        while (cursor.moveToNext()) {
            lista.add(cursor.getString(0) to cursor.getFloat(1))
        }
        cursor.close()
        db.close()
        return lista
    }
    fun modificarCategoria(id: Int, nuevoNombre: String): Boolean {
        if (id <= 0) return false
        val actual = obtenerNombreCategoria(id)
        if (actual.equals("Ahorro", ignoreCase = true) || actual.equals("Total", ignoreCase = true)) {
            return false
        }
        val db = writableDatabase
        return try {
            val values = ContentValues().apply { put("nombre", nuevoNombre) }
            val rows = db.update("categorias", values, "id = ?", arrayOf(id.toString()))
            rows > 0
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun obtenerNombreCategoria(id: Int): String {
        if (id <= 0) return ""
        val db = readableDatabase
        var cursor: Cursor? = null
        return try {
            cursor = db.rawQuery("SELECT nombre FROM categorias WHERE id = ?", arrayOf(id.toString()))
            if (cursor.moveToFirst()) cursor.getString(0) else ""
        } finally {
            cursor?.close()
            db.close()
        }
    }

}
