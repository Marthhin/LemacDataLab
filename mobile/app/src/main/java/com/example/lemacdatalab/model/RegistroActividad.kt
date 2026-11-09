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
    val estado: String,

    @ColumnInfo(name = "emocion")
    val emocion: String? = null,

    @ColumnInfo(name = "habilidad")
    val habilidad: String? = null,

    @ColumnInfo(name = "estrategia")
    val estrategia: String? = null,

    @ColumnInfo(name = "desencadenante")
    val desencadenante: String? = null,

    @ColumnInfo(name = "accion_protectora")
    val accionProtectora: String? = null,

    @ColumnInfo(name = "resultado")
    val resultado: String? = null,

    @ColumnInfo(name = "elevacion")
    val elevacion: Int? = null,

    @ColumnInfo(name = "horas_sueno")
    val horasSueno: Double? = null,

    @ColumnInfo(name = "alto_riesgo", defaultValue = "0")
    val altoRiesgo: Boolean = false,

    @ColumnInfo(name = "editado", defaultValue = "0")
    val editado: Boolean = false,

    @ColumnInfo(name = "eliminado", defaultValue = "0")
    val eliminado: Boolean = false,

    @ColumnInfo(name = "fecha_modificacion", defaultValue = "0")
    val fechaModificacion: Long = 0
)
