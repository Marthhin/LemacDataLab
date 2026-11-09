package com.example.lemacdatalab.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CamposAmbitoTest {

    private fun registro(ambito: Ambito) = RegistroActividad(
        idUsuario = 1,
        idAmbito = ambito.id,
        fechaHora = "09/11/2026",
        tipoActividad = "Prueba",
        intensidad = 5,
        contexto = "Contexto de prueba",
        nota = null,
        estado = "Prueba"
    )

    @Test
    fun desdeId_encuentraElAmbitoOdevuelveNulo() {
        assertEquals(Ambito.DBT, Ambito.desdeId(2))
        assertNull(Ambito.desdeId(99))
    }

    @Test
    fun detallesDbt_muestranHabilidadYUtilidad() {
        val detalles = detallesDeRegistro(
            registro(Ambito.DBT).copy(habilidad = "Mindfulness", resultado = "Útil")
        )

        assertEquals(
            listOf("Habilidad" to "Mindfulness", "Utilidad" to "Útil"),
            detalles.map { it.etiqueta to it.valor }
        )
    }

    @Test
    fun registroSinCamposEspecificos_noMuestraDetalles() {
        assertTrue(detallesDeRegistro(registro(Ambito.ANIMO)).isEmpty())
    }

    @Test
    fun animo_noTieneOpcionesDeResultado() {
        assertTrue(opcionesResultado(Ambito.ANIMO).isEmpty())
        assertEquals(CamposAmbito.OBJETIVO_CUMPLIDO, opcionesResultado(Ambito.ADICCIONES))
    }
}
