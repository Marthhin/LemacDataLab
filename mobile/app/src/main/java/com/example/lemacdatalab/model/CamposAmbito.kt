package com.example.lemacdatalab.model

object CamposAmbito {
    val EMOCIONES = listOf("Alegría", "Calma", "Tristeza", "Ansiedad", "Enojo", "Cansancio")

    val HABILIDADES_DBT = listOf(
        "Mindfulness",
        "Tolerancia al malestar",
        "Regulación emocional",
        "Efectividad interpersonal"
    )

    val RESULTADOS_ESTRATEGIA = listOf("Me ayudó", "Me ayudó en parte", "No me ayudó")

    val UTILIDAD_HABILIDAD = listOf("Útil", "Algo útil", "Poco útil")

    val OBJETIVO_CUMPLIDO = listOf("Sí", "En parte", "No")
}

data class TextosAmbito(
    val actividad: String,
    val ejemploActividad: String,
    val intensidad: String,
    val ayudaIntensidad: String
)

private val TEXTOS_GENERALES = TextosAmbito(
    actividad = "Actividad realizada",
    ejemploActividad = "Ej.: caminata de prueba",
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
        intensidad = "Intensidad",
        ayudaIntensidad = "¿Qué tan fuerte fue lo que sentiste?"
    )
    Ambito.ADICCIONES -> TextosAmbito(
        actividad = "Situación",
        ejemploActividad = "Ej.: reunión con amigos",
        intensidad = "Nivel de impulso",
        ayudaIntensidad = "¿Qué tan fuerte fue el impulso?"
    )
    null -> TEXTOS_GENERALES
}

enum class TipoDetalle {
    EMOCION,
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
    Ambito.ADICCIONES -> "Objetivo cumplido"
    else -> "Resultado"
}

fun opcionesResultado(ambito: Ambito?): List<String> = when (ambito) {
    Ambito.NEURODESARROLLO -> CamposAmbito.RESULTADOS_ESTRATEGIA
    Ambito.DBT -> CamposAmbito.UTILIDAD_HABILIDAD
    Ambito.ADICCIONES -> CamposAmbito.OBJETIVO_CUMPLIDO
    else -> emptyList()
}

fun detallesDeRegistro(registro: RegistroActividad): List<DetalleRegistro> {
    val ambito = Ambito.desdeId(registro.idAmbito)

    return listOfNotNull(
        registro.emocion?.let { DetalleRegistro(TipoDetalle.EMOCION, "Emoción", it) },
        registro.habilidad?.let { DetalleRegistro(TipoDetalle.HABILIDAD, "Habilidad", it) },
        registro.estrategia?.let { DetalleRegistro(TipoDetalle.ESTRATEGIA, "Estrategia", it) },
        registro.desencadenante?.let {
            DetalleRegistro(TipoDetalle.DESENCADENANTE, "Desencadenante", it)
        },
        registro.accionProtectora?.let {
            DetalleRegistro(TipoDetalle.ACCION_PROTECTORA, "Acción protectora", it)
        },
        registro.resultado?.let {
            DetalleRegistro(TipoDetalle.RESULTADO, etiquetaResultado(ambito), it)
        }
    )
}
