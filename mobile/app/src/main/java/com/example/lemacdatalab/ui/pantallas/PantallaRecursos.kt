package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.Contenido
import com.example.lemacdatalab.ui.componentes.FiltrosAmbito
import com.example.lemacdatalab.ui.componentes.IconoAmbito

@Composable
fun PantallaRecursos(
    contenidos: List<Contenido>,
    filtro: Ambito?,
    idExpandido: Int?,
    alCambiarFiltro: (Ambito?) -> Unit,
    alAlternar: (Int) -> Unit
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Recursos de apoyo",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${contenidos.size} " + if (contenidos.size == 1) "recurso" else "recursos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        FiltrosAmbito(filtro = filtro, alCambiarFiltro = alCambiarFiltro)

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(items = contenidos, key = { it.id }) { contenido ->
                TarjetaContenido(
                    contenido = contenido,
                    expandido = idExpandido == contenido.id,
                    alPresionar = { alAlternar(contenido.id) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaContenido(
    contenido: Contenido,
    expandido: Boolean,
    alPresionar: () -> Unit
) {
    val giroFlecha by animateFloatAsState(
        targetValue = if (expandido) 180f else 0f,
        label = "giroFlecha"
    )

    Card(
        onClick = alPresionar,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoAmbito(ambito = contenido.ambito)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(contenido.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${contenido.tipo.nombre} · ${contenido.minutos} min · ${contenido.ambito.nombre}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = if (expandido) "Contraer" else "Expandir",
                    modifier = Modifier.rotate(giroFlecha)
                )
            }

            if (expandido) {
                Text(
                    text = contenido.resumen,
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                contenido.pasos.forEachIndexed { indice, paso ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${indice + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = paso,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}