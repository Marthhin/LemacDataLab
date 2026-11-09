package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.Escala
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.VersionRegistro
import com.example.lemacdatalab.ui.componentes.BarrasEscala
import com.example.lemacdatalab.ui.componentes.FilaDato
import com.example.lemacdatalab.ui.componentes.FilasDetallesAmbito
import com.example.lemacdatalab.ui.componentes.IconoAmbito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaDetalle(
    registro: RegistroActividad?,
    versiones: List<VersionRegistro>,
    cargando: Boolean,
    eliminando: Boolean,
    mensajeError: String,
    alEditar: (RegistroActividad) -> Unit,
    alEliminar: () -> Unit,
    alVolver: () -> Unit
) {
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }

    if (confirmarEliminar && registro != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            icon = { Icon(Icons.Rounded.Delete, contentDescription = null) },
            title = { Text("Eliminar registro") },
            text = {
                Text(
                    "¿Quieres eliminar “${registro.tipoActividad}”? Dejará de verse y de contar " +
                        "en tus resúmenes y gráficos."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                        alEliminar()
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }

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
                text = "Detalle",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        when {
            cargando || eliminando -> EstadoDetalle {
                CircularProgressIndicator()
            }

            registro == null -> EstadoDetalle {
                Icon(
                    imageVector = Icons.Rounded.SearchOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = mensajeError.ifEmpty { "Este registro ya no existe" },
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = alVolver) { Text("Volver") }
            }

            else -> ContenidoDetalle(
                registro = registro,
                versiones = versiones,
                mensajeError = mensajeError,
                alEditar = { alEditar(registro) },
                alEliminar = { confirmarEliminar = true }
            )
        }
    }
}

@Composable
private fun ContenidoDetalle(
    registro: RegistroActividad,
    versiones: List<VersionRegistro>,
    mensajeError: String,
    alEditar: () -> Unit,
    alEliminar: () -> Unit
) {
    val ambito = Ambito.desdeId(registro.idAmbito)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconoAmbito(ambito = ambito, tamano = 56.dp)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = buildString {
                    append((ambito?.nombre ?: "Sin ámbito").uppercase())
                    if (registro.editado) append(" · EDITADO")
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = registro.tipoActividad,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilaDato(Icons.Rounded.CalendarMonth, "Fecha", registro.fechaHora)
                BarrasEscala(registro)
                FilaDato(Icons.Rounded.Place, "Contexto", registro.contexto)
                FilasDetallesAmbito(registro)
            }
        }

        if (!registro.nota.isNullOrBlank()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "NOTA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = registro.nota,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        if (versiones.isNotEmpty()) {
            HistorialVersiones(versiones = versiones, esAnimo = ambito == Ambito.ANIMO)
        }

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = alEliminar,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar")
            }

            Button(
                onClick = alEditar,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar")
            }
        }
    }
}

@Composable
private fun HistorialVersiones(versiones: List<VersionRegistro>, esAnimo: Boolean) {
    var expandido by rememberSaveable { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido }
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (versiones.size == 1) "1 versión anterior" else "${versiones.size} versiones anteriores",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expandido) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (expandido) "Ocultar versiones" else "Ver versiones"
                )
            }

            AnimatedVisibility(visible = expandido) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    versiones.forEach { version ->
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Hasta el ${formatearMomento(version.fechaGuardado)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(version.tipoActividad, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = buildString {
                                    append(if (esAnimo) "Depresión " else "Intensidad ")
                                    append(Escala.describir(version.intensidad))
                                    version.elevacion?.let { append(" · Elevación ${Escala.describir(it)}") }
                                    append(" · ${version.contexto}")
                                    version.detalles?.let { append(" · $it") }
                                    version.nota?.let { append(" · “$it”") }
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatearMomento(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm", Locale.forLanguageTag("es-CL")).format(Date(millis))

@Composable
private fun EstadoDetalle(contenido: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = contenido
        )
    }
}