package com.example.lemacdatalab.ui.componentes

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.lemacdatalab.util.abrirAjustesNotificaciones
import com.example.lemacdatalab.util.tienePermisoNotificaciones

class PermisoNotificaciones(
    val concedido: Boolean,
    val puedePreguntar: Boolean,
    val solicitar: () -> Unit
)

@Composable
fun rememberPermisoNotificaciones(): PermisoNotificaciones {
    val contexto = LocalContext.current
    var concedido by remember { mutableStateOf(tienePermisoNotificaciones(contexto)) }

    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        concedido = tienePermisoNotificaciones(contexto)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        concedido = tienePermisoNotificaciones(contexto)
    }

    val puedePreguntar = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    return PermisoNotificaciones(
        concedido = concedido,
        puedePreguntar = puedePreguntar,
        solicitar = {
            if (puedePreguntar) {
                lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                abrirAjustesNotificaciones(contexto)
            }
        }
    )
}

@Composable
fun TarjetaPermisoNotificaciones(
    alPermitir: () -> Unit,
    alOmitir: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.NotificationsActive, contentDescription = null)
                Text(
                    text = "Activa los recordatorios",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Text(
                text = "Te enviaremos un aviso discreto para registrar tu día. Nunca mostraremos tus registros en la notificación.",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = alOmitir) {
                    Text("Ahora no")
                }
                Button(onClick = alPermitir) {
                    Text("Permitir")
                }
            }
        }
    }
}