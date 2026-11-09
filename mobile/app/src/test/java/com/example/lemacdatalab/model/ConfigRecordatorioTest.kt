package com.example.lemacdatalab.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class ConfigRecordatorioTest {

    private fun momento(anio: Int, mes: Int, dia: Int, hora: Int, minuto: Int): Long =
        Calendar.getInstance().apply {
            clear()
            set(anio, mes - 1, dia, hora, minuto, 0)
        }.timeInMillis

    private fun config(hora: Int, minuto: Int, dias: Set<Int>) = ConfigRecordatorio(
        activo = true,
        hora = hora,
        minuto = minuto,
        dias = dias,
        omitirSiRegistrado = true
    )

    private val miercolesALasDiez = momento(2026, 11, 11, 10, 0)

    @Test
    fun siLaHoraNoHaPasado_avisaElMismoDia() {
        val resultado = config(20, 0, ConfigRecordatorio.TODOS_LOS_DIAS)
            .proximoDisparo(miercolesALasDiez)

        assertEquals(momento(2026, 11, 11, 20, 0), resultado)
    }

    @Test
    fun siLaHoraYaPaso_avisaAlDiaSiguiente() {
        val resultado = config(8, 0, ConfigRecordatorio.TODOS_LOS_DIAS)
            .proximoDisparo(miercolesALasDiez)

        assertEquals(momento(2026, 11, 12, 8, 0), resultado)
    }

    @Test
    fun soloLunes_saltaAlProximoLunes() {
        val resultado = config(20, 0, setOf(Calendar.MONDAY))
            .proximoDisparo(miercolesALasDiez)

        assertEquals(momento(2026, 11, 16, 20, 0), resultado)
    }

    @Test
    fun sinDias_noHayProximoAviso() {
        assertNull(config(20, 0, emptySet()).proximoDisparo(miercolesALasDiez))
    }

    @Test
    fun horaTexto_usaFormatoDeVeinticuatroHoras() {
        assertEquals("07:05", config(7, 5, ConfigRecordatorio.TODOS_LOS_DIAS).horaTexto)
    }
}
