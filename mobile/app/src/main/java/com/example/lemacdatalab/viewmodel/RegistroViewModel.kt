package com.example.lemacdatalab.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lemacdatalab.model.RegistroActividad

class RegistroViewModel : ViewModel() {

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

    fun validarFormulario(): Boolean {
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
            return false
        }

        val idAmbitoSeleccionado = when (ambito) {
            "Neurodesarrollo" -> 1
            "DBT" -> 2
            "Estado de ánimo" -> 3
            "Adicciones" -> 4
            else -> return false
        }

        registroActual = RegistroActividad(
            idRegistro = 1,
            idUsuario = 1,
            idAmbito = idAmbitoSeleccionado,
            fechaHora = fechaHora,
            tipoActividad = tipoActividad.trim(),
            intensidad = intensidad.toInt(),
            contexto = contexto.trim(),
            nota = nota.trim().ifEmpty { null },
            estado = "Prueba"
        )

        mensajeResultado = "Registro de prueba creado"
        return true
    }
}