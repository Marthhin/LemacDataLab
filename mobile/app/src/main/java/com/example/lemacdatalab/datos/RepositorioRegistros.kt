package com.example.lemacdatalab.datos

import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.VersionRegistro
import kotlinx.coroutines.flow.Flow

class RepositorioRegistros(
    private val registroDao: RegistroDao
) {

    fun observarRegistros(idUsuario: Int): Flow<List<RegistroActividad>> {
        return registroDao.observarRegistros(idUsuario)
    }

    fun observarPorId(id: Int): Flow<RegistroActividad?> {
        return registroDao.observarPorId(id)
    }

    fun observarVersiones(idRegistro: Int): Flow<List<VersionRegistro>> {
        return registroDao.observarVersiones(idRegistro)
    }

    suspend fun obtenerPorId(id: Int): RegistroActividad? {
        return registroDao.obtenerPorId(id)
    }

    suspend fun buscarDelDia(idUsuario: Int, fecha: String, idAmbito: Int): RegistroActividad? {
        return registroDao.buscarDelDia(idUsuario, fecha, idAmbito)
    }

    suspend fun tieneRegistroEnFecha(idUsuario: Int, fecha: String): Boolean {
        return registroDao.contarRegistrosDelDia(idUsuario, fecha) > 0
    }

    suspend fun insertar(registro: RegistroActividad): Long {
        return registroDao.insertar(registro.copy(fechaModificacion = System.currentTimeMillis()))
    }

    suspend fun actualizar(nuevo: RegistroActividad, anterior: RegistroActividad): Int {
        return registroDao.actualizarConVersion(nuevo, anterior, System.currentTimeMillis())
    }

    suspend fun eliminar(registro: RegistroActividad): Int {
        return registroDao.marcarEliminado(registro.idRegistro, true, System.currentTimeMillis())
    }

    suspend fun restaurar(registro: RegistroActividad): Int {
        return registroDao.marcarEliminado(registro.idRegistro, false, System.currentTimeMillis())
    }
}
