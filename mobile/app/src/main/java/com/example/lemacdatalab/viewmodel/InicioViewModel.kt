package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.datos.SesionUsuario
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.DestinoInicio
import com.example.lemacdatalab.model.EvolucionAmbito
import com.example.lemacdatalab.model.OpcionInicio
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.calcularEvolucion
import com.example.lemacdatalab.util.calcularRacha
import com.example.lemacdatalab.util.fechaDeHoy
import com.example.lemacdatalab.util.ultimosSieteDias
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

    var alias by mutableStateOf("")
        private set

    var totalRegistros by mutableStateOf(0)
        private set

    var periodo by mutableStateOf(PERIODO_SEMANA)
        private set

    var evolucion by mutableStateOf<List<EvolucionAmbito>>(emptyList())
        private set

    var registros by mutableStateOf<List<RegistroActividad>>(emptyList())
        private set

    var ambitosHoy by mutableStateOf(0)
        private set

    val principal: OpcionInicio
        get() = OpcionInicio(
            destino = DestinoInicio.REGISTRO_HOY,
            nombre = "Registro de hoy",
            descripcion = when (ambitosHoy) {
                0 -> "Recorre los cuatro ámbitos en pocos minutos"
                Ambito.entries.size -> "Completo ✓ · Toca para corregir"
                else -> "$ambitosHoy de ${Ambito.entries.size} ámbitos registrados hoy"
            },
            icono = if (ambitosHoy == Ambito.entries.size) Icons.Rounded.TaskAlt else Icons.Rounded.AddCircle
        )

    fun evolucionDe(ambito: Ambito): EvolucionAmbito? = evolucion.find { it.ambito == ambito }

    fun registrosDe(ambito: Ambito): List<RegistroActividad> = registros.filter { it.idAmbito == ambito.id }

    var racha by mutableStateOf(0)
        private set

    var semana by mutableStateOf(List(7) { false })
        private set

    var registroHoy by mutableStateOf(false)
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
                    registros = lista
                    evolucion = calcularEvolucion(lista, periodo)
                    val fechas = lista.map { it.fechaHora }.toSet()
                    racha = calcularRacha(fechas)
                    semana = ultimosSieteDias(fechas)
                    registroHoy = fechaDeHoy() in fechas
                    ambitosHoy = lista.filter { it.fechaHora == fechaDeHoy() }.map { it.idAmbito }.distinct().size
                    cargando = false
                    mensajeError = ""
                }
        }
    }

    fun cambiarPeriodo(dias: Int) {
        if (dias == periodo) return
        periodo = dias
        evolucion = calcularEvolucion(registros, dias)
    }

    companion object {
        const val PERIODO_SEMANA = 7
        const val PERIODO_MES = 30
    }
}
