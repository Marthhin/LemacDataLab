package com.example.lemacdatalab.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lemacdatalab.model.OpcionInicio

class InicioViewModel : ViewModel() {

    val opciones = listOf(
        OpcionInicio("Registrar actividad"),
        OpcionInicio("Ver historial"),
        OpcionInicio("Recursos de apoyo"),
        OpcionInicio("Recordatorios")
    )

    var mensajeSeleccionado by mutableStateOf("Selecciona una opción")
        private set

    fun seleccionarOpcion(opcion: OpcionInicio) {
        mensajeSeleccionado = "Seleccionaste: ${opcion.nombre}"
    }
}