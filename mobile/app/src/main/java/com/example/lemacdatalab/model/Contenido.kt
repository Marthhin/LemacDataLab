package com.example.lemacdatalab.model

enum class TipoContenido(val nombre: String) {
    LECTURA("Lectura"),
    EJERCICIO("Ejercicio")
}

data class Contenido(
    val id: Int,
    val ambito: Ambito,
    val titulo: String,
    val resumen: String,
    val tipo: TipoContenido,
    val minutos: Int,
    val pasos: List<String>
)