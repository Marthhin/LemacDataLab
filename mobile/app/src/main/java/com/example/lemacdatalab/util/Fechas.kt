package com.example.lemacdatalab.util

private val MESES = listOf(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
)

fun mesDeFecha(fecha: String): String {
    val partes = fecha.split("/")
    val mes = partes.getOrNull(1)?.toIntOrNull()
    val anio = partes.getOrNull(2)
    if (mes == null || mes !in 1..12 || anio == null) return "Sin fecha"
    return "${MESES[mes - 1]} $anio"
}