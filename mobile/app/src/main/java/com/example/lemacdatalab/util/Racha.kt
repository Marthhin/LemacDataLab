package com.example.lemacdatalab.util

import java.util.Calendar

fun calcularRacha(fechasConRegistro: Set<String>, hoy: Calendar = Calendar.getInstance()): Int {
    val dia = hoy.clone() as Calendar

    if (fechaDe(dia) !in fechasConRegistro) {
        dia.add(Calendar.DAY_OF_YEAR, -1)
    }

    var racha = 0
    while (fechaDe(dia) in fechasConRegistro) {
        racha++
        dia.add(Calendar.DAY_OF_YEAR, -1)
    }
    return racha
}

fun ultimosSieteDias(fechasConRegistro: Set<String>, hoy: Calendar = Calendar.getInstance()): List<Boolean> {
    val dia = (hoy.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -6) }
    return List(7) {
        val tieneRegistro = fechaDe(dia) in fechasConRegistro
        dia.add(Calendar.DAY_OF_YEAR, 1)
        tieneRegistro
    }
}
