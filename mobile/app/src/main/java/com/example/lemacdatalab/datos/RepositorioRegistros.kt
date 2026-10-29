package com.example.lemacdatalab.datos

import com.example.lemacdatalab.model.RegistroActividad
import kotlinx.coroutines.flow.Flow

class RepositorioRegistros(
    private val registroDao: RegistroDao
) {

    fun observarRegistros(): Flow<List<RegistroActividad>> {
        return registroDao.observarRegistros()
    }

    suspend fun obtenerPorId(id: Int): RegistroActividad? {
        return registroDao.obtenerPorId(id)
    }

    suspend fun insertar(registro: RegistroActividad): Long {
        return registroDao.insertar(registro)
    }

    suspend fun actualizar(registro: RegistroActividad): Int {
        return registroDao.actualizar(registro)
    }

    suspend fun eliminar(registro: RegistroActividad): Int {
        return registroDao.eliminar(registro)
    }
}