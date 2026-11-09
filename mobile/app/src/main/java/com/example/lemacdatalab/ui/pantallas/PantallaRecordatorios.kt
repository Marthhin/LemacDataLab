package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.ConfigRecordatorio
import com.example.lemacdatalab.ui.componentes.HojaSelectorHora
import com.example.lemacdatalab.ui.componentes.rememberPermisoNotificaciones
import com.example.lemacdatalab.util.abrirAjustesNotificaciones
import com.example.lemacdatalab.util.describirProximoAviso
import java.util.Calendar

private data class DiaSemana(
    val valor: Int,
    val inicial: String,
    val nombre: String
)

private val DIAS_SEMANA = listOf(
    DiaSemana(Calendar.MONDAY, "L", "Lunes"),
    DiaSemana(Calendar.TUESDAY, "M", "Martes"),
    DiaSemana(Calendar.WEDNESDAY, "M", "Miércoles"),
    DiaSemana(Calendar.THURSDAY, "J", "Jueves"),
    DiaSemana(Calendar.FRIDAY, "V", "Viernes"),
    DiaSemana(Calendar.SATURDAY, "S", "Sábado"),
    DiaSemana(Calendar.SUNDAY, "D", "Domingo")
)

@Composable
fun PantallaRecordatorios(
    config: ConfigRecordatorio,
    alActivar: () -> Unit,
    alDesactivar: () -> Unit,
    alCambiarHora: (Int, Int) -> Unit,
    alAlternarDia: (Int) -> Unit,
    alCambiarOmitir: (Boolean) -> Unit,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val permiso = rememberPermisoNotificaciones()
    var activarAlConceder by remember { mutableStateOf(false) }
    var mostrarSelectorHora by remember { mutableStateOf(false) }

    LaunchedEffect(permiso.concedido) {
        if (permiso.concedido && activarAlConceder) {
            activarAlConceder = false
            alActivar()
        }
    }

    if (mostrarSelectorHora) {
        HojaSelectorHora(
            hora = config.hora,
            minuto = config.minuto,
            alConfirmar = alCambiarHora,
            alCerrar = { mostrarSelectorHora = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = alVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Volver al inicio"
                )
            }

            Text(
                text = "Recordatorios",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Un aviso discreto para acordarte de registrar cómo va tu día.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!permiso.concedido) {
                AvisoNotificacionesDesactivadas(
                    alAbrirAjustes = { abrirAjustesNotificaciones(contexto) }
                )
            }

            TarjetaRecordatorio(
                config = config,
                alCambiarActivo = { activo ->
                    when {
                        !activo -> alDesactivar()
                        permiso.concedido -> alActivar()
                        else -> {
                            activarAlConceder = true
                            permiso.solicitar()
                        }
                    }
                },
                alElegirHora = { mostrarSelectorHora = true },
                alAlternarDia = alAlternarDia,
                alCambiarOmitir = alCambiarOmitir
            )

            if (config.activo) {
                config.proximoDisparo()?.let { momento ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Próximo aviso: ${describirProximoAviso(momento)}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "El aviso usa un mensaje general y nunca incluye información de tus registros.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
            }

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Puede llegar con algunos minutos de diferencia: Android agrupa las alarmas para cuidar la batería.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun TarjetaRecordatorio(
    config: ConfigRecordatorio,
    alCambiarActivo: (Boolean) -> Unit,
    alElegirHora: () -> Unit,
    alAlternarDia: (Int) -> Unit,
    alCambiarOmitir: (Boolean) -> Unit
) {
    Card(
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Alarm,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = "Recordatorio",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (config.activo) "Activado a las ${config.horaTexto}" else "Desactivado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = config.activo,
                    onCheckedChange = alCambiarActivo
                )
            }

            if (config.activo) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = alElegirHora)
                        .padding(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Hora del aviso",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                    )
                    Text(
                        text = config.horaTexto,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Cambiar hora",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(18.dp)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "DÍAS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SelectorDias(dias = config.dias, alAlternarDia = alAlternarDia)
                    if (config.dias.size == 1) {
                        Text(
                            text = "Debe quedar al menos un día seleccionado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "No avisarme si ya registré hoy",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Si ese día ya hiciste un registro, no te enviaremos el aviso.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.omitirSiRegistrado,
                        onCheckedChange = alCambiarOmitir,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectorDias(
    dias: Set<Int>,
    alAlternarDia: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        DIAS_SEMANA.forEach { dia ->
            val seleccionado = dia.valor in dias

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (seleccionado) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        }
                    )
                    .toggleable(
                        value = seleccionado,
                        role = Role.Checkbox,
                        onValueChange = { alAlternarDia(dia.valor) }
                    )
                    .semantics { contentDescription = dia.nombre }
            ) {
                Text(
                    text = dia.inicial,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (seleccionado) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun AvisoNotificacionesDesactivadas(alAbrirAjustes: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Rounded.NotificationsOff, contentDescription = null)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "Notificaciones desactivadas",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Actívalas para recibir tus recordatorios.",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = alAbrirAjustes,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Abrir ajustes")
                }
            }
        }
    }
}