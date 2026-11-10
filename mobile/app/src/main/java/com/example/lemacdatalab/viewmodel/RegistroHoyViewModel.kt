package com.example.lemacdatalab.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lemacdatalab.datos.RepositorioRegistros
import com.example.lemacdatalab.datos.SesionUsuario
import com.example.lemacdatalab.model.AlertaCritica
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.CamposAmbito
import com.example.lemacdatalab.model.Escala
import com.example.lemacdatalab.model.OPCION_OTRO
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.alertaCriticaDe
import com.example.lemacdatalab.model.separarSeleccion
import com.example.lemacdatalab.model.unirSeleccion
import com.example.lemacdatalab.util.fechaDeHoy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class BorradorHoy(
    val intensidad: Int = Escala.MINIMO,
    val elevacion: Int = Escala.MINIMO,
    val horasSueno: String = "",
    val altoRiesgo: Boolean = false,
    val opcion: String = "",
    val otro: String = "",
    val resultado: String = "",
    val nota: String = ""
)

enum class EstadoPaso { PENDIENTE, GUARDADO, OMITIDO }

class RegistroHoyViewModel(
    private val repositorio: RepositorioRegistros,
    private val sesion: SesionUsuario
) : ViewModel() {

    val ambitos = Ambito.entries.toList()

    var paso by mutableStateOf(0)
        private set

    val borradores = mutableStateListOf<BorradorHoy>().apply { repeat(ambitos.size) { add(BorradorHoy()) } }

    val estados = mutableStateListOf<EstadoPaso>().apply { repeat(ambitos.size) { add(EstadoPaso.PENDIENTE) } }

    val yaRegistrados = mutableStateListOf<Boolean>().apply { repeat(ambitos.size) { add(false) } }

    var alertaPendiente by mutableStateOf<AlertaCritica?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var guardando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private var fecha = fechaDeHoy()
    private val existentes = arrayOfNulls<RegistroActividad>(ambitos.size)

    val terminado: Boolean
        get() = paso >= ambitos.size

    val ambitoActual: Ambito?
        get() = ambitos.getOrNull(paso)

    val borradorActual: BorradorHoy
        get() = borradores[paso.coerceAtMost(ambitos.lastIndex)]

    fun comenzar() {
        val idUsuario = sesion.actual.value?.idUsuario ?: return

        paso = 0
        fecha = fechaDeHoy()
        alertaPendiente = null
        error = null
        cargando = true
        ambitos.indices.forEach {
            borradores[it] = BorradorHoy()
            estados[it] = EstadoPaso.PENDIENTE
            yaRegistrados[it] = false
            existentes[it] = null
        }

        viewModelScope.launch {
            try {
                ambitos.forEachIndexed { indice, ambito ->
                    val existente = repositorio.buscarDelDia(idUsuario, fecha, ambito.id) ?: return@forEachIndexed
                    existentes[indice] = existente
                    yaRegistrados[indice] = true
                    borradores[indice] = borradorDesde(existente)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("RegistroHoyViewModel", "Error al cargar los registros de hoy", e)
            } finally {
                cargando = false
            }
        }
    }

    private fun catalogoDe(ambito: Ambito): List<String> = when (ambito) {
        Ambito.DBT -> CamposAmbito.HABILIDADES_DBT
        Ambito.ADICCIONES -> CamposAmbito.DESENCADENANTES
        Ambito.ANIMO -> CamposAmbito.EMOCIONES
        Ambito.NEURODESARROLLO -> emptyList()
    }

    private fun borradorDesde(registro: RegistroActividad): BorradorHoy {
        val ambito = Ambito.desdeId(registro.idAmbito) ?: return BorradorHoy()
        val guardado = when (ambito) {
            Ambito.DBT -> registro.habilidad
            Ambito.ADICCIONES -> registro.desencadenante
            Ambito.ANIMO -> registro.emocion
            Ambito.NEURODESARROLLO -> registro.estrategia
        }
        val seleccion = if (ambito == Ambito.NEURODESARROLLO) {
            null
        } else {
            separarSeleccion(guardado, catalogoDe(ambito))
        }

        return BorradorHoy(
            intensidad = registro.intensidad.coerceIn(Escala.MINIMO, Escala.MAXIMO),
            elevacion = (registro.elevacion ?: Escala.MINIMO).coerceIn(Escala.MINIMO, Escala.MAXIMO),
            horasSueno = registro.horasSueno?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }.orEmpty(),
            altoRiesgo = registro.altoRiesgo,
            opcion = seleccion?.opcion.orEmpty(),
            otro = seleccion?.otro ?: guardado.orEmpty(),
            resultado = registro.resultado.orEmpty(),
            nota = registro.nota.orEmpty()
        )
    }

    fun actualizar(cambio: (BorradorHoy) -> BorradorHoy) {
        if (terminado) return
        borradores[paso] = cambio(borradores[paso])
        error = null
    }

    fun anterior() {
        if (paso > 0 && !guardando) paso--
    }

    fun omitir() {
        if (terminado || guardando) return
        if (estados[paso] == EstadoPaso.PENDIENTE) estados[paso] = EstadoPaso.OMITIDO
        paso++
    }

    fun descartarAlerta() {
        alertaPendiente = null
    }

    private fun validar(ambito: Ambito, borrador: BorradorHoy): String? = when {
        ambito == Ambito.ANIMO && borrador.horasSueno.isNotBlank() &&
            borrador.horasSueno.toDoubleOrNull()?.takeIf { it in 0.0..24.0 } == null ->
            "Las horas de sueño deben estar entre 0 y 24"
        borrador.opcion == OPCION_OTRO && borrador.otro.isBlank() -> "Especifica la opción «Otro»"
        else -> null
    }

    private fun construir(ambito: Ambito, borrador: BorradorHoy, idUsuario: Int, existente: RegistroActividad?): RegistroActividad {
        val seleccion = unirSeleccion(borrador.opcion, borrador.otro)
        return RegistroActividad(
            idRegistro = existente?.idRegistro ?: 0,
            idUsuario = idUsuario,
            idAmbito = ambito.id,
            fechaHora = fecha,
            tipoActividad = existente?.tipoActividad ?: TIPO_REGISTRO_HOY,
            intensidad = borrador.intensidad,
            contexto = existente?.contexto ?: CONTEXTO_REGISTRO_HOY,
            nota = borrador.nota.trim().ifEmpty { null },
            estado = existente?.estado ?: "Activo",
            emocion = seleccion.takeIf { ambito == Ambito.ANIMO },
            habilidad = seleccion.takeIf { ambito == Ambito.DBT },
            estrategia = borrador.otro.trim().ifEmpty { null }.takeIf { ambito == Ambito.NEURODESARROLLO },
            desencadenante = seleccion.takeIf { ambito == Ambito.ADICCIONES },
            accionProtectora = existente?.accionProtectora.takeIf { ambito == Ambito.ADICCIONES },
            resultado = borrador.resultado.ifEmpty { null }.takeIf { ambito != Ambito.ANIMO },
            elevacion = borrador.elevacion.takeIf { ambito == Ambito.ANIMO },
            horasSueno = borrador.horasSueno.toDoubleOrNull().takeIf { ambito == Ambito.ANIMO },
            altoRiesgo = borrador.altoRiesgo && ambito == Ambito.ANIMO,
            editado = existente?.editado ?: false
        )
    }

    fun guardarPaso() {
        if (terminado || guardando) return
        val ambito = ambitos[paso]
        val borrador = borradores[paso]
        val idUsuario = sesion.actual.value?.idUsuario

        if (idUsuario == null) {
            error = "Tu sesión terminó. Vuelve a ingresar."
            return
        }
        validar(ambito, borrador)?.let {
            error = it
            return
        }

        guardando = true
        val indice = paso

        viewModelScope.launch {
            try {
                val existente = existentes[indice] ?: repositorio.buscarDelDia(idUsuario, fecha, ambito.id)
                val registro = construir(ambito, borrador, idUsuario, existente)

                val sinCambios = existente != null && registro.copy(
                    editado = existente.editado,
                    fechaModificacion = existente.fechaModificacion
                ) == existente

                if (sinCambios) {
                    Unit
                } else if (existente != null) {
                    repositorio.actualizar(registro, existente)
                    existentes[indice] = registro.copy(editado = true)
                } else {
                    val id = repositorio.insertar(registro)
                    existentes[indice] = registro.copy(idRegistro = id.toInt())
                }

                estados[indice] = EstadoPaso.GUARDADO
                alertaPendiente = alertaCriticaDe(registro)
                paso = indice + 1
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("RegistroHoyViewModel", "Error al guardar el paso", e)
                error = "No se pudo guardar. Inténtalo de nuevo."
            } finally {
                guardando = false
            }
        }
    }

    companion object {
        const val TIPO_REGISTRO_HOY = "Registro de hoy"
        const val CONTEXTO_REGISTRO_HOY = "Registro diario"
    }
}
