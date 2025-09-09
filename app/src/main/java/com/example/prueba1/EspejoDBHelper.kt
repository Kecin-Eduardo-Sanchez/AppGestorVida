package com.example.prueba1
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class FotoEspejo(
    val id: Int,
    val fecha: String,
    val frase: String,
    val imagen: ByteArray
)



class EspejoDBHelper(context: Context) :
    SQLiteOpenHelper(context, "espejo.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE espejo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha TEXT,
                frase TEXT,
                imagen BLOB
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS espejo")
        onCreate(db)
    }

    fun insertarFoto(fecha: String, frase: String, imagen: ByteArray) {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("fecha", fecha)
            put("frase", frase)
            put("imagen", imagen)
        }
        db.insert("espejo", null, valores)
        db.close()
    }

    fun obtenerFotos(): List<FotoEspejo> {
        val lista = mutableListOf<FotoEspejo>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, fecha, frase, imagen FROM espejo ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val fecha = cursor.getString(1)
                val frase = cursor.getString(2)
                val imagen = cursor.getBlob(3)

                lista.add(FotoEspejo(id, fecha, frase, imagen))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}
