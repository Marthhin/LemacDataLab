package com.example.lemacdatalab.model

data class Consentimiento(
    val version: String,
    val fechaAceptacion: Long
)

data class ContactoApoyo(
    val nombre: String,
    val telefono: String
)

data class SeccionConsentimiento(
    val titulo: String,
    val texto: String
)

object TextoConsentimiento {
    const val VERSION = "1.0"

    val SECCIONES = listOf(
        SeccionConsentimiento(
            "Para qué sirve",
            "LemacDataLab te ayuda a llevar un registro diario de neurodesarrollo, habilidades DBT, " +
                "ánimo y adicciones, y a ver tu evolución en el tiempo."
        ),
        SeccionConsentimiento(
            "Qué datos guardamos",
            "Lo que tú registras: fecha, ámbito, intensidades, contexto, notas y los detalles de cada " +
                "ámbito. No pedimos tu ubicación, contactos ni fotos."
        ),
        SeccionConsentimiento(
            "Dónde se guardan",
            "En este dispositivo. Nadie más los ve ni los revisa en tiempo real, y no los compartimos " +
                "con terceros."
        ),
        SeccionConsentimiento(
            "Tus derechos",
            "Puedes revisar, corregir o eliminar tus registros cuando quieras. Las correcciones dejan " +
                "un rastro de la versión anterior. También puedes eliminar tu cuenta y todos tus datos " +
                "desde «Mi cuenta»."
        ),
        SeccionConsentimiento(
            "No es un servicio de urgencias",
            "La app no reemplaza la atención de un profesional. Si estás en riesgo, llama al *4141, " +
                "a Salud Responde (600 360 7777) o al SAMU (131)."
        ),
        SeccionConsentimiento(
            "Participación voluntaria",
            "Aceptar es voluntario. Si no aceptas, puedes seguir usando los recursos de la app, pero " +
                "no podrás crear registros."
        )
    )
}
