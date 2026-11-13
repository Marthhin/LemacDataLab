package com.example.lemacdatalab.datos.remoto

import retrofit2.http.GET

interface RecursosApiService {

    @GET("recursos.json")
    suspend fun obtenerRecursos(): List<RecursoDto>
}
