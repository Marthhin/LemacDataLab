package com.example.lemacdatalab.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lemacdatalab.datos.RepositorioContenidos
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.Contenido

class RecursosViewModel(
    repositorio: RepositorioContenidos
) : ViewModel() {

    private val contenidos = repositorio.obtenerTodos()

    var filtro by mutableStateOf<Ambito?>(null)
        private set

    var idExpandido by mutableStateOf<Int?>(null)
        private set

    val contenidosVisibles: List<Contenido>
        get() = filtro?.let { ambito ->
            contenidos.filter { it.ambito == ambito }
        } ?: contenidos

    fun cambiarFiltro(ambito: Ambito?) {
        filtro = ambito
        idExpandido = null
    }

    fun alternar(idContenido: Int) {
        idExpandido = if (idExpandido == idContenido) null else idContenido
    }
}