package com.example.lemacdatalab.model

import java.util.Locale

data class ConfigRecordatorio(
    val activo: Boolean,
    val hora: Int,
    val minuto: Int
) {
    val horaTexto: String
        get() = String.format(Locale.ROOT, "%02d:%02d", hora, minuto)
}