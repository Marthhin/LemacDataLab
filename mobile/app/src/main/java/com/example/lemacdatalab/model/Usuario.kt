package com.example.lemacdatalab.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["alias"], unique = true)]
)
data class Usuario(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,

    @ColumnInfo(name = "alias")
    val alias: String,

    @ColumnInfo(name = "contrasena_hash")
    val contrasenaHash: String,

    @ColumnInfo(name = "sal")
    val sal: String,

    @ColumnInfo(name = "nombre")
    val nombre: String? = null,

    @ColumnInfo(name = "objetivo")
    val objetivo: String? = null
)