package com.example.lemacdatalab.datos

import com.example.lemacdatalab.model.Contenido

class RepositorioContenidos {

    fun obtenerTodos(): List<Contenido> = CatalogoContenidos.contenidos
}