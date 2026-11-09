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
        intensidad = 2,
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
    fun opcionOtro_seGuardaConPrefijoYSeRecupera() {
        val guardado = unirSeleccion(OPCION_OTRO, "  Escribir un diario ")

        assertEquals("Otro: Escribir un diario", guardado)
        assertEquals(
            SeleccionCatalogo(OPCION_OTRO, "Escribir un diario"),
            separarSeleccion(guardado, CamposAmbito.HABILIDADES_DBT)
        )
        assertNull(unirSeleccion(OPCION_OTRO, "  "))
    }

    @Test
    fun textoAntiguoFueraDelCatalogo_quedaComoOtro() {
        assertEquals(
            SeleccionCatalogo(OPCION_OTRO, "Salí con mi perro"),
            separarSeleccion("Salí con mi perro", CamposAmbito.ACCIONES_PROTECTORAS)
        )
    }

    @Test
    fun escala_muestraNumeroYEtiqueta() {
        assertEquals("0 · Sin síntomas", Escala.describir(0))
        assertEquals("4 · Severa", Escala.describir(9))
    }

    @Test
    fun animo_noTieneOpcionesDeResultado() {
        assertTrue(opcionesResultado(Ambito.ANIMO).isEmpty())
        assertEquals(CamposAmbito.OBJETIVO_CUMPLIDO, opcionesResultado(Ambito.ADICCIONES))
    }
}
