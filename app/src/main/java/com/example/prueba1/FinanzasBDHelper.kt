package com.example.prueba1

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Se ha añadido el patrón Singleton para asegurar una única instancia de la base de datos.
class FinanzasBDHelper(context: Context) :
    SQLiteOpenHelper(context, "Finanzas.db", null, 2) {

    // --- INICIO: Implementación del patrón Singleton ---
    companion object {
        @Volatile
        private var INSTANCE: FinanzasBDHelper? = null

        fun getInstance(context: Context): FinanzasBDHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = FinanzasBDHelper(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    // --- FIN: Implementación del patrón Singleton ---

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
            put("saldo", 0.0) // Es mejor usar 0.0 para Reales/Doubles
        }
        db.insert("categorias", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS movimientos")
        db.execSQL("DROP TABLE IF EXISTS categorias")
        onCreate(db)
    }

    fun agregarMovimiento(categoria: String, tipo: String, monto: Float, descripcion: String) {
        // Obtenemos una única instancia de la base de datos para toda la operación.
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Obtenemos o creamos el ID de la categoría
            var categoriaId = obtenerIdCategoria(categoria, db)
            if (categoriaId == -1L) { // El ID es de tipo Long
                val cv = ContentValues().apply {
                    put("nombre", categoria)
                    put("es_fija", 0)
                    put("saldo", 0.0)
                }
                categoriaId = db.insert("categorias", null, cv)
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

            // Actualizamos el saldo de la categoría específica
            actualizarSaldo(categoriaId, tipo, monto, db)

            // Actualizamos el saldo de la categoría "Total"
            val totalId = obtenerIdCategoria("Total", db)
            if (totalId != -1L) {
                actualizarSaldo(totalId, tipo, monto, db)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            // NO cerramos la base de datos aquí: db.close() <-- ELIMINADO
        }
    }

    // Esta función ahora es privada y requiere que se le pase la instancia de la BD
    // para poder ser usada dentro de una transacción.
    private fun actualizarSaldo(categoriaId: Long, tipo: String, monto: Float, db: SQLiteDatabase) {
        val signo = if (tipo == "ingreso") 1 else -1
        val montoAfectado = signo * monto
        db.execSQL("UPDATE categorias SET saldo = saldo + ? WHERE id = ?", arrayOf(montoAfectado, categoriaId))
    }

    // Devuelve Long, ya que los IDs de las filas son Long.
    // Requiere la instancia de la BD para ser usada en transacciones.
    private fun obtenerIdCategoria(nombre: String, db: SQLiteDatabase): Long {
        val cursor = db.rawQuery("SELECT id FROM categorias WHERE nombre = ?", arrayOf(nombre))
        return cursor.use { // .use cierra el cursor automáticamente
            if (it.moveToFirst()) it.getLong(0) else -1L
        }
    }

    fun agregarCategoria(nombre: String): Boolean {
        val db = this.writableDatabase
        // Comprobamos si ya existe dentro de la misma conexión
        if (obtenerIdCategoria(nombre, db) != -1L) {
            return false
        }
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("es_fija", 0)
            put("saldo", 0.0)
        }
        return db.insert("categorias", null, values) != -1L
        // NO cerramos la base de datos: db.close() <-- ELIMINADO
    }

    fun obtenerCategoriasConSaldos(): List<Pair<String, Float>> {
        val lista = mutableListOf<Pair<String, Float>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT nombre, saldo FROM categorias ORDER BY es_fija DESC, nombre ASC", null)

        cursor.use { c -> // Usar .use para garantizar que el cursor se cierre
            while (c.moveToNext()) {
                lista.add(c.getString(0) to c.getFloat(1))
            }
        }
        // NO cerramos la base de datos: db.close() <-- ELIMINADO
        return lista
    }

    // ... (El resto de tus funciones como `modificarCategoria` y `obtenerNombreCategoria`
    // también deben ser modificadas para eliminar `db.close()`)
    // Ejemplo:
    fun obtenerCategorias(): List<String> {
        val lista = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM categorias", null)
        cursor.use {
            while (it.moveToNext()) {
                lista.add(it.getString(0))
            }
        }
        return lista
    }
    // Pega estas dos funciones DENTRO de tu clase FinanzasBDHelper

    // --- AÑADIDO: Versión pública de obtenerIdCategoria que la UI puede llamar ---
    fun obtenerIdCategoria(nombre: String): Long {
        val db = this.readableDatabase
        return obtenerIdCategoria(nombre, db) // Llama a la versión privada que ya tenías
    }

    // --- AÑADIDO: La función modificarCategoria que estaba completamente ausente ---
    fun modificarCategoria(id: Long, nuevoNombre: String): Boolean {
        // Protección para no modificar un ID inválido
        if (id <= 0) return false

        // Protección para no modificar las categorías fijas
        val nombreActual = obtenerNombreCategoria(id)
        if (nombreActual.equals("Ahorro", ignoreCase = true) || nombreActual.equals("Total", ignoreCase = true)) {
            return false
        }

        val db = this.writableDatabase
        return try {
            val values = ContentValues().apply { put("nombre", nuevoNombre) }
            val rows = db.update("categorias", values, "id = ?", arrayOf(id.toString()))
            rows > 0 // Devuelve true si se actualizó al menos una fila
        } catch (e: Exception) {
            // En caso de que el nuevo nombre ya exista (violación de UNIQUE)
            false
        } finally {
            // NO cerramos la base de datos
        }
    }

    // --- AÑADIDO: Función auxiliar que necesita modificarCategoria ---
    fun obtenerNombreCategoria(id: Long): String {
        if (id <= 0) return ""
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM categorias WHERE id = ?", arrayOf(id.toString()))
        return cursor.use {
            if (it.moveToFirst()) it.getString(0) else ""
        }
    }
}