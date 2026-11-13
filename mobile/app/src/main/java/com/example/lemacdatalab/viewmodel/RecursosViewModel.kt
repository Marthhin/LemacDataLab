package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioContenidos
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.Contenido
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface EstadoCarga {
    data object Cargando : EstadoCarga
    data class Datos(val contenidos: List<Contenido>) : EstadoCarga
    data class Error(val mensaje: String) : EstadoCarga
}

data class EstadoRecursos(
    val carga: EstadoCarga = EstadoCarga.Cargando,
    val filtro: Ambito? = null,
    val idExpandido: Int? = null
) {
    val visibles: List<Contenido>
        get() {
            val todos = (carga as? EstadoCarga.Datos)?.contenidos.orEmpty()
            return filtro?.let { ambito -> todos.filter { it.ambito == ambito } } ?: todos
        }
}

class RecursosViewModel(
    private val repositorio: RepositorioContenidos
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoRecursos())
    val estado: StateFlow<EstadoRecursos> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _estado.update { it.copy(carga = EstadoCarga.Cargando) }

        viewModelScope.launch {
            val resultado = try {
                EstadoCarga.Datos(repositorio.obtenerTodos())
            } catch (error: CancellationException) {
                throw error
            } catch (error: IOException) {
                Log.e("RecursosViewModel", "Sin conexión", error)
                EstadoCarga.Error("No hay conexión a internet. Revisa tu red e inténtalo de nuevo.")
            } catch (error: HttpException) {
                Log.e("RecursosViewModel", "Respuesta HTTP ${error.code()}", error)
                EstadoCarga.Error("El servidor respondió con un error (${error.code()}).")
            } catch (error: Exception) {
                Log.e("RecursosViewModel", "Error al leer los recursos", error)
                EstadoCarga.Error("No se pudieron leer los recursos.")
            }
            _estado.update { it.copy(carga = resultado) }
        }
    }

    fun cambiarFiltro(ambito: Ambito?) {
        _estado.update { it.copy(filtro = ambito, idExpandido = null) }
    }

    fun alternar(idContenido: Int) {
        _estado.update {
            it.copy(idExpandido = if (it.idExpandido == idContenido) null else idContenido)
        }
    }
}
