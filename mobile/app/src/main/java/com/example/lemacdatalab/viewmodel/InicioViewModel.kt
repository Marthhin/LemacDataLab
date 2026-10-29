package com.example.lemacdatalab.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lemacdatalab.model.OpcionInicio

class InicioViewModel : ViewModel() {

    val principal = OpcionInicio(
        nombre = "Registrar actividad",
        descripcion = "¿Qué hiciste hoy por ti?",
        icono = Icons.Rounded.AddCircle
    )

    val secundarias = listOf(
        OpcionInicio(
            nombre = "Ver historial",
            descripcion = "Revisa tus registros anteriores",
            icono = Icons.Rounded.History
        ),
        OpcionInicio(
            nombre = "Recursos de apoyo",
            descripcion = "Próximamente",
            icono = Icons.Rounded.Spa,
            habilitada = false
        ),
        OpcionInicio(
            nombre = "Recordatorios",
            descripcion = "Prueba un aviso en tu dispositivo",
            icono = Icons.Rounded.NotificationsActive
        )
    )

    // Mantiene compatible la pantalla actual hasta reemplazarla.
    val opciones = listOf(principal) + secundarias

    var mensajeSeleccionado by mutableStateOf("Selecciona una opción")
        private set

    fun seleccionarOpcion(opcion: OpcionInicio) {
        mensajeSeleccionado = "Seleccionaste: ${opcion.nombre}"
    }
}