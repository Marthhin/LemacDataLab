package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.ui.componentes.BotonPrincipal
import com.example.lemacdatalab.ui.componentes.EncabezadoApp

@Composable
fun PantallaInicio() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EncabezadoApp(
            titulo = "LemacDataLab",
            descripcion = "Registra y consulta tus actividades de autocuidado."
        )

        BotonPrincipal(
            texto = "Registrar actividad",
            alPresionar = {
                // La navegación se agregará más adelante
            }
        )

        BotonPrincipal(
            texto = "Ver historial",
            alPresionar = {
                // La navegación se agregará más adelante
            }
        )

        BotonPrincipal(
            texto = "Recursos de apoyo",
            alPresionar = {
                // La navegación se agregará más adelante
            }
        )

        BotonPrincipal(
            texto = "Recordatorios",
            alPresionar = {
                // La navegación se agregará más adelante
            }
        )
    }
}