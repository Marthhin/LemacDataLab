package com.example.lemacdatalab.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class EvolucionTest {

    private val hoy = Calendar.getInstance().apply {
        clear()
        set(2026, Calendar.NOVEMBER, 10)
    }

    private fun registro(ambito: Ambito, fecha: String, intensidad: Int, elevacion: Int? = null) =
        RegistroActividad(
            idUsuario = 1,
            idAmbito = ambito.id,
            fechaHora = fecha,
            tipoActividad = "Prueba",
            intensidad = intensidad,
            contexto = "Contexto",
            nota = null,
            estado = "Activo",
            elevacion = elevacion
        )

    @Test
    fun ultimasFechas_terminaHoyYCruzaElMes() {
        val fechas = ultimasFechas(12, hoy)

        assertEquals("30/10/2026", fechas.first())
        assertEquals("10/11/2026", fechas.last())
        assertEquals(12, fechas.size)
    }

    @Test
    fun diaSinRegistro_quedaNuloYNoComoCero() {
        val evolucion = calcularEvolucion(
            listOf(registro(Ambito.DBT, "10/11/2026", 3)),
            dias = 7,
            hoy = hoy
        ).first { it.ambito == Ambito.DBT }

        assertEquals(3f, evolucion.valores.last())
        assertNull(evolucion.valores.first())
        assertEquals(1, evolucion.diasRegistrados)
    }

    @Test
    fun animo_separaDepresionYElevacion() {
        val evolucion = calcularEvolucion(
            listOf(registro(Ambito.ANIMO, "09/11/2026", 2, elevacion = 3)),
            dias = 7,
            hoy = hoy
        ).first { it.ambito == Ambito.ANIMO }

        assertEquals(2f, evolucion.valores[5])
        assertEquals(3f, evolucion.elevacion?.get(5))
    }

    @Test
    fun registrosEliminados_noCuentan() {
        val evolucion = calcularEvolucion(
            listOf(registro(Ambito.ADICCIONES, "10/11/2026", 4).copy(eliminado = true)),
            dias = 7,
            hoy = hoy
        ).first { it.ambito == Ambito.ADICCIONES }

        assertEquals(0, evolucion.diasRegistrados)
    }
}
