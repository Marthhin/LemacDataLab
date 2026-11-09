package com.example.lemacdatalab.datos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.withTransaction
import com.example.lemacdatalab.model.Usuario

sealed class ResultadoAcceso {
    data class Exito(val usuario: Usuario) : ResultadoAcceso()
    data object AliasEnUso : ResultadoAcceso()
    data object CredencialesInvalidas : ResultadoAcceso()
}

sealed class ResultadoCuenta {
    data object Exito : ResultadoCuenta()
    data object ContrasenaIncorrecta : ResultadoCuenta()
    data object UsuarioNoEncontrado : ResultadoCuenta()
}

class RepositorioUsuarios(
    private val baseDatos: BaseDatosRoom
) {

    private val usuarioDao = baseDatos.usuarioDao()
    private val registroDao = baseDatos.registroDao()

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

    suspend fun cambiarContrasena(
        idUsuario: Int,
        actual: String,
        nueva: String
    ): ResultadoCuenta {
        val usuario = usuarioDao.buscarPorId(idUsuario)
            ?: return ResultadoCuenta.UsuarioNoEncontrado

        if (!Seguridad.coincide(actual, usuario.sal, usuario.contrasenaHash)) {
            return ResultadoCuenta.ContrasenaIncorrecta
        }

        val sal = Seguridad.generarSal()
        usuarioDao.actualizar(
            usuario.copy(contrasenaHash = Seguridad.calcularHash(nueva, sal), sal = sal)
        )
        return ResultadoCuenta.Exito
    }

    suspend fun eliminarCuenta(idUsuario: Int, contrasena: String): ResultadoCuenta {
        val usuario = usuarioDao.buscarPorId(idUsuario)
            ?: return ResultadoCuenta.UsuarioNoEncontrado

        if (!Seguridad.coincide(contrasena, usuario.sal, usuario.contrasenaHash)) {
            return ResultadoCuenta.ContrasenaIncorrecta
        }

        baseDatos.withTransaction {
            registroDao.eliminarDeUsuario(idUsuario)
            usuarioDao.eliminar(usuario)
        }
        return ResultadoCuenta.Exito
    }
}
