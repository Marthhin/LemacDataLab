package com.example.lemacdatalab.model

data class RegistroActividad(
    val idRegistro: Int,
    val idUsuario: Int,
    val idAmbito: Int,
    val fechaHora: String,
    val tipoActividad: String,
    val intensidad: Int,
    val contexto: String,
    val nota: String?,
    val estado: String
)