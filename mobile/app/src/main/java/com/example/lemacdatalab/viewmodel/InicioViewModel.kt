package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.model.OpcionInicio
import com.example.lemacdatalab.model.RegistroActividad
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.flow.collect

class InicioViewModel(
    private val repositorio: RepositorioRegistros
) : ViewModel() {

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

    var totalRegistros by mutableStateOf(0)
        private set

    var ultimoRegistro by mutableStateOf<RegistroActividad?>(null)
        private set

    var cargando by mutableStateOf(true)
        private set

    var mensajeError by mutableStateOf("")
        private set

    val saludo: String
        get() = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Buenos días"
            in 12..19 -> "Buenas tardes"
            else -> "Buenas noches"
        }

    init {
        observarResumen()
    }

    private fun observarResumen() {
        viewModelScope.launch {
            repositorio.observarRegistros()
                .catch { error ->
                    Log.e("InicioViewModel", "Error al consultar el resumen", error)
                    cargando = false
                    mensajeError = "No se pudo cargar tu resumen."
                }
                .collect { lista ->
                    totalRegistros = lista.size
                    ultimoRegistro = lista.maxByOrNull { it.idRegistro }
                    cargando = false
                    mensajeError = ""
                }
        }
    }
}