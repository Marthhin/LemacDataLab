package com.example.lemacdatalab.datos

import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.Contenido
import com.example.lemacdatalab.model.TipoContenido

object CatalogoContenidos {

    val contenidos = listOf(
        Contenido(
            id = 1,
            ambito = Ambito.NEURODESARROLLO,
            titulo = "Dividir una tarea en pasos",
            resumen = "Cuando una tarea se siente demasiado grande, separarla en partes pequeñas ayuda a empezar.",
            tipo = TipoContenido.EJERCICIO,
            minutos = 5,
            pasos = listOf(
                "Escribe la tarea completa en una sola frase.",
                "Divídela en 3 a 5 pasos concretos.",
                "Elige el primer paso y dedícale 10 minutos.",
                "Marca el paso como hecho antes de pasar al siguiente."
            )
        ),
        Contenido(
            id = 2,
            ambito = Ambito.NEURODESARROLLO,
            titulo = "Bloques de tiempo con pausas",
            resumen = "Trabajar en bloques cortos con descansos planificados puede facilitar la concentración.",
            tipo = TipoContenido.EJERCICIO,
            minutos = 25,
            pasos = listOf(
                "Define un bloque de 20 a 25 minutos para una sola tarea.",
                "Usa un temporizador y deja el teléfono lejos.",
                "Al terminar, toma una pausa de 5 minutos.",
                "Después de 3 o 4 bloques, haz una pausa más larga."
            )
        ),
        Contenido(
            id = 3,
            ambito = Ambito.DBT,
            titulo = "Habilidad STOP",
            resumen = "Una pausa breve para no reaccionar de inmediato ante una emoción intensa.",
            tipo = TipoContenido.EJERCICIO,
            minutos = 2,
            pasos = listOf(
                "Para: no actúes todavía.",
                "Toma distancia: respira y da un paso atrás.",
                "Observa qué está pasando dentro y fuera de ti.",
                "Procede con atención, eligiendo qué hacer."
            )
        ),
        Contenido(
            id = 4,
            ambito = Ambito.DBT,
            titulo = "Mente sabia",
            resumen = "En DBT, la mente sabia es el equilibrio entre lo que sientes y lo que indican los hechos.",
            tipo = TipoContenido.LECTURA,
            minutos = 3,
            pasos = listOf(
                "Reconoce qué emoción estás sintiendo.",
                "Reconoce qué dicen los hechos de la situación.",
                "Pregúntate qué sabes que es cierto considerando ambas cosas."
            )
        ),
        Contenido(
            id = 5,
            ambito = Ambito.ANIMO,
            titulo = "Respiración pausada",
            resumen = "Respirar a un ritmo lento y parejo puede ayudar a bajar la activación del cuerpo.",
            tipo = TipoContenido.EJERCICIO,
            minutos = 3,
            pasos = listOf(
                "Inhala por la nariz contando hasta 4.",
                "Mantén el aire contando hasta 4.",
                "Exhala lentamente contando hasta 4.",
                "Espera contando hasta 4 y repite durante 1 a 3 minutos."
            )
        ),
        Contenido(
            id = 6,
            ambito = Ambito.ANIMO,
            titulo = "Técnica 5-4-3-2-1",
            resumen = "Un ejercicio para volver al momento presente usando los sentidos.",
            tipo = TipoContenido.EJERCICIO,
            minutos = 3,
            pasos = listOf(
                "Nombra 5 cosas que puedes ver.",
                "Nombra 4 cosas que puedes tocar.",
                "Nombra 3 sonidos que puedes escuchar.",
                "Nombra 2 olores que puedes percibir.",
                "Nombra 1 sabor que puedes notar."
            )
        ),
        Contenido(
            id = 7,
            ambito = Ambito.ADICCIONES,
            titulo = "Surfear el impulso",
            resumen = "Los impulsos suelen subir, llegar a un punto máximo y bajar, como una ola.",
            tipo = TipoContenido.EJERCICIO,
            minutos = 5,
            pasos = listOf(
                "Nota el impulso sin actuar sobre él.",
                "Describe dónde lo sientes en el cuerpo.",
                "Respira y observa cómo cambia su intensidad.",
                "Recuerda que suele bajar en algunos minutos."
            )
        ),
        Contenido(
            id = 8,
            ambito = Ambito.ADICCIONES,
            titulo = "Plan ante desencadenantes",
            resumen = "Anticipar las situaciones de riesgo permite decidir qué hacer antes de que ocurran.",
            tipo = TipoContenido.LECTURA,
            minutos = 4,
            pasos = listOf(
                "Anota una situación que suele activar el impulso.",
                "Escribe una acción protectora que puedas hacer en ese momento.",
                "Identifica a una persona de confianza a quien contactar."
            )
        )
    )
}