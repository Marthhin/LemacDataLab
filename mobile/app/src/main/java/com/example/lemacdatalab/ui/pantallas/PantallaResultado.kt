package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.ui.componentes.BarraIntensidad
import com.example.lemacdatalab.ui.componentes.FilaDato
import com.example.lemacdatalab.ui.componentes.FilasDetallesAmbito
import com.example.lemacdatalab.ui.componentes.IconoAmbito

@Composable
fun PantallaResultado(
    registro: RegistroActividad?,
    editando: Boolean,
    alVolver: () -> Unit,
    alRegistrarOtra: () -> Unit
) {
    var mostrar by remember(registro?.idRegistro) { mutableStateOf(false) }

    LaunchedEffect(registro?.idRegistro) {
        mostrar = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(
            visible = mostrar,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Text(
            text = when {
                registro == null -> "Sin registro"
                editando -> "¡Cambios guardados!"
                else -> "¡Actividad guardada!"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = if (registro == null) {
                "Todavía no hay un registro para mostrar."
            } else {
                "Quedó guardada en este dispositivo."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (registro != null) {
            AnimatedVisibility(
                visible = mostrar,
                enter = fadeIn(tween(450, delayMillis = 150)) +
                        expandVertically(tween(450, delayMillis = 150))
            ) {
                TarjetaResumen(registro)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = alVolver,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Volver al inicio", style = MaterialTheme.typography.titleMedium)
        }

        OutlinedButton(
            onClick = alRegistrarOtra,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Registrar otra actividad")
        }
    }
}

@Composable
private fun TarjetaResumen(registro: RegistroActividad) {
    val ambito = Ambito.desdeId(registro.idAmbito)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoAmbito(ambito = ambito)

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(registro.tipoActividad, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = ambito?.nombre ?: "Sin ámbito",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            FilaDato(Icons.Rounded.CalendarMonth, "Fecha", registro.fechaHora)
            BarraIntensidad(intensidad = registro.intensidad)
            FilaDato(Icons.Rounded.Place, "Contexto", registro.contexto)
            FilasDetallesAmbito(registro)

            if (!registro.nota.isNullOrBlank()) {
                Text(
                    text = "“${registro.nota}”",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}