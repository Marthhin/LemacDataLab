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
import com.example.lemacdatalab.model.CamposAmbito
import com.example.lemacdatalab.model.Escala
import com.example.lemacdatalab.model.OPCION_OTRO
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.separarSeleccion
import com.example.lemacdatalab.model.unirSeleccion
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

    var intensidad by mutableStateOf(INTENSIDAD_INICIAL)
        private set

    var elevacion by mutableStateOf(INTENSIDAD_INICIAL)
        private set

    var horasSueno by mutableStateOf("")
        private set

    var altoRiesgo by mutableStateOf(false)
        private set

    var nota by mutableStateOf("")
        private set

    var ambito by mutableStateOf("")
        private set

    var emocion by mutableStateOf("")
        private set

    var habilidad by mutableStateOf("")
        private set

    var habilidadOtra by mutableStateOf("")
        private set

    var estrategia by mutableStateOf("")
        private set

    var desencadenante by mutableStateOf("")
        private set

    var desencadenanteOtro by mutableStateOf("")
        private set

    var accionProtectora by mutableStateOf("")
        private set

    var accionProtectoraOtra by mutableStateOf("")
        private set

    var resultado by mutableStateOf("")
        private set

    var errorFechaHora by mutableStateOf<String?>(null)
        private set

    var errorTipoActividad by mutableStateOf<String?>(null)
        private set

    var errorContexto by mutableStateOf<String?>(null)
        private set

    var errorAmbito by mutableStateOf<String?>(null)
        private set

    var errorHabilidad by mutableStateOf<String?>(null)
        private set

    var errorHorasSueno by mutableStateOf<String?>(null)
        private set

    var errorOtro by mutableStateOf<String?>(null)
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
        intensidad = INTENSIDAD_INICIAL
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
        intensidad = registro.intensidad.coerceIn(INTENSIDAD_MINIMA, INTENSIDAD_MAXIMA)
        elevacion = (registro.elevacion ?: INTENSIDAD_INICIAL).coerceIn(INTENSIDAD_MINIMA, INTENSIDAD_MAXIMA)
        horasSueno = registro.horasSueno?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }.orEmpty()
        altoRiesgo = registro.altoRiesgo
        nota = registro.nota.orEmpty()
        ambito = Ambito.desdeId(registro.idAmbito)?.nombre.orEmpty()
        emocion = registro.emocion.orEmpty()
        estrategia = registro.estrategia.orEmpty()
        resultado = registro.resultado.orEmpty()

        separarSeleccion(registro.habilidad, CamposAmbito.HABILIDADES_DBT).let {
            habilidad = it.opcion
            habilidadOtra = it.otro
        }
        separarSeleccion(registro.desencadenante, CamposAmbito.DESENCADENANTES).let {
            desencadenante = it.opcion
            desencadenanteOtro = it.otro
        }
        separarSeleccion(registro.accionProtectora, CamposAmbito.ACCIONES_PROTECTORAS).let {
            accionProtectora = it.opcion
            accionProtectoraOtra = it.otro
        }

        limpiarErrores()
    }

    private fun limpiarCamposAmbito() {
        elevacion = INTENSIDAD_INICIAL
        horasSueno = ""
        altoRiesgo = false
        emocion = ""
        habilidad = ""
        habilidadOtra = ""
        estrategia = ""
        desencadenante = ""
        desencadenanteOtro = ""
        accionProtectora = ""
        accionProtectoraOtra = ""
        resultado = ""
    }

    private fun limpiarErrores() {
        errorFechaHora = null
        errorTipoActividad = null
        errorContexto = null
        errorAmbito = null
        errorHabilidad = null
        errorHorasSueno = null
        errorOtro = null
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

    fun cambiarIntensidad(valor: Int) {
        intensidad = valor.coerceIn(INTENSIDAD_MINIMA, INTENSIDAD_MAXIMA)
    }

    fun cambiarElevacion(valor: Int) {
        elevacion = valor.coerceIn(INTENSIDAD_MINIMA, INTENSIDAD_MAXIMA)
    }

    fun cambiarHorasSueno(valor: String) {
        horasSueno = valor.replace(',', '.').filter { it.isDigit() || it == '.' }.take(4)
        errorHorasSueno = null
    }

    fun cambiarAltoRiesgo(valor: Boolean) {
        altoRiesgo = valor
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
        errorHabilidad = null
        errorOtro = null
    }

    fun cambiarEmocion(valor: String) {
        emocion = valor
    }

    fun cambiarHabilidad(valor: String) {
        habilidad = valor
        errorHabilidad = null
        errorOtro = null
    }

    fun cambiarHabilidadOtra(valor: String) {
        habilidadOtra = valor
        errorOtro = null
    }

    fun cambiarEstrategia(valor: String) {
        estrategia = valor
    }

    fun cambiarDesencadenante(valor: String) {
        desencadenante = valor
        errorOtro = null
    }

    fun cambiarDesencadenanteOtro(valor: String) {
        desencadenanteOtro = valor
        errorOtro = null
    }

    fun cambiarAccionProtectora(valor: String) {
        accionProtectora = valor
        errorOtro = null
    }

    fun cambiarAccionProtectoraOtra(valor: String) {
        accionProtectoraOtra = valor
        errorOtro = null
    }

    fun cambiarResultado(valor: String) {
        resultado = valor
    }

    private fun otroSinEspecificar(opcion: String, texto: String) =
        opcion == OPCION_OTRO && texto.isBlank()

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

        errorHabilidad =
            if (seleccionado == Ambito.DBT && habilidad.isBlank()) "Elige la habilidad que practicaste" else null

        errorHorasSueno = when {
            seleccionado != Ambito.ANIMO || horasSueno.isBlank() -> null
            horasSueno.toDoubleOrNull()?.takeIf { it in 0.0..24.0 } == null -> "Ingresa un número entre 0 y 24"
            else -> null
        }

        errorOtro = when {
            seleccionado == Ambito.DBT && otroSinEspecificar(habilidad, habilidadOtra) ||
                seleccionado == Ambito.ADICCIONES && (
                    otroSinEspecificar(desencadenante, desencadenanteOtro) ||
                        otroSinEspecificar(accionProtectora, accionProtectoraOtra)
                    ) -> "Especifica la opción «Otro»"
            else -> null
        }

        val esValido = listOf(
            errorFechaHora,
            errorTipoActividad,
            errorContexto,
            errorAmbito,
            errorHabilidad,
            errorHorasSueno,
            errorOtro
        ).all { it == null }

        if (!esValido) {
            mensajeResultado = "Revisa los campos marcados"
        }

        return esValido
    }

    private fun construirRegistro(ambitoElegido: Ambito, idUsuario: Int, original: RegistroActividad?) =
        RegistroActividad(
            idRegistro = original?.idRegistro ?: 0,
            idUsuario = idUsuario,
            idAmbito = ambitoElegido.id,
            fechaHora = fechaHora,
            tipoActividad = tipoActividad.trim(),
            intensidad = intensidad,
            contexto = contexto.trim(),
            nota = nota.oNulo(),
            estado = original?.estado ?: "Activo",
            emocion = emocion.oNulo().takeIf { ambitoElegido == Ambito.ANIMO },
            habilidad = unirSeleccion(habilidad, habilidadOtra).takeIf { ambitoElegido == Ambito.DBT },
            estrategia = estrategia.oNulo().takeIf { ambitoElegido == Ambito.NEURODESARROLLO },
            desencadenante = unirSeleccion(desencadenante, desencadenanteOtro)
                .takeIf { ambitoElegido == Ambito.ADICCIONES },
            accionProtectora = unirSeleccion(accionProtectora, accionProtectoraOtra)
                .takeIf { ambitoElegido == Ambito.ADICCIONES },
            resultado = resultado.oNulo().takeIf { ambitoElegido != Ambito.ANIMO },
            elevacion = elevacion.takeIf { ambitoElegido == Ambito.ANIMO },
            horasSueno = horasSueno.toDoubleOrNull().takeIf { ambitoElegido == Ambito.ANIMO },
            altoRiesgo = altoRiesgo && ambitoElegido == Ambito.ANIMO,
            editado = original?.editado ?: false
        )

    fun guardarRegistro(alGuardarCorrectamente: () -> Unit) {
        if (guardando || !validarFormulario()) return

        val ambitoElegido = ambitoSeleccionado ?: return
        val original = registroEnEdicion

        val idUsuario = original?.idUsuario ?: sesion.actual.value?.idUsuario
        if (idUsuario == null) {
            mensajeResultado = "Tu sesión terminó. Vuelve a ingresar."
            return
        }

        guardando = true
        mensajeResultado = if (original == null) "Guardando registro..." else "Actualizando registro..."

        viewModelScope.launch {
            try {
                val existente = repositorio.buscarDelDia(idUsuario, fechaHora, ambitoElegido.id)
                    ?.takeIf { it.idRegistro != original?.idRegistro }

                when {
                    original != null && existente != null -> {
                        mensajeResultado =
                            "Ya tienes un registro de ${ambitoElegido.nombre.lowercase()} el $fechaHora. Edita ese en su lugar."
                        return@launch
                    }

                    original != null -> {
                        val registro = construirRegistro(ambitoElegido, idUsuario, original)
                        check(repositorio.actualizar(registro, original) == 1) {
                            "No se encontró el registro que se quería actualizar"
                        }
                        registroActual = registro.copy(editado = true)
                        mensajeResultado = "Registro actualizado. La versión anterior quedó guardada."
                    }

                    existente != null -> {
                        val registro = construirRegistro(ambitoElegido, idUsuario, existente)
                        check(repositorio.actualizar(registro, existente) == 1) {
                            "No se encontró el registro del día"
                        }
                        registroActual = registro.copy(editado = true)
                        mensajeResultado =
                            "Ya tenías un registro de ${ambitoElegido.nombre.lowercase()} ese día: lo actualizamos y guardamos la versión anterior."
                    }

                    else -> {
                        val registro = construirRegistro(ambitoElegido, idUsuario, null)
                        val idGuardado = repositorio.insertar(registro)
                        check(idGuardado > 0) {
                            "La base de datos no devolvió un identificador válido"
                        }
                        registroActual = registro.copy(idRegistro = idGuardado.toInt())
                        mensajeResultado = "Registro guardado en el dispositivo"
                    }
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
        const val INTENSIDAD_MINIMA = Escala.MINIMO
        const val INTENSIDAD_MAXIMA = Escala.MAXIMO
        const val INTENSIDAD_INICIAL = Escala.MINIMO
    }
}
