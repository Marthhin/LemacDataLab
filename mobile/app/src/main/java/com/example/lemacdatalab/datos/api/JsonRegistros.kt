package com.example.lemacdatalab.datos.api

import com.example.lemacdatalab.model.RegistroActividad
import org.json.JSONObject

fun RegistroActividad.aJson(): JSONObject = JSONObject().apply {
    put("uuid", uuid)
    put("idAmbito", idAmbito)
    put("fecha", fechaHora)
    put("tipoActividad", tipoActividad)
    put("intensidad", intensidad)
    put("elevacion", elevacion ?: JSONObject.NULL)
    put("horasSueno", horasSueno ?: JSONObject.NULL)
    put("altoRiesgo", altoRiesgo)
    put("contexto", contexto)
    put("nota", nota ?: JSONObject.NULL)
    put("emocion", emocion ?: JSONObject.NULL)
    put("habilidad", habilidad ?: JSONObject.NULL)
    put("estrategia", estrategia ?: JSONObject.NULL)
    put("desencadenante", desencadenante ?: JSONObject.NULL)
    put("accionProtectora", accionProtectora ?: JSONObject.NULL)
    put("resultado", resultado ?: JSONObject.NULL)
    put("editado", editado)
    put("eliminado", eliminado)
    put("fechaModificacion", fechaModificacion)
}

private fun JSONObject.textoONulo(nombre: String): String? =
    if (isNull(nombre)) null else optString(nombre)

fun registroDesdeJson(json: JSONObject, idUsuario: Int): RegistroActividad = RegistroActividad(
    idUsuario = idUsuario,
    idAmbito = json.getInt("idAmbito"),
    fechaHora = json.getString("fecha"),
    tipoActividad = json.getString("tipoActividad"),
    intensidad = json.getInt("intensidad"),
    contexto = json.optString("contexto"),
    nota = json.textoONulo("nota"),
    estado = "Activo",
    emocion = json.textoONulo("emocion"),
    habilidad = json.textoONulo("habilidad"),
    estrategia = json.textoONulo("estrategia"),
    desencadenante = json.textoONulo("desencadenante"),
    accionProtectora = json.textoONulo("accionProtectora"),
    resultado = json.textoONulo("resultado"),
    elevacion = if (json.isNull("elevacion")) null else json.getInt("elevacion"),
    horasSueno = if (json.isNull("horasSueno")) null else json.getDouble("horasSueno"),
    altoRiesgo = json.optBoolean("altoRiesgo"),
    editado = json.optBoolean("editado"),
    eliminado = json.optBoolean("eliminado"),
    fechaModificacion = json.getLong("fechaModificacion"),
    uuid = json.getString("uuid")
)
