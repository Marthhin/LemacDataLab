package com.example.lemacdatalab.datos.api

import android.content.Context
import android.util.Log
import com.example.lemacdatalab.datos.RegistroDao
import com.example.lemacdatalab.datos.SesionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EstadoSincronizacion(
    val sincronizando: Boolean = false,
    val ultima: Long? = null,
    val error: String? = null
)

class Sincronizador(
    context: Context,
    private val registroDao: RegistroDao,
    private val cliente: ClienteApi,
    private val sesion: SesionUsuario
) {

    private val preferencias =
        context.getSharedPreferences("sincronizacion_lemac", Context.MODE_PRIVATE)

    private val _estado = MutableStateFlow(EstadoSincronizacion())
    val estado: StateFlow<EstadoSincronizacion> = _estado.asStateFlow()

    fun cargarEstado(idUsuario: Int) {
        _estado.value = EstadoSincronizacion(ultima = ultimaDe(idUsuario).takeIf { it > 0 })
    }

    private fun ultimaDe(idUsuario: Int) = preferencias.getLong("ultima_$idUsuario", 0L)

    suspend fun sincronizar() {
        val idUsuario = sesion.actual.value?.idUsuario ?: return
        val token = sesion.token
        if (token == null) {
            _estado.value = _estado.value.copy(error = "Vuelve a iniciar sesión para sincronizar")
            return
        }
        if (_estado.value.sincronizando) return

        _estado.value = _estado.value.copy(sincronizando = true, error = null)
        val desde = ultimaDe(idUsuario)

        try {
            registroDao.cambiosDesde(idUsuario, desde).forEach { local ->
                val respuesta = cliente.subir(token, local)
                if (respuesta.codigo == 409) {
                    val remoto = registroDesdeJson(respuesta.cuerpo.getJSONObject("registro"), idUsuario)
                    registroDao.aplicarVersionRemota(remoto, local)
                } else {
                    check(respuesta.exitosa) { "El servidor respondió ${respuesta.codigo}" }
                }
            }

            val respuesta = cliente.cambiosDesde(token, desde)
            check(respuesta.exitosa) { "El servidor respondió ${respuesta.codigo}" }
            val remotos = respuesta.cuerpo.getJSONArray("registros")
            for (i in 0 until remotos.length()) {
                val remoto = registroDesdeJson(remotos.getJSONObject(i), idUsuario)
                val local = registroDao.buscarPorUuid(remoto.uuid.orEmpty())
                when {
                    local == null -> registroDao.insertar(remoto)
                    remoto.fechaModificacion > local.fechaModificacion ->
                        registroDao.aplicarVersionRemota(remoto, local)
                }
            }

            val ahora = respuesta.cuerpo.optLong("fechaServidor", System.currentTimeMillis())
            preferencias.edit().putLong("ultima_$idUsuario", ahora).apply()
            _estado.value = EstadoSincronizacion(ultima = ahora)
        } catch (error: Exception) {
            Log.e("Sincronizador", "Error al sincronizar", error)
            _estado.value = _estado.value.copy(sincronizando = false, error = "No se pudo sincronizar")
        }
    }
}
