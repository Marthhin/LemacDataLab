package com.example.lemacdatalab.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros")
data class RegistroActividad(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_registro")
    val idRegistro: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "id_ambito")
    val idAmbito: Int,

    @ColumnInfo(name = "fecha_hora")
    val fechaHora: String,

    @ColumnInfo(name = "tipo_actividad")
    val tipoActividad: String,

    @ColumnInfo(name = "intensidad")
    val intensidad: Int,

    @ColumnInfo(name = "contexto")
    val contexto: String,

    @ColumnInfo(name = "nota")
    val nota: String?,

    @ColumnInfo(name = "estado")
    val estado: String
)