package com.example.lemacdatalab.datos.remoto

import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.Contenido
import com.example.lemacdatalab.model.TipoContenido

data class RecursoDto(
    val id: Int,
    val idAmbito: Int,
    val titulo: String,
    val resumen: String,
    val tipo: String,
    val minutos: Int,
    val pasos: List<String>?
)

fun RecursoDto.aContenido(): Contenido? {
    val ambito = Ambito.desdeId(idAmbito) ?: return null
    val tipoContenido = TipoContenido.entries.find { it.name == tipo } ?: TipoContenido.LECTURA

    return Contenido(
        id = id,
        ambito = ambito,
        titulo = titulo,
        resumen = resumen,
        tipo = tipoContenido,
        minutos = minutos,
        pasos = pasos.orEmpty()
    )
}
