package com.example.lemacdatalab.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import java.util.Calendar

private val INICIALES = mapOf(
    Calendar.MONDAY to "L",
    Calendar.TUESDAY to "M",
    Calendar.WEDNESDAY to "M",
    Calendar.THURSDAY to "J",
    Calendar.FRIDAY to "V",
    Calendar.SATURDAY to "S",
    Calendar.SUNDAY to "D"
)

@Composable
fun TarjetaRacha(
    racha: Int,
    semana: List<Boolean>,
    registroHoy: Boolean
) {
    val iniciales = remember {
        val dia = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }
        List(7) {
            val inicial = INICIALES[dia.get(Calendar.DAY_OF_WEEK)].orEmpty()
            dia.add(Calendar.DAY_OF_YEAR, 1)
            inicial
        }
    }

    val titulo = if (racha == 1) "1 día dándote un momento" else "$racha días dándote un momento"
    val subtitulo = if (registroHoy) {
        "Hoy ya te diste un momento."
    } else {
        "Hoy también cuenta, cuando tengas un rato."
    }

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
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFlorist,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = titulo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {
                        contentDescription = "${semana.count { it }} de los últimos 7 días con registro"
                    }
            ) {
                semana.forEachIndexed { indice, conRegistro ->
                    val esHoy = indice == semana.lastIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .then(
                                    if (conRegistro) {
                                        Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                                    } else {
                                        Modifier.border(
                                            width = if (esHoy) 2.dp else 1.dp,
                                            color = if (esHoy) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outlineVariant
                                            },
                                            shape = CircleShape
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (conRegistro) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = iniciales[indice],
                            style = MaterialTheme.typography.labelSmall,
                            color = if (esHoy) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}
