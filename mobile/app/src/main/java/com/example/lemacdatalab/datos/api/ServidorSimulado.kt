package com.example.lemacdatalab.datos.api

import android.content.Context
import android.util.Log
import com.example.lemacdatalab.datos.RepositorioUsuarios
import com.example.lemacdatalab.datos.ResultadoAcceso
import com.example.lemacdatalab.model.TextoConsentimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class RespuestaApi(val codigo: Int, val cuerpo: JSONObject) {
    val exitosa: Boolean
        get() = codigo in 200..299
}

class ServidorSimulado(
    context: Context,
    private val repositorioUsuarios: RepositorioUsuarios
) {

    private val archivo = File(context.filesDir, "servidor_simulado.json")
    private val candado = Mutex()

    suspend fun atender(
        metodo: String,
        ruta: String,
        cuerpo: JSONObject? = null,
        token: String? = null
    ): RespuestaApi = withContext(Dispatchers.IO) {
        delay(LATENCIA_MS)
        val respuesta = try {
            candado.withLock { enrutar(metodo, ruta, cuerpo, token) }
        } catch (e: Exception) {
            Log.e(ETIQUETA, "Error interno", e)
            error(500, "Error interno del servidor")
        }
        Log.d(ETIQUETA, "$metodo $ruta -> ${respuesta.codigo} ${respuesta.cuerpo}")
        respuesta
    }

    private suspend fun enrutar(metodo: String, ruta: String, cuerpo: JSONObject?, token: String?): RespuestaApi {
        val (camino, consulta) = ruta.split("?", limit = 2).let { it[0] to it.getOrNull(1) }
        val segmentos = camino.removePrefix(PREFIJO).trim('/').split("/")

        if (metodo == "POST" && segmentos == listOf("auth", "login")) return login(cuerpo)

        val datos = leer()
        val idUsuario = datos.getJSONObject("tokens").optInt(token.orEmpty(), -1)
        if (idUsuario == -1) return error(401, "Token inválido o ausente")

        return when {
            segmentos == listOf("consentimiento") && metodo == "GET" -> consultarConsentimiento(datos, idUsuario)
            segmentos == listOf("consentimiento") && metodo == "POST" -> aceptarConsentimiento(datos, idUsuario, cuerpo)
            segmentos == listOf("registros") && metodo == "GET" -> cambiosDesde(datos, idUsuario, consulta)
            segmentos == listOf("registros") && metodo == "POST" -> guardarRegistro(datos, idUsuario, cuerpo, creando = true)
            segmentos.size == 2 && segmentos[0] == "registros" && metodo == "PUT" ->
                guardarRegistro(datos, idUsuario, cuerpo?.put("uuid", segmentos[1]), creando = false)
            segmentos.size == 2 && segmentos[0] == "registros" && metodo == "DELETE" ->
                eliminarRegistro(datos, idUsuario, segmentos[1], consulta)
            else -> error(404, "Ruta no encontrada")
        }
    }

    private suspend fun login(cuerpo: JSONObject?): RespuestaApi {
        val alias = cuerpo?.optString("alias").orEmpty()
        val contrasena = cuerpo?.optString("contrasena").orEmpty()
        val resultado = repositorioUsuarios.iniciarSesion(alias, contrasena)
        if (resultado !is ResultadoAcceso.Exito) return error(401, "Credenciales inválidas")

        val usuario = resultado.usuario
        val token = "tok_" + UUID.randomUUID().toString().replace("-", "")
        val datos = leer()
        datos.getJSONObject("tokens").put(token, usuario.idUsuario)
        escribir(datos)

        return RespuestaApi(
            200,
            JSONObject()
                .put("token", token)
                .put("expiraEnSegundos", 300)
                .put(
                    "usuario",
                    JSONObject()
                        .put("id", usuario.idUsuario)
                        .put("alias", usuario.alias)
                        .put("nombre", usuario.nombre ?: JSONObject.NULL)
                        .put("objetivo", usuario.objetivo ?: JSONObject.NULL)
                )
        )
    }

    private fun consultarConsentimiento(datos: JSONObject, idUsuario: Int): RespuestaApi {
        val aceptado = datos.getJSONObject("consentimientos").optJSONObject(idUsuario.toString())
        return RespuestaApi(
            200,
            JSONObject()
                .put("versionVigente", TextoConsentimiento.VERSION)
                .put("aceptado", aceptado ?: JSONObject.NULL)
        )
    }

    private fun aceptarConsentimiento(datos: JSONObject, idUsuario: Int, cuerpo: JSONObject?): RespuestaApi {
        val version = cuerpo?.optString("version").orEmpty()
        if (version != TextoConsentimiento.VERSION) return error(422, "Versión de consentimiento no vigente")

        val aceptado = JSONObject()
            .put("version", version)
            .put("fechaAceptacion", cuerpo?.optLong("fechaAceptacion")?.takeIf { it > 0 } ?: System.currentTimeMillis())
        datos.getJSONObject("consentimientos").put(idUsuario.toString(), aceptado)
        escribir(datos)
        return RespuestaApi(201, aceptado)
    }

    private fun cambiosDesde(datos: JSONObject, idUsuario: Int, consulta: String?): RespuestaApi {
        val desde = parametro(consulta, "desde")?.toLongOrNull() ?: 0L
        val registros = datos.getJSONArray("registros")
        val cambios = JSONArray()
        for (i in 0 until registros.length()) {
            val registro = registros.getJSONObject(i)
            if (registro.getInt("idUsuario") == idUsuario && registro.getLong("fechaModificacion") > desde) {
                cambios.put(registro)
            }
        }
        return RespuestaApi(
            200,
            JSONObject().put("registros", cambios).put("fechaServidor", System.currentTimeMillis())
        )
    }

    private fun guardarRegistro(datos: JSONObject, idUsuario: Int, cuerpo: JSONObject?, creando: Boolean): RespuestaApi {
        if (cuerpo == null || !cuerpo.has("uuid") || !cuerpo.has("fechaModificacion")) {
            return error(400, "Faltan uuid o fechaModificacion")
        }
        val uuid = cuerpo.getString("uuid")
        val registros = datos.getJSONArray("registros")
        val indice = indiceDe(registros, uuid)

        if (indice >= 0) {
            val guardado = registros.getJSONObject(indice)
            if (guardado.getInt("idUsuario") != idUsuario) return error(403, "El registro pertenece a otro usuario")
            if (guardado.getLong("fechaModificacion") > cuerpo.getLong("fechaModificacion")) {
                return RespuestaApi(
                    409,
                    JSONObject().put("error", "Existe una versión más reciente").put("registro", guardado)
                )
            }
            registros.put(indice, cuerpo.put("idUsuario", idUsuario))
        } else {
            registros.put(cuerpo.put("idUsuario", idUsuario))
        }

        escribir(datos)
        return RespuestaApi(if (creando && indice < 0) 201 else 200, cuerpo)
    }

    private fun eliminarRegistro(datos: JSONObject, idUsuario: Int, uuid: String, consulta: String?): RespuestaApi {
        val registros = datos.getJSONArray("registros")
        val indice = indiceDe(registros, uuid)
        if (indice < 0) return error(404, "Registro no encontrado")

        val guardado = registros.getJSONObject(indice)
        if (guardado.getInt("idUsuario") != idUsuario) return error(403, "El registro pertenece a otro usuario")

        val fecha = parametro(consulta, "fechaModificacion")?.toLongOrNull() ?: System.currentTimeMillis()
        if (guardado.getLong("fechaModificacion") > fecha) {
            return RespuestaApi(409, JSONObject().put("error", "Existe una versión más reciente").put("registro", guardado))
        }

        guardado.put("eliminado", true).put("fechaModificacion", fecha)
        escribir(datos)
        return RespuestaApi(200, guardado)
    }

    private fun indiceDe(registros: JSONArray, uuid: String): Int {
        for (i in 0 until registros.length()) {
            if (registros.getJSONObject(i).optString("uuid") == uuid) return i
        }
        return -1
    }

    private fun parametro(consulta: String?, nombre: String): String? =
        consulta?.split("&")
            ?.map { it.split("=", limit = 2) }
            ?.firstOrNull { it[0] == nombre }
            ?.getOrNull(1)

    private fun error(codigo: Int, mensaje: String) = RespuestaApi(codigo, JSONObject().put("error", mensaje))

    private fun leer(): JSONObject {
        val json = if (archivo.exists()) JSONObject(archivo.readText()) else JSONObject()
        if (!json.has("tokens")) json.put("tokens", JSONObject())
        if (!json.has("consentimientos")) json.put("consentimientos", JSONObject())
        if (!json.has("registros")) json.put("registros", JSONArray())
        return json
    }

    private fun escribir(datos: JSONObject) {
        archivo.writeText(datos.toString())
    }

    private companion object {
        const val PREFIJO = "/api/v1"
        const val ETIQUETA = "ApiSimulada"
        const val LATENCIA_MS = 250L
    }
}
