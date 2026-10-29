package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.ui.componentes.TarjetaOpcion
import com.example.lemacdatalab.ui.componentes.TarjetaPrincipal
import com.example.lemacdatalab.viewmodel.InicioViewModel

@Composable
fun PantallaInicio(
    viewModel: InicioViewModel,
    alRegistrar: () -> Unit,
    alVerHistorial: () -> Unit,
    alVerRecordatorios: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "LemacDataLab",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = viewModel.saludo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Cuida de ti hoy",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = when {
                viewModel.cargando -> "Cargando tu resumen..."
                viewModel.mensajeError.isNotEmpty() -> viewModel.mensajeError
                viewModel.totalRegistros == 1 -> "Tienes 1 registro guardado"
                else -> "Tienes ${viewModel.totalRegistros} registros guardados"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (viewModel.mensajeError.isNotEmpty()) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TarjetaPrincipal(
            opcion = viewModel.principal,
            alPresionar = alRegistrar
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Más opciones",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        viewModel.secundarias.forEach { opcion ->
            TarjetaOpcion(
                opcion = opcion,
                alPresionar = {
                    when (opcion.nombre) {
                        "Ver historial" -> alVerHistorial()
                        "Recordatorios" -> alVerRecordatorios()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Proyecto académico · Usa datos ficticios",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}