package com.example.lemacdatalab.util

import java.util.Calendar
import java.util.Locale

private val MESES = listOf(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
)

fun formatearFecha(dia: Int, mes: Int, anio: Int): String =
    String.format(Locale.ROOT, "%02d/%02d/%04d", dia, mes, anio)

fun fechaDeHoy(): String {
    val hoy = Calendar.getInstance()
    return formatearFecha(
        hoy.get(Calendar.DAY_OF_MONTH),
        hoy.get(Calendar.MONTH) + 1,
        hoy.get(Calendar.YEAR)
    )
}

fun claveFecha(fecha: String): Int? {
    val partes = fecha.split("/").map { it.toIntOrNull() }
    if (partes.size != 3 || partes.any { it == null }) return null
    val (dia, mes, anio) = partes.map { it!! }
    return anio * 10_000 + mes * 100 + dia
}

fun esFechaFutura(fecha: String): Boolean {
    val clave = claveFecha(fecha) ?: return false
    return clave > claveFecha(fechaDeHoy())!!
}

fun mesDeFecha(fecha: String): String {
    val partes = fecha.split("/")
    val mes = partes.getOrNull(1)?.toIntOrNull()
    val anio = partes.getOrNull(2)
    if (mes == null || mes !in 1..12 || anio == null) return "Sin fecha"
    return "${MESES[mes - 1]} $anio"
}