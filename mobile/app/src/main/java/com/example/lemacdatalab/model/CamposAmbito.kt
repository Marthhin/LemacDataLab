package com.example.lemacdatalab.model

import java.util.Locale

const val OPCION_OTRO = "Otro (especificar)"
private const val PREFIJO_OTRO = "Otro: "

object CamposAmbito {
    val EMOCIONES = listOf("Alegría", "Calma", "Tristeza", "Ansiedad", "Enojo", "Cansancio")

    val HABILIDADES_DBT = listOf(
        "Mindfulness",
        "Tolerancia al malestar",
        "Regulación emocional",
        "Efectividad interpersonal",
        OPCION_OTRO
    )

    val DESENCADENANTES = listOf(
        "Estrés",
        "Discusión o conflicto",
        "Aburrimiento",
        "Soledad",
        "Lugar o personas asociadas",
        OPCION_OTRO
    )

    val ACCIONES_PROTECTORAS = listOf(
        "Hablar con alguien de confianza",
        "Salir a caminar",
        "Ejercicio de respiración",
        "Cambiar de lugar",
        "Otra actividad para distraerme",
        OPCION_OTRO
    )

    val RESULTADOS_ESTRATEGIA = listOf("Me ayudó", "Me ayudó en parte", "No me ayudó")

    val UTILIDAD_HABILIDAD = listOf("Útil", "Algo útil", "Poco útil")

    val OBJETIVO_CUMPLIDO = listOf("Cumplido", "Parcialmente cumplido", "No cumplido")

    const val HORAS_SUENO_CRITICAS = 3.0
}

data class SeleccionCatalogo(val opcion: String, val otro: String)

fun separarSeleccion(valor: String?, catalogo: List<String>): SeleccionCatalogo = when {
    valor.isNullOrBlank() -> SeleccionCatalogo("", "")
    valor in catalogo -> SeleccionCatalogo(valor, "")
    valor.startsWith(PREFIJO_OTRO) -> SeleccionCatalogo(OPCION_OTRO, valor.removePrefix(PREFIJO_OTRO))
    else -> SeleccionCatalogo(OPCION_OTRO, valor)
}

fun unirSeleccion(opcion: String, otro: String): String? = when {
    opcion.isBlank() -> null
    opcion == OPCION_OTRO -> otro.trim().takeIf { it.isNotEmpty() }?.let { PREFIJO_OTRO + it }
    else -> opcion
}

fun formatearHoras(horas: Double): String =
    if (horas % 1.0 == 0.0) "${horas.toInt()} h" else String.format(Locale.forLanguageTag("es-CL"), "%.1f h", horas)

data class TextosAmbito(
    val actividad: String,
    val ejemploActividad: String,
    val intensidad: String,
    val ayudaIntensidad: String
)

private val TEXTOS_GENERALES = TextosAmbito(
    actividad = "Actividad realizada",
    ejemploActividad = "Ej.: caminata de 20 minutos",
    intensidad = "Intensidad",
    ayudaIntensidad = "Elige un ámbito para ver más detalles."
)

fun textosDe(ambito: Ambito?): TextosAmbito = when (ambito) {
    Ambito.NEURODESARROLLO -> TextosAmbito(
        actividad = "Rutina o tarea",
        ejemploActividad = "Ej.: ordenar el escritorio",
        intensidad = "Nivel de sobrecarga",
        ayudaIntensidad = "¿Qué tan sobrecargado o sobrecargada te sentiste?"
    )
    Ambito.DBT -> TextosAmbito(
        actividad = "Situación",
        ejemploActividad = "Ej.: conversación difícil",
        intensidad = "Intensidad de la emoción",
        ayudaIntensidad = "¿Qué tan intensa fue la emoción?"
    )
    Ambito.ANIMO -> TextosAmbito(
        actividad = "¿Qué estabas haciendo?",
        ejemploActividad = "Ej.: volviendo a casa",
        intensidad = "Depresión",
        ayudaIntensidad = "¿Qué tan bajo o baja de ánimo te sentiste?"
    )
    Ambito.ADICCIONES -> TextosAmbito(
        actividad = "Situación",
        ejemploActividad = "Ej.: reunión con amigos",
        intensidad = "Craving",
        ayudaIntensidad = "¿Qué tan fuerte fueron las ganas de consumir?"
    )
    null -> TEXTOS_GENERALES
}

enum class TipoDetalle {
    EMOCION,
    SUENO,
    RIESGO,
    HABILIDAD,
    ESTRATEGIA,
    DESENCADENANTE,
    ACCION_PROTECTORA,
    RESULTADO
}

data class DetalleRegistro(
    val tipo: TipoDetalle,
    val etiqueta: String,
    val valor: String
)

fun etiquetaResultado(ambito: Ambito?): String = when (ambito) {
    Ambito.NEURODESARROLLO -> "¿Te ayudó?"
    Ambito.DBT -> "Utilidad"
    Ambito.ADICCIONES -> "Objetivo personal"
    else -> "Resultado"
}

fun opcionesResultado(ambito: Ambito?): List<String> = when (ambito) {
    Ambito.NEURODESARROLLO -> CamposAmbito.RESULTADOS_ESTRATEGIA
    Ambito.DBT -> CamposAmbito.UTILIDAD_HABILIDAD
    Ambito.ADICCIONES -> CamposAmbito.OBJETIVO_CUMPLIDO
    else -> emptyList()
}

private fun mostrarCatalogo(valor: String): String = valor.removePrefix(PREFIJO_OTRO)

fun detallesDeRegistro(registro: RegistroActividad): List<DetalleRegistro> {
    val ambito = Ambito.desdeId(registro.idAmbito)

    return listOfNotNull(
        registro.emocion?.let { DetalleRegistro(TipoDetalle.EMOCION, "Emoción", it) },
        registro.horasSueno?.let {
            DetalleRegistro(TipoDetalle.SUENO, "Horas de sueño", formatearHoras(it))
        },
        DetalleRegistro(TipoDetalle.RIESGO, "Alto riesgo", "Marcado").takeIf { registro.altoRiesgo },
        registro.habilidad?.let {
            DetalleRegistro(TipoDetalle.HABILIDAD, "Habilidad", mostrarCatalogo(it))
        },
        registro.estrategia?.let { DetalleRegistro(TipoDetalle.ESTRATEGIA, "Estrategia", it) },
        registro.desencadenante?.let {
            DetalleRegistro(TipoDetalle.DESENCADENANTE, "Desencadenante", mostrarCatalogo(it))
        },
        registro.accionProtectora?.let {
            DetalleRegistro(TipoDetalle.ACCION_PROTECTORA, "Acción protectora", mostrarCatalogo(it))
        },
        registro.resultado?.let {
            DetalleRegistro(TipoDetalle.RESULTADO, etiquetaResultado(ambito), it)
        }
    )
}
