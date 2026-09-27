package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.ui.componentes.BotonPrincipal
import com.example.lemacdatalab.ui.componentes.EncabezadoApp
import com.example.lemacdatalab.viewmodel.InicioViewModel

@Composable
fun PantallaInicio(
    viewModel: InicioViewModel
) {
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

        Text(text = viewModel.mensajeSeleccionado)

        viewModel.opciones.forEach { opcion ->
            BotonPrincipal(
                texto = opcion.nombre,
                alPresionar = {
                    viewModel.seleccionarOpcion(opcion)
                }
            )
        }
    }
}