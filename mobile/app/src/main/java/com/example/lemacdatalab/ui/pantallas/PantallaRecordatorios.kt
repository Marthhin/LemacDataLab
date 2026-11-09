package com.example.lemacdatalab.ui.pantallas

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.lemacdatalab.model.ConfigRecordatorio
import com.example.lemacdatalab.notificaciones.NotificacionesRecordatorio

private data class ResultadoRecordatorio(
    val texto: String,
    val exito: Boolean
)

@Composable
fun PantallaRecordatorios(
    config: ConfigRecordatorio,
    alActivar: () -> Unit,
    alDesactivar: () -> Unit,
    alCambiarHora: (Int, Int) -> Unit,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current

    var resultado by remember { mutableStateOf<ResultadoRecordatorio?>(null) }
    var accionPendiente by remember { mutableStateOf<(() -> Unit)?>(null) }

    val enviarPrueba: () -> Unit = {
        resultado = if (NotificacionesRecordatorio.mostrar(contexto)) {
            ResultadoRecordatorio("Aviso enviado. Revisa la barra de notificaciones.", true)
        } else {
            ResultadoRecordatorio(
                "Las notificaciones están desactivadas. Puedes activarlas en los ajustes de la aplicación.",
                false
            )
        }
    }

    val activarRecordatorio: () -> Unit = {
        alActivar()
        resultado = ResultadoRecordatorio(
            "Listo. Te avisaremos todos los días a las ${config.horaTexto}.",
            true
        )
    }

    val solicitarPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permitido ->
        if (permitido) {
            accionPendiente?.invoke()
        } else {
            resultado = ResultadoRecordatorio(
                "Sin permiso de notificaciones no podemos enviarte avisos. Puedes activarlo en los ajustes de la aplicación.",
                false
            )
        }
        accionPendiente = null
    }

    fun conPermiso(accion: () -> Unit) {
        val tienePermiso = Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(
                    contexto,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            accion()
        } else {
            accionPendiente = accion
            solicitarPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun elegirHora() {
        TimePickerDialog(
            contexto,
            { _, hora, minuto ->
                alCambiarHora(hora, minuto)
                resultado = null
            },
            config.hora,
            config.minuto,
            true
        ).show()
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
                text = "Un aviso diario y discreto para acordarte de registrar cómo va tu día.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TarjetaRecordatorioDiario(
                config = config,
                alCambiarActivo = { activo ->
                    if (activo) {
                        conPermiso(activarRecordatorio)
                    } else {
                        alDesactivar()
                        resultado = null
                    }
                },
                alElegirHora = { elegirHora() }
            )

            resultado?.let { TarjetaResultadoRecordatorio(it) }

            Text(
                text = "ASÍ SE VERÁ EL AVISO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            VistaPreviaRecordatorio()

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

            OutlinedButton(
                onClick = { conPermiso(enviarPrueba) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsActive,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar aviso de prueba")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "El aviso puede llegar con algunos minutos de diferencia: Android agrupa las alarmas para cuidar la batería.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaRecordatorioDiario(
    config: ConfigRecordatorio,
    alCambiarActivo: (Boolean) -> Unit,
    alElegirHora: () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        text = "Recordatorio diario",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (config.activo) {
                            "Todos los días a las ${config.horaTexto}"
                        } else {
                            "Desactivado"
                        },
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
                        .padding(vertical = 8.dp)
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
            }
        }
    }
}

@Composable
private fun VistaPreviaRecordatorio() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "LemacDataLab",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = NotificacionesRecordatorio.TITULO,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = NotificacionesRecordatorio.TEXTO,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun TarjetaResultadoRecordatorio(resultado: ResultadoRecordatorio) {
    val fondo = if (resultado.exito) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }

    val colorTexto = if (resultado.exito) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = fondo,
            contentColor = colorTexto
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (resultado.exito) Icons.Rounded.CheckCircle else Icons.Rounded.ErrorOutline,
                contentDescription = null
            )
            Text(
                text = resultado.texto,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            )
        }
    }
}