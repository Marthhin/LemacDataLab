package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.EvolucionAmbito
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.ui.componentes.TarjetaEvolucion
import com.example.lemacdatalab.ui.componentes.TarjetaRegistro

@Composable
fun PantallaEvolucionAmbito(
    ambito: Ambito,
    evolucion: EvolucionAmbito?,
    registros: List<RegistroActividad>,
    periodo: Int,
    alCambiarPeriodo: (Int) -> Unit,
    alVerRegistro: (Int) -> Unit,
    alVerHistorial: () -> Unit,
    alVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = alVolver) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = ambito.nombre,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SelectorPeriodo(
                periodo = periodo,
                alCambiar = alCambiarPeriodo,
                modifier = Modifier.fillMaxWidth()
            )

            if (evolucion != null) {
                TarjetaEvolucion(evolucion = evolucion)
            }

            Text(
                text = "ÚLTIMOS REGISTROS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (registros.isEmpty()) {
                Text(
                    text = "Aún no tienes registros de este ámbito.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                registros.take(MAXIMO_RECIENTES).forEach { registro ->
                    TarjetaRegistro(
                        registro = registro,
                        alPresionar = { alVerRegistro(registro.idRegistro) }
                    )
                }
            }

            OutlinedButton(
                onClick = alVerHistorial,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                Text("Ver todo en el historial")
            }
        }
    }
}

private const val MAXIMO_RECIENTES = 5
