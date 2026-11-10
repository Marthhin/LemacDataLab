package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.ui.componentes.TarjetaEvolucionMini
import com.example.lemacdatalab.ui.componentes.TarjetaPermisoNotificaciones
import com.example.lemacdatalab.ui.componentes.TarjetaPrincipal
import com.example.lemacdatalab.ui.componentes.TarjetaRacha
import com.example.lemacdatalab.ui.componentes.rememberPermisoNotificaciones
import com.example.lemacdatalab.viewmodel.InicioViewModel

@Composable
fun PantallaInicio(
    viewModel: InicioViewModel,
    alias: String,
    mostrarAvisoPermiso: Boolean,
    alMarcarPermisoPreguntado: () -> Unit,
    alRegistroHoy: () -> Unit,
    alVerAmbito: (Ambito) -> Unit,
    alVerPerfil: () -> Unit
) {
    val permiso = rememberPermisoNotificaciones()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (alias.isNotEmpty()) "${viewModel.saludo}, $alias" else viewModel.saludo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Mi evolución",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(onClick = alVerPerfil) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = alias.take(1).uppercase().ifEmpty { "?" },
                        modifier = Modifier.semantics { contentDescription = "Perfil" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = mostrarAvisoPermiso && permiso.puedePreguntar && !permiso.concedido
        ) {
            TarjetaPermisoNotificaciones(
                alPermitir = {
                    alMarcarPermisoPreguntado()
                    permiso.solicitar()
                },
                alOmitir = alMarcarPermisoPreguntado
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        TarjetaPrincipal(
            opcion = viewModel.principal,
            alPresionar = alRegistroHoy
        )

        if (!viewModel.cargando && viewModel.racha > 0) {
            TarjetaRacha(
                racha = viewModel.racha,
                semana = viewModel.semana,
                registroHoy = viewModel.registroHoy
            )
        }

        if (viewModel.mensajeError.isNotEmpty()) {
            Text(
                text = viewModel.mensajeError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!viewModel.cargando && viewModel.totalRegistros > 0) {
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "POR ÁMBITO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                SelectorPeriodo(
                    periodo = viewModel.periodo,
                    alCambiar = viewModel::cambiarPeriodo
                )
            }

            viewModel.evolucion.chunked(2).forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    fila.forEach { evolucion ->
                        TarjetaEvolucionMini(
                            evolucion = evolucion,
                            alPresionar = { alVerAmbito(evolucion.ambito) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorPeriodo(
    periodo: Int,
    alCambiar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val opciones = listOf(InicioViewModel.PERIODO_SEMANA, InicioViewModel.PERIODO_MES)

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        opciones.forEachIndexed { indice, dias ->
            SegmentedButton(
                selected = dias == periodo,
                onClick = { alCambiar(dias) },
                shape = SegmentedButtonDefaults.itemShape(index = indice, count = opciones.size),
                icon = {},
                label = { Text("$dias días") }
            )
        }
    }
}
