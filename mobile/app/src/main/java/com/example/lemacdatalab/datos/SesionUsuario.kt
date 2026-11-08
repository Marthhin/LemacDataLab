package com.example.lemacdatalab.datos

import android.content.Context

class SesionUsuario(context: Context) {

    private val preferencias =
        context.getSharedPreferences("sesion_lemac", Context.MODE_PRIVATE)

    val idUsuario: Int
        get() = preferencias.getInt(CLAVE_ID, SIN_SESION)

    val alias: String
        get() = preferencias.getString(CLAVE_ALIAS, "").orEmpty()

    val haySesion: Boolean
        get() = idUsuario != SIN_SESION

    fun iniciar(idUsuario: Int, alias: String) {
        preferencias.edit()
            .putInt(CLAVE_ID, idUsuario)
            .putString(CLAVE_ALIAS, alias)
            .apply()
    }

    fun cerrar() {
        preferencias.edit().clear().apply()
    }

    private companion object {
        const val CLAVE_ID = "id_usuario"
        const val CLAVE_ALIAS = "alias"
        const val SIN_SESION = -1
    }
}