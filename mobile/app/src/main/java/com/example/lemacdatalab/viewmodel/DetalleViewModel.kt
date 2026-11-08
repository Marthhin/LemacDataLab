package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.datos.SesionUsuario
import com.example.lemacdatalab.model.RegistroActividad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DetalleViewModel(
    private val repositorio: RepositorioRegistros,
    private val sesion: SesionUsuario
) : ViewModel() {

    private val idSeleccionado = MutableStateFlow<Int?>(null)

    var registro by mutableStateOf<RegistroActividad?>(null)
        private set

    var cargando by mutableStateOf(true)
        private set

    var eliminando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf("")
        private set

    init {
        observarRegistro()
    }

    fun mostrar(idRegistro: Int) {
        if (idSeleccionado.value == idRegistro) return
        registro = null
        cargando = true
        eliminando = false
        mensajeError = ""
        idSeleccionado.value = idRegistro
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observarRegistro() {
        viewModelScope.launch {
            combine(idSeleccionado, sesion.actual) { id, datos -> id to datos?.idUsuario }
                .flatMapLatest { (id, idUsuario) ->
                    if (id == null || idUsuario == null) {
                        flowOf(null)
                    } else {
                        repositorio.observarPorId(id).map { encontrado ->
                            encontrado?.takeIf { it.idUsuario == idUsuario }
                        }
                    }
                }
                .catch { error ->
                    Log.e("DetalleViewModel", "Error al consultar el registro", error)
                    cargando = false
                    mensajeError = "No se pudo cargar el registro."
                }
                .collect { encontrado ->
                    registro = encontrado
                    cargando = false
                }
        }
    }

    fun eliminar(alEliminar: () -> Unit) {
        val actual = registro ?: return
        if (eliminando) return

        eliminando = true

        viewModelScope.launch {
            try {
                repositorio.eliminar(actual)
                alEliminar()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("DetalleViewModel", "Error al eliminar el registro", error)
                mensajeError = "No se pudo eliminar. Inténtalo de nuevo."
                eliminando = false
            }
        }
    }
}