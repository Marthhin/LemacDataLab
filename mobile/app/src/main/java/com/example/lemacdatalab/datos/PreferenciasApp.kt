package com.example.lemacdatalab.datos

import android.content.Context
import com.example.lemacdatalab.model.ModoTema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenciasApp(context: Context) {

    private val preferencias =
        context.getSharedPreferences("preferencias_lemac", Context.MODE_PRIVATE)

    private val _modoTema = MutableStateFlow(leerModoTema())

    val modoTema: StateFlow<ModoTema> = _modoTema.asStateFlow()

    fun cambiarModoTema(modo: ModoTema) {
        preferencias.edit().putString(CLAVE_MODO_TEMA, modo.name).apply()
        _modoTema.value = modo
    }

    private fun leerModoTema(): ModoTema {
        val guardado = preferencias.getString(CLAVE_MODO_TEMA, null)
        return ModoTema.entries.find { it.name == guardado } ?: ModoTema.SISTEMA
    }

    private companion object {
        const val CLAVE_MODO_TEMA = "modo_tema"
    }
}