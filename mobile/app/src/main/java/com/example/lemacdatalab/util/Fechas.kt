package com.example.lemacdatalab.util

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

private val MESES = listOf(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
)

private val NOMBRES_DIAS = mapOf(
    Calendar.SUNDAY to "domingo",
    Calendar.MONDAY to "lunes",
    Calendar.TUESDAY to "martes",
    Calendar.WEDNESDAY to "miércoles",
    Calendar.THURSDAY to "jueves",
    Calendar.FRIDAY to "viernes",
    Calendar.SATURDAY to "sábado"
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

fun fechaAMillisUtc(fecha: String): Long? {
    val partes = fecha.split("/").map { it.toIntOrNull() }
    if (partes.size != 3 || partes.any { it == null }) return null
    val (dia, mes, anio) = partes.map { it!! }
    if (mes !in 1..12 || dia !in 1..31) return null
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(anio, mes - 1, dia)
    }.timeInMillis
}

fun millisUtcAFecha(millis: Long): String {
    val fecha = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
    return formatearFecha(
        fecha.get(Calendar.DAY_OF_MONTH),
        fecha.get(Calendar.MONTH) + 1,
        fecha.get(Calendar.YEAR)
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

fun describirProximoAviso(momento: Long): String {
    val objetivo = Calendar.getInstance().apply { timeInMillis = momento }
    val hora = String.format(
        Locale.ROOT,
        "%02d:%02d",
        objetivo.get(Calendar.HOUR_OF_DAY),
        objetivo.get(Calendar.MINUTE)
    )

    val dia = when (diasDesdeHoy(objetivo)) {
        0 -> "hoy"
        1 -> "mañana"
        else -> "el ${NOMBRES_DIAS[objetivo.get(Calendar.DAY_OF_WEEK)]}"
    }

    return "$dia a las $hora"
}

private fun diasDesdeHoy(objetivo: Calendar): Int {
    val inicioHoy = inicioDelDia(Calendar.getInstance())
    val inicioObjetivo = inicioDelDia(objetivo.clone() as Calendar)
    return ((inicioObjetivo - inicioHoy) / 86_400_000.0).roundToInt()
}

private fun inicioDelDia(fecha: Calendar): Long {
    fecha.set(Calendar.HOUR_OF_DAY, 0)
    fecha.set(Calendar.MINUTE, 0)
    fecha.set(Calendar.SECOND, 0)
    fecha.set(Calendar.MILLISECOND, 0)
    return fecha.timeInMillis
}