package com.example.lemacdatalab.model

import java.util.Calendar
import java.util.Locale

data class ConfigRecordatorio(
    val activo: Boolean,
    val hora: Int,
    val minuto: Int,
    val dias: Set<Int>,
    val omitirSiRegistrado: Boolean
) {
    val horaTexto: String
        get() = String.format(Locale.ROOT, "%02d:%02d", hora, minuto)

    fun proximoDisparo(ahora: Long = System.currentTimeMillis()): Long? {
        if (dias.isEmpty()) return null

        val candidato = Calendar.getInstance().apply {
            timeInMillis = ahora
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        repeat(8) {
            if (candidato.timeInMillis > ahora && candidato.get(Calendar.DAY_OF_WEEK) in dias) {
                return candidato.timeInMillis
            }
            candidato.add(Calendar.DAY_OF_YEAR, 1)
        }
        return null
    }

    companion object {
        val TODOS_LOS_DIAS = setOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
        )
    }
}