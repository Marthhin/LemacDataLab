package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.datos.RepositorioUsuarios
import com.example.lemacdatalab.datos.ResultadoCuenta
import com.example.lemacdatalab.datos.SesionUsuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CuentaViewModel(
    private val repositorioUsuarios: RepositorioUsuarios,
    private val repositorioRegistros: RepositorioRegistros,
    private val sesion: SesionUsuario
) : ViewModel() {

    var alias by mutableStateOf("")
        private set

    var totalRegistros by mutableStateOf(0)
        private set

    var contrasenaActual by mutableStateOf("")
        private set

    var contrasenaNueva by mutableStateOf("")
        private set

    var confirmacion by mutableStateOf("")
        private set

    var errorActual by mutableStateOf<String?>(null)
        private set

    var errorNueva by mutableStateOf<String?>(null)
        private set

    var errorConfirmacion by mutableStateOf<String?>(null)
        private set

    var cambiando by mutableStateOf(false)
        private set

    var mensaje by mutableStateOf("")
        private set

    var eliminando by mutableStateOf(false)
        private set

    var errorEliminar by mutableStateOf<String?>(null)
        private set

    init {
        observarCuenta()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observarCuenta() {
        viewModelScope.launch {
            sesion.actual
                .onEach { alias = it?.alias.orEmpty() }
                .flatMapLatest { datos ->
                    if (datos == null) {
                        flowOf(0)
                    } else {
                        repositorioRegistros.observarRegistros(datos.idUsuario).map { it.size }
                    }
                }
                .collect { totalRegistros = it }
        }
    }

    fun reiniciarFormulario() {
        contrasenaActual = ""
        contrasenaNueva = ""
        confirmacion = ""
        errorActual = null
        errorNueva = null
        errorConfirmacion = null
        errorEliminar = null
        mensaje = ""
    }

    fun cambiarActual(valor: String) {
        contrasenaActual = valor
        errorActual = null
        mensaje = ""
    }

    fun cambiarNueva(valor: String) {
        contrasenaNueva = valor
        errorNueva = null
        mensaje = ""
    }

    fun cambiarConfirmacion(valor: String) {
        confirmacion = valor
        errorConfirmacion = null
    }

    fun limpiarMensaje() {
        mensaje = ""
    }

    fun limpiarErrorEliminar() {
        errorEliminar = null
    }

    private fun validarCambio(): Boolean {
        errorActual = if (contrasenaActual.isEmpty()) "Ingresa tu contraseña actual" else null

        errorNueva = when {
            contrasenaNueva.length < 6 -> "La nueva contraseña debe tener al menos 6 caracteres"
            contrasenaNueva == contrasenaActual -> "Debe ser distinta a la actual"
            else -> null
        }

        errorConfirmacion =
            if (confirmacion != contrasenaNueva) "Las contraseñas no coinciden" else null

        return errorActual == null && errorNueva == null && errorConfirmacion == null
    }

    fun cambiarContrasena() {
        if (cambiando || !validarCambio()) return
        val idUsuario = sesion.actual.value?.idUsuario ?: return

        cambiando = true
        viewModelScope.launch {
            try {
                val resultado = withContext(Dispatchers.Default) {
                    repositorioUsuarios.cambiarContrasena(idUsuario, contrasenaActual, contrasenaNueva)
                }

                when (resultado) {
                    ResultadoCuenta.Exito -> {
                        contrasenaActual = ""
                        contrasenaNueva = ""
                        confirmacion = ""
                        mensaje = "Contraseña actualizada"
                    }
                    ResultadoCuenta.ContrasenaIncorrecta ->
                        errorActual = "La contraseña actual no es correcta"
                    ResultadoCuenta.UsuarioNoEncontrado ->
                        mensaje = "No encontramos tu cuenta. Vuelve a iniciar sesión."
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("CuentaViewModel", "Error al cambiar la contraseña", error)
                mensaje = "No se pudo cambiar la contraseña. Inténtalo de nuevo."
            } finally {
                cambiando = false
            }
        }
    }

    fun eliminarCuenta(contrasena: String, alEliminar: () -> Unit) {
        if (eliminando) return
        if (contrasena.isEmpty()) {
            errorEliminar = "Ingresa tu contraseña para confirmar"
            return
        }
        val idUsuario = sesion.actual.value?.idUsuario ?: return

        eliminando = true
        viewModelScope.launch {
            try {
                val resultado = withContext(Dispatchers.Default) {
                    repositorioUsuarios.eliminarCuenta(idUsuario, contrasena)
                }

                when (resultado) {
                    ResultadoCuenta.Exito -> {
                        sesion.cerrar()
                        reiniciarFormulario()
                        alEliminar()
                    }
                    ResultadoCuenta.ContrasenaIncorrecta ->
                        errorEliminar = "La contraseña no es correcta"
                    ResultadoCuenta.UsuarioNoEncontrado ->
                        errorEliminar = "No encontramos tu cuenta"
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("CuentaViewModel", "Error al eliminar la cuenta", error)
                errorEliminar = "No se pudo eliminar la cuenta. Inténtalo de nuevo."
            } finally {
                eliminando = false
            }
        }
    }
}
