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

    private val preferencias =
        context.getSharedPreferences("sesion_lemac", Context.MODE_PRIVATE)

    private val _actual = MutableStateFlow(leerGuardada())

    val actual: StateFlow<DatosSesion?> = _actual.asStateFlow()

    val haySesion: Boolean
        get() = _actual.value != null

    fun iniciar(idUsuario: Int, alias: String) {
        preferencias.edit()
            .putInt(CLAVE_ID, idUsuario)
            .putString(CLAVE_ALIAS, alias)
            .apply()
        _actual.value = DatosSesion(idUsuario, alias)
    }

    fun cerrar() {
        preferencias.edit().clear().apply()
        _actual.value = null
    }

    private fun leerGuardada(): DatosSesion? {
        val id = preferencias.getInt(CLAVE_ID, SIN_SESION)
        if (id == SIN_SESION) return null
        return DatosSesion(id, preferencias.getString(CLAVE_ALIAS, "").orEmpty())
    }

    private companion object {
        const val CLAVE_ID = "id_usuario"
        const val CLAVE_ALIAS = "alias"
        const val SIN_SESION = -1
    }
}