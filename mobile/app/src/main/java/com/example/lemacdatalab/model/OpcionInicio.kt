package com.example.lemacdatalab.model

import androidx.compose.ui.graphics.vector.ImageVector

data class OpcionInicio(
    val nombre: String,
    val descripcion: String,
    val icono: ImageVector,
    val habilitada: Boolean = true
)