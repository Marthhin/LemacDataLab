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

    @Query(
        """
        SELECT * FROM registros
        WHERE id_usuario = :idUsuario
        ORDER BY substr(fecha_hora, 7, 4) || substr(fecha_hora, 4, 2) || substr(fecha_hora, 1, 2) DESC,
                 id_registro DESC
        """
    )
    fun observarRegistros(idUsuario: Int): Flow<List<RegistroActividad>>

    @Query("SELECT * FROM registros WHERE id_registro = :id")
    suspend fun obtenerPorId(id: Int): RegistroActividad?

    @Query("SELECT * FROM registros WHERE id_registro = :id")
    fun observarPorId(id: Int): Flow<RegistroActividad?>

    @Query("SELECT COUNT(*) FROM registros WHERE id_usuario = :idUsuario AND fecha_hora = :fecha")
    suspend fun contarRegistrosDelDia(idUsuario: Int, fecha: String): Int

    @Query("DELETE FROM registros WHERE id_usuario = :idUsuario")
    suspend fun eliminarDeUsuario(idUsuario: Int): Int

    @Update
    suspend fun actualizar(registro: RegistroActividad): Int

    @Delete
    suspend fun eliminar(registro: RegistroActividad): Int
}