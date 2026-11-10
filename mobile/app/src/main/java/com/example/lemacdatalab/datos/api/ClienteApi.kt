package com.example.lemacdatalab.datos.api

import com.example.lemacdatalab.model.RegistroActividad
import org.json.JSONObject

data class SesionApi(val token: String, val objetivo: String?)

class ClienteApi(private val servidor: ServidorSimulado) {

    suspend fun login(alias: String, contrasena: String): SesionApi? {
        val respuesta = servidor.atender(
            "POST",
            "/api/v1/auth/login",
            JSONObject().put("alias", alias).put("contrasena", contrasena)
        )
        if (!respuesta.exitosa) return null
        val usuario = respuesta.cuerpo.getJSONObject("usuario")
        return SesionApi(
            token = respuesta.cuerpo.getString("token"),
            objetivo = if (usuario.isNull("objetivo")) null else usuario.getString("objetivo")
        )
    }

    suspend fun aceptarConsentimiento(token: String, version: String, fecha: Long): Boolean =
        servidor.atender(
            "POST",
            "/api/v1/consentimiento",
            JSONObject().put("version", version).put("fechaAceptacion", fecha),
            token
        ).exitosa

    suspend fun subir(token: String, registro: RegistroActividad): RespuestaApi {
        val uuid = registro.uuid ?: error("Registro sin uuid")
        return if (registro.eliminado) {
            servidor.atender(
                "DELETE",
                "/api/v1/registros/$uuid?fechaModificacion=${registro.fechaModificacion}",
                token = token
            ).let { if (it.codigo == 404) subirCompleto(token, registro) else it }
        } else {
            subirCompleto(token, registro)
        }
    }

    private suspend fun subirCompleto(token: String, registro: RegistroActividad): RespuestaApi =
        servidor.atender("PUT", "/api/v1/registros/${registro.uuid}", registro.aJson(), token)

    suspend fun cambiosDesde(token: String, desde: Long): RespuestaApi =
        servidor.atender("GET", "/api/v1/registros?desde=$desde", token = token)
}
