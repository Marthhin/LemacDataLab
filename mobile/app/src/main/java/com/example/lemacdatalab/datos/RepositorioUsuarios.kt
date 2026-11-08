package com.example.lemacdatalab.datos

import android.database.sqlite.SQLiteConstraintException
import com.example.lemacdatalab.model.Usuario

sealed class ResultadoAcceso {
    data class Exito(val usuario: Usuario) : ResultadoAcceso()
    data object AliasEnUso : ResultadoAcceso()
    data object CredencialesInvalidas : ResultadoAcceso()
}

class RepositorioUsuarios(
    private val usuarioDao: UsuarioDao
) {

    suspend fun crearCuenta(alias: String, contrasena: String): ResultadoAcceso {
        if (usuarioDao.buscarPorAlias(alias) != null) {
            return ResultadoAcceso.AliasEnUso
        }

        val sal = Seguridad.generarSal()
        val nuevo = Usuario(
            alias = alias,
            contrasenaHash = Seguridad.calcularHash(contrasena, sal),
            sal = sal
        )

        return try {
            val id = usuarioDao.insertar(nuevo)
            ResultadoAcceso.Exito(nuevo.copy(idUsuario = id.toInt()))
        } catch (error: SQLiteConstraintException) {
            ResultadoAcceso.AliasEnUso
        }
    }

    suspend fun iniciarSesion(alias: String, contrasena: String): ResultadoAcceso {
        val usuario = usuarioDao.buscarPorAlias(alias)
            ?: return ResultadoAcceso.CredencialesInvalidas

        return if (Seguridad.coincide(contrasena, usuario.sal, usuario.contrasenaHash)) {
            ResultadoAcceso.Exito(usuario)
        } else {
            ResultadoAcceso.CredencialesInvalidas
        }
    }
}