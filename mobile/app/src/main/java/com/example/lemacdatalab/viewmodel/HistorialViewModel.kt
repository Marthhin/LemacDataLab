package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.datos.SesionUsuario
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.RegistroActividad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HistorialViewModel(
    private val repositorio: RepositorioRegistros,
    private val sesion: SesionUsuario
) : ViewModel() {

    var registros by mutableStateOf<List<RegistroActividad>>(emptyList())
        private set

    var cargando by mutableStateOf(true)
        private set

    var mensajeError by mutableStateOf("")
        private set

    var eliminando by mutableStateOf(false)
        private set

    var mensajeOperacion by mutableStateOf("")
        private set

    var registroEliminado by mutableStateOf<RegistroActividad?>(null)
        private set

    var filtro by mutableStateOf<Ambito?>(null)
        private set

    var busqueda by mutableStateOf("")
        private set

    val registrosVisibles: List<RegistroActividad>
        get() {
            val ambito = filtro
            val texto = busqueda.trim()
            return registros.filter { registro ->
                (ambito == null || registro.idAmbito == ambito.id) &&
                        (texto.isEmpty() || coincideConBusqueda(registro, texto))
            }
        }

    init {
        observarRegistros()
    }

    fun cambiarFiltro(ambito: Ambito?) {
        filtro = ambito
    }

    fun cambiarBusqueda(texto: String) {
        busqueda = texto
    }

    fun limpiarFiltros() {
        filtro = null
        busqueda = ""
    }

    private fun coincideConBusqueda(registro: RegistroActividad, texto: String): Boolean =
        listOfNotNull(
            registro.tipoActividad,
            registro.contexto,
            registro.nota,
            registro.emocion,
            registro.habilidad,
            registro.estrategia,
            registro.desencadenante,
            registro.accionProtectora
        ).any { it.contains(texto, ignoreCase = true) }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observarRegistros() {
        viewModelScope.launch {
            sesion.actual
                .onEach {
                    cargando = true
                    mensajeOperacion = ""
                    registroEliminado = null
                }
                .flatMapLatest { datos ->
                    if (datos == null) {
                        flowOf(emptyList())
                    } else {
                        repositorio.observarRegistros(datos.idUsuario)
                    }
                }
                .catch { error ->
                    Log.e("HistorialViewModel", "Error al consultar los registros", error)
                    cargando = false
                    mensajeError = "No se pudieron cargar los registros."
                }
                .collect { lista ->
                    registros = lista
                    cargando = false
                    mensajeError = ""
                }
        }
    }

    fun eliminarRegistro(registro: RegistroActividad) {
        if (eliminando) return

        eliminando = true

        viewModelScope.launch {
            try {
                val filasEliminadas = repositorio.eliminar(registro)

                if (filasEliminadas == 1) {
                    registroEliminado = registro
                } else {
                    mensajeOperacion = "El registro ya no se encuentra en la base de datos."
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("HistorialViewModel", "Error al eliminar el registro", error)
                mensajeOperacion = "No se pudo eliminar. Inténtalo de nuevo."
            } finally {
                eliminando = false
            }
        }
    }

    fun deshacerEliminacion() {
        val registro = registroEliminado ?: return
        registroEliminado = null

        viewModelScope.launch {
            try {
                repositorio.insertar(registro)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("HistorialViewModel", "Error al restaurar el registro", error)
                mensajeOperacion = "No se pudo restaurar el registro."
            }
        }
    }

    fun descartarEliminacion() {
        registroEliminado = null
    }

    fun limpiarMensaje() {
        mensajeOperacion = ""
    }
}