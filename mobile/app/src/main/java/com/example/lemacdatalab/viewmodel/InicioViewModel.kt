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
import com.example.lemacdatalab.datos.SesionUsuario
import com.example.lemacdatalab.model.OpcionInicio
import com.example.lemacdatalab.model.PuntoGrafico
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.util.claveFecha
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_MAXIMA
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_MINIMA
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar

class InicioViewModel(
    private val repositorio: RepositorioRegistros,
    private val sesion: SesionUsuario
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
            descripcion = "Ejercicios y lecturas breves",
            icono = Icons.Rounded.Spa
        ),
        OpcionInicio(
            nombre = "Recordatorios",
            descripcion = "Programa un aviso diario",
            icono = Icons.Rounded.NotificationsActive
        )
    )

    var alias by mutableStateOf("")
        private set

    var totalRegistros by mutableStateOf(0)
        private set

    var ultimoRegistro by mutableStateOf<RegistroActividad?>(null)
        private set

    var puntosGrafico by mutableStateOf<List<PuntoGrafico>>(emptyList())
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observarResumen() {
        viewModelScope.launch {
            sesion.actual
                .onEach { datos ->
                    alias = datos?.alias.orEmpty()
                    cargando = true
                }
                .flatMapLatest { datos ->
                    if (datos == null) {
                        flowOf(emptyList())
                    } else {
                        repositorio.observarRegistros(datos.idUsuario)
                    }
                }
                .catch { error ->
                    Log.e("InicioViewModel", "Error al consultar el resumen", error)
                    cargando = false
                    mensajeError = "No se pudo cargar tu resumen."
                }
                .collect { lista ->
                    totalRegistros = lista.size
                    ultimoRegistro = lista.maxByOrNull { it.idRegistro }
                    puntosGrafico = calcularPuntos(lista)
                    cargando = false
                    mensajeError = ""
                }
        }
    }

    private fun calcularPuntos(lista: List<RegistroActividad>): List<PuntoGrafico> {
        return lista
            .groupBy { it.fechaHora }
            .mapNotNull { (fecha, delDia) ->
                val clave = claveFecha(fecha) ?: return@mapNotNull null
                val promedio = delDia
                    .map { it.intensidad.coerceIn(INTENSIDAD_MINIMA, INTENSIDAD_MAXIMA) }
                    .average()
                    .toFloat()
                clave to PuntoGrafico(fecha, promedio)
            }
            .sortedBy { (clave, _) -> clave }
            .takeLast(MAXIMO_PUNTOS)
            .map { (_, punto) -> punto }
    }

    private companion object {
        const val MAXIMO_PUNTOS = 10
    }
}