package com.example.lemacdatalab.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class DestinoInicio {
    REGISTRO_HOY,
    REGISTRAR,
    HISTORIAL,
    RECURSOS,
    RECORDATORIOS
}

data class OpcionInicio(
    val destino: DestinoInicio,
    val nombre: String,
    val descripcion: String,
    val icono: ImageVector,
    val habilitada: Boolean = true
)
