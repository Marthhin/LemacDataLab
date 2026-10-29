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
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repositorio: RepositorioRegistros
) : ViewModel() {

    var fechaHora by mutableStateOf("")
        private set

    var tipoActividad by mutableStateOf("")
        private set

    var contexto by mutableStateOf("")
        private set

    var intensidad by mutableStateOf("")
        private set

    var nota by mutableStateOf("")
        private set

    var ambito by mutableStateOf("")
        private set

    var errorFechaHora by mutableStateOf<String?>(null)
        private set

    var errorTipoActividad by mutableStateOf<String?>(null)
        private set

    var errorContexto by mutableStateOf<String?>(null)
        private set

    var errorIntensidad by mutableStateOf<String?>(null)
        private set

    var errorAmbito by mutableStateOf<String?>(null)
        private set

    var mensajeResultado by mutableStateOf("")
        private set

    var registroActual by mutableStateOf<RegistroActividad?>(null)
        private set

    var guardando by mutableStateOf(false)
        private set

    private var registroEnEdicion by mutableStateOf<RegistroActividad?>(null)

    val editando: Boolean
        get() = registroEnEdicion != null

    fun comenzarNuevoRegistro() {
        if (guardando) return

        registroEnEdicion = null
        registroActual = null

        fechaHora = ""
        tipoActividad = ""
        contexto = ""
        intensidad = ""
        nota = ""
        ambito = ""

        limpiarErrores()
    }

    fun comenzarEditarRegistro(registro: RegistroActividad) {
        if (guardando) return

        registroEnEdicion = registro
        registroActual = null

        fechaHora = registro.fechaHora
        tipoActividad = registro.tipoActividad
        contexto = registro.contexto
        intensidad = registro.intensidad.toString()
        nota = registro.nota.orEmpty()

        ambito = when (registro.idAmbito) {
            1 -> "Neurodesarrollo"
            2 -> "DBT"
            3 -> "Estado de ánimo"
            4 -> "Adicciones"
            else -> ""
        }

        limpiarErrores()
    }

    private fun limpiarErrores() {
        errorFechaHora = null
        errorTipoActividad = null
        errorContexto = null
        errorIntensidad = null
        errorAmbito = null
        mensajeResultado = ""
    }

    fun cambiarFechaHora(valor: String) {
        fechaHora = valor
        errorFechaHora = null
    }

    fun cambiarTipoActividad(valor: String) {
        tipoActividad = valor
        errorTipoActividad = null
    }

    fun cambiarContexto(valor: String) {
        contexto = valor
        errorContexto = null
    }

    fun cambiarIntensidad(valor: String) {
        intensidad = valor
        errorIntensidad = null
    }

    fun cambiarNota(valor: String) {
        nota = valor
    }

    fun cambiarAmbito(valor: String) {
        ambito = valor
        errorAmbito = null
    }

    private fun validarFormulario(): Boolean {
        errorFechaHora =
            if (fechaHora.isBlank()) "Selecciona una fecha" else null

        errorTipoActividad =
            if (tipoActividad.isBlank()) "La actividad es obligatoria" else null

        errorContexto =
            if (contexto.isBlank()) "Ingresa el contexto" else null

        errorAmbito =
            if (ambito.isBlank()) "Selecciona un ámbito" else null

        errorIntensidad = when {
            intensidad.isBlank() -> "Ingresa la intensidad"
            intensidad.toIntOrNull() == null ->
                "La intensidad debe ser un número entero"
            else -> null
        }

        val esValido = errorFechaHora == null &&
                errorTipoActividad == null &&
                errorContexto == null &&
                errorAmbito == null &&
                errorIntensidad == null

        if (!esValido) {
            mensajeResultado = "Revisa los campos marcados"
        }

        return esValido
    }

    fun guardarRegistro(alGuardarCorrectamente: () -> Unit) {
        if (guardando || !validarFormulario()) return

        val idAmbitoSeleccionado = when (ambito) {
            "Neurodesarrollo" -> 1
            "DBT" -> 2
            "Estado de ánimo" -> 3
            "Adicciones" -> 4
            else -> {
                errorAmbito = "Selecciona un ámbito"
                return
            }
        }

        val original = registroEnEdicion

        val registro = RegistroActividad(
            idRegistro = original?.idRegistro ?: 0,
            idUsuario = original?.idUsuario ?: 1,
            idAmbito = idAmbitoSeleccionado,
            fechaHora = fechaHora,
            tipoActividad = tipoActividad.trim(),
            intensidad = intensidad.toInt(),
            contexto = contexto.trim(),
            nota = nota.trim().ifEmpty { null },
            estado = original?.estado ?: "Prueba"
        )

        guardando = true
        mensajeResultado = if (original == null) {
            "Guardando registro..."
        } else {
            "Actualizando registro..."
        }

        viewModelScope.launch {
            try {
                if (original == null) {
                    val idGuardado = repositorio.insertar(registro)

                    check(idGuardado > 0) {
                        "La base de datos no devolvió un identificador válido"
                    }

                    registroActual = registro.copy(
                        idRegistro = idGuardado.toInt()
                    )
                    mensajeResultado = "Registro guardado en el dispositivo"
                } else {
                    val filasActualizadas = repositorio.actualizar(registro)

                    check(filasActualizadas == 1) {
                        "No se encontró el registro que se quería actualizar"
                    }

                    registroActual = registro
                    mensajeResultado = "Registro actualizado correctamente"
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e(
                    "RegistroViewModel",
                    "Error al guardar o actualizar el registro",
                    error
                )
                mensajeResultado = "No se pudieron guardar los cambios. Inténtalo de nuevo."
                return@launch
            } finally {
                guardando = false
            }

            alGuardarCorrectamente()
        }
    }
}