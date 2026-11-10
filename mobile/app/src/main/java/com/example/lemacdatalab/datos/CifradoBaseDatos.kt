package com.example.lemacdatalab.datos

import android.content.Context
import android.util.Log
import net.zetetic.database.sqlcipher.SQLiteDatabase
import java.io.File
import java.security.SecureRandom

object CifradoBaseDatos {

    private const val CLAVE_FRASE = "frase_base_datos"
    private val CABECERA_SQLITE = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)

    @Volatile
    private var bibliotecaCargada = false

    fun cargarBiblioteca() {
        if (!bibliotecaCargada) {
            System.loadLibrary("sqlcipher")
            bibliotecaCargada = true
        }
    }

    fun obtenerFrase(context: Context): String {
        val almacen = AlmacenSeguro(context)
        almacen.leer(CLAVE_FRASE)?.let { return it }

        val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val frase = bytes.joinToString("") { "%02x".format(it) }
        almacen.guardar(CLAVE_FRASE, frase)
        return frase
    }

    fun cifrarSiEsTextoPlano(context: Context, nombre: String, frase: String) {
        val archivo = context.getDatabasePath(nombre)
        if (!archivo.exists() || !esTextoPlano(archivo)) return

        val temporal = File(archivo.parentFile, "$nombre.cifrando")
        temporal.delete()

        try {
            val plana = SQLiteDatabase.openOrCreateDatabase(archivo, "", null, null, null)
            val version = plana.version
            plana.execSQL("ATTACH DATABASE ? AS cifrada KEY ?", arrayOf<Any?>(temporal.absolutePath, frase))
            plana.query("SELECT sqlcipher_export('cifrada')").use { it.moveToFirst() }
            plana.execSQL("PRAGMA cifrada.user_version = $version")
            plana.execSQL("DETACH DATABASE cifrada")
            plana.close()

            eliminarConAuxiliares(archivo)
            check(temporal.renameTo(archivo)) { "No se pudo reemplazar la base de datos" }
        } catch (error: Exception) {
            Log.e("CifradoBaseDatos", "No se pudo cifrar la base existente; se crea una nueva", error)
            temporal.delete()
            archivo.renameTo(File(archivo.parentFile, "$nombre.respaldo"))
            eliminarConAuxiliares(archivo)
        }
    }

    private fun esTextoPlano(archivo: File): Boolean {
        val inicio = ByteArray(CABECERA_SQLITE.size)
        val leidos = archivo.inputStream().use { it.read(inicio) }
        return leidos == inicio.size && inicio.contentEquals(CABECERA_SQLITE)
    }

    private fun eliminarConAuxiliares(archivo: File) {
        listOf("", "-wal", "-shm", "-journal").forEach { sufijo ->
            File(archivo.path + sufijo).delete()
        }
    }
}
