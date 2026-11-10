package com.example.lemacdatalab.model

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AlertaCriticaTest {

    private fun registro(ambito: Ambito, intensidad: Int) = RegistroActividad(
        idUsuario = 1,
        idAmbito = ambito.id,
        fechaHora = "10/11/2026",
        tipoActividad = "Prueba",
        intensidad = intensidad,
        contexto = "Contexto",
        nota = null,
        estado = "Activo"
    )

    @Test
    fun cravingSevero_esCritico() {
        assertNotNull(alertaCriticaDe(registro(Ambito.ADICCIONES, 4)))
        assertNull(alertaCriticaDe(registro(Ambito.ADICCIONES, 3)))
    }

    @Test
    fun emocionSeveraEnDbt_esCritica() {
        assertNotNull(alertaCriticaDe(registro(Ambito.DBT, 4)))
    }

    @Test
    fun neurodesarrolloSevero_noDisparaAviso() {
        assertNull(alertaCriticaDe(registro(Ambito.NEURODESARROLLO, 4)))
    }

    @Test
    fun animo_altoRiesgoOMenosDeTresHorasDeSueno() {
        val base = registro(Ambito.ANIMO, 1)

        assertNull(alertaCriticaDe(base.copy(horasSueno = 3.0)))
        assertNotNull(alertaCriticaDe(base.copy(horasSueno = 2.5)))
        assertNotNull(alertaCriticaDe(base.copy(altoRiesgo = true)))
    }
}
