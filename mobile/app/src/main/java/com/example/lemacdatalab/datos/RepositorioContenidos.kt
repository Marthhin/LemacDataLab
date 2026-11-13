package com.example.lemacdatalab.datos

import com.example.lemacdatalab.datos.remoto.RecursosApiService
import com.example.lemacdatalab.datos.remoto.aContenido
import com.example.lemacdatalab.model.Contenido

class RepositorioContenidos(
    private val api: RecursosApiService
) {

    suspend fun obtenerTodos(): List<Contenido> =
        api.obtenerRecursos().mapNotNull { it.aContenido() }
}
