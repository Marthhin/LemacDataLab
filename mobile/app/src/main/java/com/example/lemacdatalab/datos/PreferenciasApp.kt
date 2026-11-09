package com.example.lemacdatalab.datos

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenciasApp(context: Context) {

    private val preferencias =
        context.getSharedPreferences("preferencias_lemac", Context.MODE_PRIVATE)

    private val _modoOscuro = MutableStateFlow(leerModoOscuro())

    val modoOscuro: StateFlow<Boolean?> = _modoOscuro.asStateFlow()

    fun cambiarModoOscuro(activo: Boolean) {
        preferencias.edit().putBoolean(CLAVE_MODO_OSCURO, activo).apply()
        _modoOscuro.value = activo
    }

    private fun leerModoOscuro(): Boolean? {
        if (!preferencias.contains(CLAVE_MODO_OSCURO)) return null
        return preferencias.getBoolean(CLAVE_MODO_OSCURO, false)
    }

    private companion object {
        const val CLAVE_MODO_OSCURO = "modo_oscuro"
    }
}