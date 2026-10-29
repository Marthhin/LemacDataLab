package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.model.RegistroActividad
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistorialViewModel(
    private val repositorio: RepositorioRegistros
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

    init {
        observarRegistros()
    }

    private fun observarRegistros() {
        viewModelScope.launch {
            repositorio.observarRegistros()
                .catch { error ->
                    Log.e(
                        "HistorialViewModel",
                        "Error al consultar los registros",
                        error
                    )
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
        mensajeOperacion = "Eliminando registro..."

        viewModelScope.launch {
            try {
                val filasEliminadas = repositorio.eliminar(registro)

                mensajeOperacion = if (filasEliminadas == 1) {
                    "Registro eliminado correctamente."
                } else {
                    "El registro ya no se encuentra en la base de datos."
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e(
                    "HistorialViewModel",
                    "Error al eliminar el registro",
                    error
                )
                mensajeOperacion = "No se pudo eliminar. Inténtalo de nuevo."
            } finally {
                eliminando = false
            }
        }
    }
}