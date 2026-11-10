package com.example.lemacdatalab.datos

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DatosSesion(
    val idUsuario: Int,
    val alias: String
)

class SesionUsuario(context: Context) {

    private val almacen = AlmacenSeguro(context)

    @Volatile
    private var ultimaActividad = almacen.leer(CLAVE_ACTIVIDAD)?.toLongOrNull() ?: 0L

    private val _actual = MutableStateFlow(leerGuardada())
    val actual: StateFlow<DatosSesion?> = _actual.asStateFlow()

    init {
        context.getSharedPreferences("sesion_lemac", Context.MODE_PRIVATE).edit().clear().apply()
    }

    val haySesion: Boolean
        get() = _actual.value != null

    val token: String?
        get() = if (haySesion) almacen.leer(CLAVE_TOKEN) else null

    fun iniciar(idUsuario: Int, alias: String, token: String?) {
        ultimaActividad = System.currentTimeMillis()
        almacen.guardar(CLAVE_ID, idUsuario.toString())
        almacen.guardar(CLAVE_ALIAS, alias)
        almacen.guardar(CLAVE_ACTIVIDAD, ultimaActividad.toString())
        if (token != null) almacen.guardar(CLAVE_TOKEN, token) else almacen.borrar(CLAVE_TOKEN)
        _actual.value = DatosSesion(idUsuario, alias)
    }

    fun guardarToken(token: String) {
        if (haySesion) almacen.guardar(CLAVE_TOKEN, token)
    }

    fun cerrar() {
        almacen.borrar(CLAVE_ID, CLAVE_ALIAS, CLAVE_TOKEN, CLAVE_ACTIVIDAD)
        _actual.value = null
    }

    fun registrarActividad() {
        ultimaActividad = System.currentTimeMillis()
    }

    fun guardarActividad() {
        if (haySesion) almacen.guardar(CLAVE_ACTIVIDAD, ultimaActividad.toString())
    }

    fun expirada(ahora: Long = System.currentTimeMillis()): Boolean =
        haySesion && ahora - ultimaActividad >= TIEMPO_INACTIVIDAD_MS

    private fun leerGuardada(): DatosSesion? {
        val id = almacen.leer(CLAVE_ID)?.toIntOrNull() ?: return null
        if (System.currentTimeMillis() - ultimaActividad >= TIEMPO_INACTIVIDAD_MS) {
            almacen.borrar(CLAVE_ID, CLAVE_ALIAS, CLAVE_TOKEN, CLAVE_ACTIVIDAD)
            return null
        }
        return DatosSesion(id, almacen.leer(CLAVE_ALIAS).orEmpty())
    }

    companion object {
        const val TIEMPO_INACTIVIDAD_MS = 5 * 60 * 1000L
        private const val CLAVE_ID = "sesion_id"
        private const val CLAVE_ALIAS = "sesion_alias"
        private const val CLAVE_TOKEN = "sesion_token"
        private const val CLAVE_ACTIVIDAD = "sesion_actividad"
    }
}
