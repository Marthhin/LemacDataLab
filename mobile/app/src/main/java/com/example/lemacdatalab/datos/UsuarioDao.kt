package com.example.lemacdatalab.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lemacdatalab.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE alias = :alias COLLATE NOCASE LIMIT 1")
    suspend fun buscarPorAlias(alias: String): Usuario?
}