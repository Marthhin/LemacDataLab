package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.ui.componentes.TarjetaOpcion
import com.example.lemacdatalab.ui.componentes.TarjetaPrincipal
import com.example.lemacdatalab.viewmodel.InicioViewModel

@Composable
fun PantallaInicio(
    viewModel: InicioViewModel,
    alias: String,
    alRegistrar: () -> Unit,
    alVerHistorial: () -> Unit,
    alVerRecordatorios: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            icon = { Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null) },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tus registros quedan guardados en este dispositivo.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarSalida = false
                        alCerrarSesion()
                    }
                ) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "LEMACDATALAB",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { confirmarSalida = true }) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = alias.take(1).uppercase().ifEmpty { "?" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Text(
            text = if (alias.isNotEmpty()) "${viewModel.saludo}, $alias" else viewModel.saludo,
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