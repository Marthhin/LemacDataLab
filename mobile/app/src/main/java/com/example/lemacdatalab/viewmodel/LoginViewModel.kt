package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioUsuarios
import com.example.lemacdatalab.datos.ResultadoAcceso
import com.example.lemacdatalab.datos.SesionUsuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val repositorio: RepositorioUsuarios,
    private val sesion: SesionUsuario
) : ViewModel() {

    var creandoCuenta by mutableStateOf(false)
        private set

    var alias by mutableStateOf("")
        private set

    var contrasena by mutableStateOf("")
        private set

    var confirmacion by mutableStateOf("")
        private set

    var errorAlias by mutableStateOf<String?>(null)
        private set

    var errorContrasena by mutableStateOf<String?>(null)
        private set

    var errorConfirmacion by mutableStateOf<String?>(null)
        private set

    var mensajeError by mutableStateOf("")
        private set

    var cargando by mutableStateOf(false)
        private set

    fun cambiarAlias(valor: String) {
        alias = valor
        errorAlias = null
        mensajeError = ""
    }

    fun cambiarContrasena(valor: String) {
        contrasena = valor
        errorContrasena = null
        mensajeError = ""
    }

    fun cambiarConfirmacion(valor: String) {
        confirmacion = valor
        errorConfirmacion = null
    }

    fun cambiarModo() {
        if (cargando) return
        creandoCuenta = !creandoCuenta
        contrasena = ""
        confirmacion = ""
        errorAlias = null
        errorContrasena = null
        errorConfirmacion = null
        mensajeError = ""
    }

    private fun validar(): Boolean {
        val aliasLimpio = alias.trim()

        errorAlias = when {
            aliasLimpio.isEmpty() -> "Ingresa tu alias"
            creandoCuenta && aliasLimpio.length !in 3..20 ->
                "El alias debe tener entre 3 y 20 caracteres"
            creandoCuenta && !aliasLimpio.all { it.isLetterOrDigit() || it == '_' } ->
                "Usa solo letras, números o guion bajo"
            else -> null
        }

        errorContrasena = when {
            contrasena.isEmpty() -> "Ingresa tu contraseña"
            creandoCuenta && contrasena.length < 6 ->
                "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }

        errorConfirmacion =
            if (creandoCuenta && confirmacion != contrasena) "Las contraseñas no coinciden" else null

        return errorAlias == null && errorContrasena == null && errorConfirmacion == null
    }

    fun enviar(alEntrar: () -> Unit) {
        if (cargando || !validar()) return

        cargando = true
        mensajeError = ""
        val aliasLimpio = alias.trim()

        viewModelScope.launch {
            try {
                val resultado = withContext(Dispatchers.Default) {
                    if (creandoCuenta) {
                        repositorio.crearCuenta(aliasLimpio, contrasena)
                    } else {
                        repositorio.iniciarSesion(aliasLimpio, contrasena)
                    }
                }

                when (resultado) {
                    is ResultadoAcceso.Exito -> {
                        sesion.iniciar(resultado.usuario.idUsuario, resultado.usuario.alias)
                        contrasena = ""
                        confirmacion = ""
                        alEntrar()
                    }
                    ResultadoAcceso.AliasEnUso -> errorAlias = "Ese alias ya está en uso"
                    ResultadoAcceso.CredencialesInvalidas ->
                        mensajeError = "Alias o contraseña incorrectos"
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("LoginViewModel", "Error al acceder", error)
                mensajeError = "No se pudo completar. Inténtalo de nuevo."
            } finally {
                cargando = false
            }
        }
    }
}