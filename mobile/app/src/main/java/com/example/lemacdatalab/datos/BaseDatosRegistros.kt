package com.example.lemacdatalab.datos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lemacdatalab.model.RegistroActividad

class BaseDatosRegistros(context: Context) :
    SQLiteOpenHelper(context, "lemac_registros.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE registros (
                id_registro INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                id_ambito INTEGER NOT NULL,
                fecha_hora TEXT NOT NULL,
                tipo_actividad TEXT NOT NULL,
                intensidad INTEGER NOT NULL,
                contexto TEXT NOT NULL,
                nota TEXT,
                estado TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        versionAnterior: Int,
        versionNueva: Int
    ) {
        // Primera versión: todavía no hay cambios de estructura que migrar.
    }

    fun guardar(registro: RegistroActividad): Long {
        val valores = ContentValues().apply {
            put("id_usuario", registro.idUsuario)
            put("id_ambito", registro.idAmbito)
            put("fecha_hora", registro.fechaHora)
            put("tipo_actividad", registro.tipoActividad)
            put("intensidad", registro.intensidad)
            put("contexto", registro.contexto)
            put("nota", registro.nota)
            put("estado", registro.estado)
        }

        return writableDatabase.insert("registros", null, valores)
    }

    fun obtenerTodos(): List<RegistroActividad> {
        val registros = mutableListOf<RegistroActividad>()

        readableDatabase.query(
            "registros",
            null,
            null,
            null,
            null,
            null,
            "id_registro DESC"
        ).use { cursor ->
            val columnaId = cursor.getColumnIndexOrThrow("id_registro")
            val columnaUsuario = cursor.getColumnIndexOrThrow("id_usuario")
            val columnaAmbito = cursor.getColumnIndexOrThrow("id_ambito")
            val columnaFecha = cursor.getColumnIndexOrThrow("fecha_hora")
            val columnaActividad = cursor.getColumnIndexOrThrow("tipo_actividad")
            val columnaIntensidad = cursor.getColumnIndexOrThrow("intensidad")
            val columnaContexto = cursor.getColumnIndexOrThrow("contexto")
            val columnaNota = cursor.getColumnIndexOrThrow("nota")
            val columnaEstado = cursor.getColumnIndexOrThrow("estado")

            while (cursor.moveToNext()) {
                registros.add(
                    RegistroActividad(
                        idRegistro = cursor.getInt(columnaId),
                        idUsuario = cursor.getInt(columnaUsuario),
                        idAmbito = cursor.getInt(columnaAmbito),
                        fechaHora = cursor.getString(columnaFecha),
                        tipoActividad = cursor.getString(columnaActividad),
                        intensidad = cursor.getInt(columnaIntensidad),
                        contexto = cursor.getString(columnaContexto),
                        nota = if (cursor.isNull(columnaNota)) {
                            null
                        } else {
                            cursor.getString(columnaNota)
                        },
                        estado = cursor.getString(columnaEstado)
                    )
                )
            }
        }

        return registros
    }
}