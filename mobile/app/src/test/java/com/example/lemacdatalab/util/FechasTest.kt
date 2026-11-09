package com.example.lemacdatalab.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FechasTest {

    @Test
    fun formatearFecha_agregaCerosALaIzquierda() {
        assertEquals("05/03/2026", formatearFecha(5, 3, 2026))
    }

    @Test
    fun claveFecha_ordenaPorAnioMesDia() {
        assertEquals(20261126, claveFecha("26/11/2026"))
        assertTrue(claveFecha("03/11/2026")!! > claveFecha("26/10/2026")!!)
    }

    @Test
    fun claveFecha_devuelveNuloSiLaFechaEstaMalFormada() {
        assertNull(claveFecha("sin fecha"))
        assertNull(claveFecha("26/11"))
    }

    @Test
    fun esFechaFutura_distingueElPasadoDelFuturo() {
        assertFalse(esFechaFutura("01/01/2000"))
        assertTrue(esFechaFutura("01/01/2999"))
        assertFalse(esFechaFutura(fechaDeHoy()))
    }

    @Test
    fun mesDeFecha_devuelveElNombreDelMes() {
        assertEquals("Noviembre 2026", mesDeFecha("26/11/2026"))
        assertEquals("Sin fecha", mesDeFecha("26/13/2026"))
    }

    @Test
    fun fechaYMillisUtc_seConviertenSinPerderElDia() {
        val millis = fechaAMillisUtc("09/11/2026")!!
        assertEquals("09/11/2026", millisUtcAFecha(millis))
    }

    @Test
    fun fechaAMillisUtc_rechazaMesesInvalidos() {
        assertNull(fechaAMillisUtc("10/13/2026"))
    }
}
