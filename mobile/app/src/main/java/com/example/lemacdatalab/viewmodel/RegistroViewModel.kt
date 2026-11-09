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
import com.example.lemacdatalab.util.esFechaFutura
import com.example.lemacdatalab.util.fechaDeHoy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repositorio: RepositorioRegistros,
    private val sesion: SesionUsuario
) : ViewModel() {

    var fechaHora by mutableStateOf("")
        private set

    var tipoActividad by mutableStateOf("")
        private set

    var contexto by mutableStateOf("")
        private set

    var intensidad by mutableStateOf(INTENSIDAD_INICIAL.toString())
        private set

    var nota by mutableStateOf("")
        private set

    var ambito by mutableStateOf("")
        private set

    var emocion by mutableStateOf("")
        private set

    var habilidad by mutableStateOf("")
        private set

    var estrategia by mutableStateOf("")
        private set

    var desencadenante by mutableStateOf("")
        private set

    var accionProtectora by mutableStateOf("")
        private set

    var resultado by mutableStateOf("")
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

    var errorEmocion by mutableStateOf<String?>(null)
        private set

    var errorHabilidad by mutableStateOf<String?>(null)
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

    val ambitoSeleccionado: Ambito?
        get() = Ambito.entries.find { it.nombre == ambito }

    fun comenzarNuevoRegistro() {
        if (guardando) return

        registroEnEdicion = null
        registroActual = null

        fechaHora = fechaDeHoy()
        tipoActividad = ""
        contexto = ""
        intensidad = INTENSIDAD_INICIAL.toString()
        nota = ""
        ambito = ""
        limpiarCamposAmbito()
        limpiarErrores()
    }

    fun comenzarEditarRegistro(registro: RegistroActividad) {
        if (guardando) return

        registroEnEdicion = registro
        registroActual = null

        fechaHora = registro.fechaHora
        tipoActividad = registro.tipoActividad
        contexto = registro.contexto
        intensidad = registro.intensidad.coerceIn(INTENSIDAD_MINIMA, INTENSIDAD_MAXIMA).toString()
        nota = registro.nota.orEmpty()
        ambito = Ambito.desdeId(registro.idAmbito)?.nombre.orEmpty()
        emocion = registro.emocion.orEmpty()
        habilidad = registro.habilidad.orEmpty()
        estrategia = registro.estrategia.orEmpty()
        desencadenante = registro.desencadenante.orEmpty()
        accionProtectora = registro.accionProtectora.orEmpty()
        resultado = registro.resultado.orEmpty()

        limpiarErrores()
    }

    private fun limpiarCamposAmbito() {
        emocion = ""
        habilidad = ""
        estrategia = ""
        desencadenante = ""
        accionProtectora = ""
        resultado = ""
    }

    private fun limpiarErrores() {
        errorFechaHora = null
        errorTipoActividad = null
        errorContexto = null
        errorIntensidad = null
        errorAmbito = null
        errorEmocion = null
        errorHabilidad = null
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
        if (valor != ambito) {
            resultado = ""
        }
        ambito = valor
        errorAmbito = null
        errorEmocion = null
        errorHabilidad = null
    }

    fun cambiarEmocion(valor: String) {
        emocion = valor
        errorEmocion = null
    }

    fun cambiarHabilidad(valor: String) {
        habilidad = valor
        errorHabilidad = null
    }

    fun cambiarEstrategia(valor: String) {
        estrategia = valor
    }

    fun cambiarDesencadenante(valor: String) {
        desencadenante = valor
    }

    fun cambiarAccionProtectora(valor: String) {
        accionProtectora = valor
    }

    fun cambiarResultado(valor: String) {
        resultado = valor
    }

    private fun validarFormulario(): Boolean {
        val seleccionado = ambitoSeleccionado

        errorFechaHora = when {
            fechaHora.isBlank() -> "Selecciona una fecha"
            esFechaFutura(fechaHora) -> "La fecha no puede ser posterior a hoy"
            else -> null
        }

        errorTipoActividad =
            if (tipoActividad.isBlank()) "Este campo es obligatorio" else null

        errorContexto =
            if (contexto.isBlank()) "Ingresa el contexto" else null

        errorAmbito =
            if (seleccionado == null) "Selecciona un ámbito" else null

        errorEmocion =
            if (seleccionado == Ambito.ANIMO && emocion.isBlank()) "Elige cómo te sentiste" else null

        errorHabilidad =
            if (seleccionado == Ambito.DBT && habilidad.isBlank()) "Elige la habilidad que practicaste" else null

        val valorIntensidad = intensidad.toIntOrNull()
        errorIntensidad = when {
            valorIntensidad == null -> "Selecciona una intensidad"
            valorIntensidad !in INTENSIDAD_MINIMA..INTENSIDAD_MAXIMA ->
                "La intensidad debe estar entre $INTENSIDAD_MINIMA y $INTENSIDAD_MAXIMA"
            else -> null
        }

        val esValido = listOf(
            errorFechaHora,
            errorTipoActividad,
            errorContexto,
            errorAmbito,
            errorEmocion,
            errorHabilidad,
            errorIntensidad
        ).all { it == null }

        if (!esValido) {
            mensajeResultado = "Revisa los campos marcados"
        }

        return esValido
    }

    fun guardarRegistro(alGuardarCorrectamente: () -> Unit) {
        if (guardando || !validarFormulario()) return

        val ambitoElegido = ambitoSeleccionado ?: return
        val original = registroEnEdicion

        val idUsuario = original?.idUsuario ?: sesion.actual.value?.idUsuario
        if (idUsuario == null) {
            mensajeResultado = "Tu sesión terminó. Vuelve a ingresar."
            return
        }

        val registro = RegistroActividad(
            idRegistro = original?.idRegistro ?: 0,
            idUsuario = idUsuario,
            idAmbito = ambitoElegido.id,
            fechaHora = fechaHora,
            tipoActividad = tipoActividad.trim(),
            intensidad = intensidad.toInt(),
            contexto = contexto.trim(),
            nota = nota.oNulo(),
            estado = original?.estado ?: "Prueba",
            emocion = emocion.oNulo().takeIf { ambitoElegido == Ambito.ANIMO },
            habilidad = habilidad.oNulo().takeIf { ambitoElegido == Ambito.DBT },
            estrategia = estrategia.oNulo().takeIf { ambitoElegido == Ambito.NEURODESARROLLO },
            desencadenante = desencadenante.oNulo().takeIf { ambitoElegido == Ambito.ADICCIONES },
            accionProtectora = accionProtectora.oNulo().takeIf { ambitoElegido == Ambito.ADICCIONES },
            resultado = resultado.oNulo().takeIf { ambitoElegido != Ambito.ANIMO }
        )

        guardando = true
        mensajeResultado = if (original == null) "Guardando registro..." else "Actualizando registro..."

        viewModelScope.launch {
            try {
                if (original == null) {
                    val idGuardado = repositorio.insertar(registro)

                    check(idGuardado > 0) {
                        "La base de datos no devolvió un identificador válido"
                    }

                    registroActual = registro.copy(idRegistro = idGuardado.toInt())
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
                Log.e("RegistroViewModel", "Error al guardar o actualizar el registro", error)
                mensajeResultado = "No se pudieron guardar los cambios. Inténtalo de nuevo."
                return@launch
            } finally {
                guardando = false
            }

            alGuardarCorrectamente()
        }
    }

    private fun String.oNulo(): String? = trim().ifEmpty { null }

    companion object {
        const val INTENSIDAD_MINIMA = 1
        const val INTENSIDAD_MAXIMA = 10
        const val INTENSIDAD_INICIAL = 5
    }
}
