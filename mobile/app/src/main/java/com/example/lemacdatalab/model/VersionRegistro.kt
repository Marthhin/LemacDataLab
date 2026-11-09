package com.example.lemacdatalab.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registro_versiones")
data class VersionRegistro(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_version")
    val idVersion: Int = 0,

    @ColumnInfo(name = "id_registro")
    val idRegistro: Int,

    @ColumnInfo(name = "fecha_guardado")
    val fechaGuardado: Long,

    @ColumnInfo(name = "tipo_actividad")
    val tipoActividad: String,

    @ColumnInfo(name = "intensidad")
    val intensidad: Int,

    @ColumnInfo(name = "elevacion")
    val elevacion: Int?,

    @ColumnInfo(name = "contexto")
    val contexto: String,

    @ColumnInfo(name = "nota")
    val nota: String?,

    @ColumnInfo(name = "detalles")
    val detalles: String?
) {
    companion object {
        fun desde(registro: RegistroActividad, fechaGuardado: Long) = VersionRegistro(
            idRegistro = registro.idRegistro,
            fechaGuardado = fechaGuardado,
            tipoActividad = registro.tipoActividad,
            intensidad = registro.intensidad,
            elevacion = registro.elevacion,
            contexto = registro.contexto,
            nota = registro.nota,
            detalles = detallesDeRegistro(registro)
                .joinToString(" · ") { "${it.etiqueta}: ${it.valor}" }
                .ifEmpty { null }
        )
    }
}
