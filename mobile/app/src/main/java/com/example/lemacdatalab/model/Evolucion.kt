package com.example.lemacdatalab.model

import com.example.lemacdatalab.util.fechaDe
import java.util.Calendar

data class EvolucionAmbito(
    val ambito: Ambito,
    val fechas: List<String>,
    val valores: List<Float?>,
    val elevacion: List<Float?>? = null
) {
    val diasRegistrados: Int
        get() = valores.count { it != null }
}

fun ultimasFechas(dias: Int, hoy: Calendar = Calendar.getInstance()): List<String> {
    val calendario = hoy.clone() as Calendar
    calendario.add(Calendar.DAY_OF_YEAR, -(dias - 1))
    return List(dias) {
        fechaDe(calendario).also { calendario.add(Calendar.DAY_OF_YEAR, 1) }
    }
}

fun calcularEvolucion(
    registros: List<RegistroActividad>,
    dias: Int,
    hoy: Calendar = Calendar.getInstance()
): List<EvolucionAmbito> {
    val fechas = ultimasFechas(dias, hoy)
    val porAmbitoYFecha = registros
        .filterNot { it.eliminado }
        .groupBy { it.idAmbito to it.fechaHora }

    fun serie(ambito: Ambito, valor: (RegistroActividad) -> Int?): List<Float?> = fechas.map { fecha ->
        porAmbitoYFecha[ambito.id to fecha]
            ?.mapNotNull(valor)
            ?.takeIf { it.isNotEmpty() }
            ?.average()
            ?.toFloat()
    }

    return Ambito.entries.map { ambito ->
        EvolucionAmbito(
            ambito = ambito,
            fechas = fechas,
            valores = serie(ambito) { it.intensidad.coerceIn(Escala.MINIMO, Escala.MAXIMO) },
            elevacion = if (ambito == Ambito.ANIMO) {
                serie(ambito) { it.elevacion?.coerceIn(Escala.MINIMO, Escala.MAXIMO) }
            } else {
                null
            }
        )
    }
}
