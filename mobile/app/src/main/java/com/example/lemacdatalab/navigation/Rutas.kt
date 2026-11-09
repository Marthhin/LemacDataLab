package com.example.lemacdatalab.navigation

object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val REGISTRO = "registro"
    const val HISTORIAL = "historial"
    const val DETALLE = "detalle/{idRegistro}"
    const val RECURSOS = "recursos"
    const val RECORDATORIOS = "recordatorios"
    const val RESULTADO = "resultado"
    const val CUENTA = "cuenta"

    fun detalle(idRegistro: Int) = "detalle/$idRegistro"
}
