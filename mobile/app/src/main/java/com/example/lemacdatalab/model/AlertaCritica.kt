package com.example.lemacdatalab.model

data class AlertaCritica(
    val motivo: String,
    val sugerencia: String
)

fun alertaCriticaDe(registro: RegistroActividad): AlertaCritica? {
    val intensidadCritica = registro.intensidad >= Escala.CRITICO
    val suenoCritico = registro.horasSueno?.let { it < CamposAmbito.HORAS_SUENO_CRITICAS } == true

    return when (Ambito.desdeId(registro.idAmbito)) {
        Ambito.ADICCIONES -> AlertaCritica(
            motivo = "Registraste un craving severo.",
            sugerencia = "Prueba con una acción protectora: cambia de lugar, sal a caminar o habla con " +
                "alguien de confianza. Las ganas suelen bajar en unos minutos."
        ).takeIf { intensidadCritica }

        Ambito.DBT -> AlertaCritica(
            motivo = "Registraste una emoción de intensidad severa.",
            sugerencia = "Prueba la habilidad TIPP: agua fría en la cara, ejercicio breve e intenso, " +
                "respiración lenta y relajación muscular."
        ).takeIf { intensidadCritica }

        Ambito.ANIMO -> when {
            registro.altoRiesgo -> AlertaCritica(
                motivo = "Marcaste que te sentiste en alto riesgo o con experiencias inusuales.",
                sugerencia = "Busca un lugar seguro y no te quedes a solas: avisa a alguien de confianza."
            )
            suenoCritico -> AlertaCritica(
                motivo = "Dormiste menos de ${CamposAmbito.HORAS_SUENO_CRITICAS.toInt()} horas.",
                sugerencia = "Dormir muy poco puede ser una señal de alerta en el ánimo. Intenta " +
                    "descansar y comenta este registro con tu profesional tratante."
            )
            else -> null
        }

        else -> null
    }
}
