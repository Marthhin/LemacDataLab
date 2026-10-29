package com.example.lemacdatalab.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lemacdatalab.model.RegistroActividad
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Insert
    suspend fun insertar(registro: RegistroActividad): Long

    @Query("SELECT * FROM registros ORDER BY id_registro DESC")
    fun observarRegistros(): Flow<List<RegistroActividad>>

    @Query("SELECT * FROM registros WHERE id_registro = :id")
    suspend fun obtenerPorId(id: Int): RegistroActividad?

    @Update
    suspend fun actualizar(registro: RegistroActividad): Int

    @Delete
    suspend fun eliminar(registro: RegistroActividad): Int
}