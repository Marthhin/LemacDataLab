package com.example.lemacdatalab.datos

data class UsuarioDataset(
    val alias: String,
    val contrasena: String,
    val nombre: String,
    val objetivo: String
)

object DatasetSintetico {

    val USUARIOS = listOf(
        UsuarioDataset(
            alias = "camila.rojas",
            contrasena = "Lemac2026",
            nombre = "Camila Rojas",
            objetivo = "No consumir alcohol de lunes a jueves"
        ),
        UsuarioDataset(
            alias = "diego.munoz",
            contrasena = "Lemac2026",
            nombre = "Diego Muñoz",
            objetivo = "Reducir el cigarro a 3 por día"
        ),
        UsuarioDataset(
            alias = "valentina.soto",
            contrasena = "Lemac2026",
            nombre = "Valentina Soto",
            objetivo = "Llamar a mi contacto de apoyo cuando aparezca el impulso"
        )
    )
}
