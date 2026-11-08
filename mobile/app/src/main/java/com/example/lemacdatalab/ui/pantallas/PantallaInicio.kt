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
import com.example.lemacdatalab.ui.componentes.TarjetaCartaAnimo
import com.example.lemacdatalab.ui.componentes.TarjetaOpcion
import com.example.lemacdatalab.ui.componentes.TarjetaPrincipal
import com.example.lemacdatalab.ui.componentes.TarjetaRegistro
import com.example.lemacdatalab.ui.componentes.TarjetaSinRegistros
import com.example.lemacdatalab.viewmodel.InicioViewModel

@Composable
fun PantallaInicio(
    viewModel: InicioViewModel,
    alias: String,
    alRegistrar: () -> Unit,
    alVerHistorial: () -> Unit,
    alVerRegistro: (Int) -> Unit,
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

        Spacer(modifier = Modifier.height(4.dp))

        TarjetaPrincipal(
            opcion = viewModel.principal,
            alPresionar = alRegistrar
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TituloSeccion(
                texto = "TU ÚLTIMO REGISTRO",
                modifier = Modifier.weight(1f)
            )
            if (!viewModel.cargando && viewModel.totalRegistros > 0) {
                Text(
                    text = "${viewModel.totalRegistros} en total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val ultimo = viewModel.ultimoRegistro
        when {
            viewModel.cargando -> Unit
            viewModel.mensajeError.isNotEmpty() -> Text(
                text = viewModel.mensajeError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            ultimo == null -> TarjetaSinRegistros(alPresionar = alRegistrar)
            else -> TarjetaRegistro(
                registro = ultimo,
                alPresionar = { alVerRegistro(ultimo.idRegistro) }
            )
        }

        if (!viewModel.cargando && viewModel.totalRegistros > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            TarjetaCartaAnimo(puntos = viewModel.puntosGrafico)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TituloSeccion(texto = "MÁS OPCIONES")

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

@Composable
private fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}