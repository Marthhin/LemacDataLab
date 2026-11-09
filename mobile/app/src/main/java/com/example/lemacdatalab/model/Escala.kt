package com.example.lemacdatalab.model

object Escala {
    const val MINIMO = 0
    const val MAXIMO = 4
    const val CRITICO = 4

    val ETIQUETAS = listOf("Sin síntomas", "Leve", "Moderada", "Marcada", "Severa")

    fun etiqueta(valor: Int): String = ETIQUETAS[valor.coerceIn(MINIMO, MAXIMO)]

    fun describir(valor: Int): String = "${valor.coerceIn(MINIMO, MAXIMO)} · ${etiqueta(valor)}"
}
