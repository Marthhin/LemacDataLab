package com.example.lemacdatalab.model

enum class Ambito(val id: Int, val nombre: String) {
    NEURODESARROLLO(1, "Neurodesarrollo"),
    DBT(2, "DBT"),
    ANIMO(3, "Estado de ánimo"),
    ADICCIONES(4, "Adicciones");

    companion object {
        fun desdeId(id: Int): Ambito? = entries.find { it.id == id }
    }
}