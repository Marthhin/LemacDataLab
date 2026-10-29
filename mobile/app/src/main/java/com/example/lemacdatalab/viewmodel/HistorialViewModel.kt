package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.model.RegistroActividad
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
}