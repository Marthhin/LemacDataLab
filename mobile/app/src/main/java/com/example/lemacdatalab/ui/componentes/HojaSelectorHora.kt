package com.example.lemacdatalab.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Locale

private data class SugerenciaHora(
    val nombre: String,
    val hora: Int,
    val minuto: Int,
    val icono: ImageVector
) {
    val horaTexto: String
        get() = String.format(Locale.ROOT, "%02d:%02d", hora, minuto)
}

private val SUGERENCIAS = listOf(
    SugerenciaHora("Mañana", 8, 0, Icons.Rounded.WbTwilight),
    SugerenciaHora("Mediodía", 13, 0, Icons.Rounded.WbSunny),
    SugerenciaHora("Tarde", 18, 0, Icons.Rounded.LocalCafe),
    SugerenciaHora("Noche", 21, 0, Icons.Rounded.Bedtime)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HojaSelectorHora(
    hora: Int,
    minuto: Int,
    alConfirmar: (Int, Int) -> Unit,
    alCerrar: () -> Unit
) {
    val estadoHoja = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val estadoHora = rememberTimePickerState(
        initialHour = hora,
        initialMinute = minuto,
        is24Hour = true
    )
    val alcance = rememberCoroutineScope()

    fun cerrarYLuego(accion: () -> Unit) {
        alcance.launch { estadoHoja.hide() }.invokeOnCompletion {
            accion()
            alCerrar()
        }
    }

    ModalBottomSheet(
        onDismissRequest = alCerrar,
        sheetState = estadoHoja
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Hora del aviso",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Elige una sugerencia o escribe la hora exacta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "SUGERENCIAS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SUGERENCIAS.chunked(2).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        fila.forEach { sugerencia ->
                            TarjetaSugerencia(
                                sugerencia = sugerencia,
                                seleccionada = sugerencia.hora == hora && sugerencia.minuto == minuto,
                                alPresionar = {
                                    cerrarYLuego { alConfirmar(sugerencia.hora, sugerencia.minuto) }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Text(
                text = "OTRA HORA",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TimeInput(
                state = estadoHora,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { cerrarYLuego { } }) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        cerrarYLuego { alConfirmar(estadoHora.hour, estadoHora.minute) }
                    }
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

@Composable
private fun TarjetaSugerencia(
    sugerencia: SugerenciaHora,
    seleccionada: Boolean,
    alPresionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = alPresionar,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            },
            contentColor = if (seleccionada) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        ),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Icon(imageVector = sugerencia.icono, contentDescription = null)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = sugerencia.nombre,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = sugerencia.horaTexto,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}