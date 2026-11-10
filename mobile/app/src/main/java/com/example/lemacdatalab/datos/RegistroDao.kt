package com.example.lemacdatalab.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.VersionRegistro
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RegistroDao {

    @Insert
    abstract suspend fun insertar(registro: RegistroActividad): Long

    @Query(
        """
        SELECT * FROM registros
        WHERE id_usuario = :idUsuario AND eliminado = 0
        ORDER BY substr(fecha_hora, 7, 4) || substr(fecha_hora, 4, 2) || substr(fecha_hora, 1, 2) DESC,
                 id_registro DESC
        """
    )
    abstract fun observarRegistros(idUsuario: Int): Flow<List<RegistroActividad>>

    @Query("SELECT * FROM registros WHERE id_registro = :id AND eliminado = 0")
    abstract suspend fun obtenerPorId(id: Int): RegistroActividad?

    @Query("SELECT * FROM registros WHERE id_registro = :id AND eliminado = 0")
    abstract fun observarPorId(id: Int): Flow<RegistroActividad?>

    @Query(
        """
        SELECT * FROM registros
        WHERE id_usuario = :idUsuario AND fecha_hora = :fecha AND id_ambito = :idAmbito
              AND eliminado = 0
        LIMIT 1
        """
    )
    abstract suspend fun buscarDelDia(idUsuario: Int, fecha: String, idAmbito: Int): RegistroActividad?

    @Query(
        "SELECT COUNT(*) FROM registros WHERE id_usuario = :idUsuario AND fecha_hora = :fecha AND eliminado = 0"
    )
    abstract suspend fun contarRegistrosDelDia(idUsuario: Int, fecha: String): Int

    @Update
    abstract suspend fun actualizar(registro: RegistroActividad): Int

    @Query(
        "UPDATE registros SET eliminado = :eliminado, fecha_modificacion = :fecha WHERE id_registro = :id"
    )
    abstract suspend fun marcarEliminado(id: Int, eliminado: Boolean, fecha: Long): Int

    @Query(
        "SELECT * FROM registros WHERE id_usuario = :idUsuario AND fecha_modificacion > :desde ORDER BY fecha_modificacion"
    )
    abstract suspend fun cambiosDesde(idUsuario: Int, desde: Long): List<RegistroActividad>

    @Query("SELECT * FROM registros WHERE uuid = :uuid LIMIT 1")
    abstract suspend fun buscarPorUuid(uuid: String): RegistroActividad?

    @Transaction
    open suspend fun aplicarVersionRemota(remoto: RegistroActividad, local: RegistroActividad): Int {
        insertarVersion(VersionRegistro.desde(local, System.currentTimeMillis()))
        return actualizar(remoto.copy(idRegistro = local.idRegistro, idUsuario = local.idUsuario, editado = true))
    }

    @Insert
    abstract suspend fun insertarVersion(version: VersionRegistro): Long

    @Query("SELECT * FROM registro_versiones WHERE id_registro = :idRegistro ORDER BY fecha_guardado DESC")
    abstract fun observarVersiones(idRegistro: Int): Flow<List<VersionRegistro>>

    @Transaction
    open suspend fun actualizarConVersion(nuevo: RegistroActividad, anterior: RegistroActividad, ahora: Long): Int {
        insertarVersion(VersionRegistro.desde(anterior, ahora))
        return actualizar(
            nuevo.copy(
                editado = true,
                eliminado = false,
                fechaModificacion = ahora,
                uuid = nuevo.uuid ?: anterior.uuid
            )
        )
    }

    @Query(
        """
        DELETE FROM registro_versiones
        WHERE id_registro IN (SELECT id_registro FROM registros WHERE id_usuario = :idUsuario)
        """
    )
    abstract suspend fun eliminarVersionesDeUsuario(idUsuario: Int): Int

    @Query("DELETE FROM registros WHERE id_usuario = :idUsuario")
    abstract suspend fun eliminarRegistrosDeUsuario(idUsuario: Int): Int

    @Transaction
    open suspend fun eliminarDeUsuario(idUsuario: Int): Int {
        eliminarVersionesDeUsuario(idUsuario)
        return eliminarRegistrosDeUsuario(idUsuario)
    }
}
