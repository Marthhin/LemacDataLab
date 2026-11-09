package com.example.lemacdatalab.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class RachaTest {

    private val hoy = Calendar.getInstance().apply {
        clear()
        set(2026, Calendar.NOVEMBER, 9, 12, 0)
    }

    @Test
    fun sinRegistros_laRachaEsCero() {
        assertEquals(0, calcularRacha(emptySet(), hoy))
    }

    @Test
    fun tresDiasSeguidosHastaHoy() {
        val fechas = setOf("09/11/2026", "08/11/2026", "07/11/2026")
        assertEquals(3, calcularRacha(fechas, hoy))
    }

    @Test
    fun siHoyNoHayRegistro_laRachaDeAyerSigueVigente() {
        val fechas = setOf("08/11/2026", "07/11/2026")
        assertEquals(2, calcularRacha(fechas, hoy))
    }

    @Test
    fun unDiaSinRegistro_cortaLaRacha() {
        val fechas = setOf("09/11/2026", "07/11/2026", "06/11/2026")
        assertEquals(1, calcularRacha(fechas, hoy))
    }

    @Test
    fun laRachaCruzaElCambioDeMes() {
        val dosDeNoviembre = (hoy.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 2) }
        val fechas = setOf("02/11/2026", "01/11/2026", "31/10/2026")
        assertEquals(3, calcularRacha(fechas, dosDeNoviembre))
    }

    @Test
    fun ultimosSieteDias_terminaHoy() {
        val semana = ultimosSieteDias(setOf("09/11/2026", "03/11/2026"), hoy)
        assertEquals(listOf(true, false, false, false, false, false, true), semana)
    }

    @Test
    fun desfaseDesdeLunes_noviembre2026EmpiezaEnDomingo() {
        assertEquals(6, desfaseDesdeLunes(2026, 11))
        assertEquals(30, diasDelMes(2026, 11))
    }
}
