package com.example.lemacdatalab.ui.componentes

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.AlertaCritica
import com.example.lemacdatalab.model.ContactoApoyo

private data class LineaAyuda(val nombre: String, val numero: String)

private val LINEAS_AYUDA = listOf(
    LineaAyuda("*4141 · Prevención del suicidio", "*4141"),
    LineaAyuda("600 360 7777 · Salud Responde", "6003607777"),
    LineaAyuda("131 · SAMU", "131")
)

@Composable
fun AvisoCritico(
    alerta: AlertaCritica,
    contacto: ContactoApoyo?,
    alCerrar: () -> Unit
) {
    val context = LocalContext.current

    fun marcar(numero: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(numero)))
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
        }
    }

    AlertDialog(
        onDismissRequest = alCerrar,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Tu registro quedó guardado") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${alerta.motivo} Lo que sientes es importante y no tienes que pasarlo a solas.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = alerta.sugerencia,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Esta app no es un servicio de urgencias y nadie revisa los registros en " +
                        "tiempo real. Si necesitas ayuda ahora, llama:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (contacto != null) {
                    BotonLlamar(
                        texto = "${contacto.nombre} · ${contacto.telefono}",
                        icono = Icons.Rounded.Person,
                        destacado = true,
                        alPresionar = { marcar(contacto.telefono) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }

                LINEAS_AYUDA.forEach { linea ->
                    BotonLlamar(
                        texto = linea.nombre,
                        icono = Icons.Rounded.Call,
                        destacado = contacto == null && linea == LINEAS_AYUDA.first(),
                        alPresionar = { marcar(linea.numero) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = alCerrar) { Text("Entendido") }
        }
    )
}

@Composable
private fun BotonLlamar(
    texto: String,
    icono: ImageVector,
    destacado: Boolean,
    alPresionar: () -> Unit
) {
    val modificador = Modifier
        .fillMaxWidth()
        .heightIn(min = 48.dp)
    val forma = RoundedCornerShape(14.dp)

    val contenido: @Composable RowScope.() -> Unit = {
        Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(texto, modifier = Modifier.weight(1f))
    }

    if (destacado) {
        FilledTonalButton(onClick = alPresionar, shape = forma, modifier = modificador, content = contenido)
    } else {
        OutlinedButton(onClick = alPresionar, shape = forma, modifier = modificador, content = contenido)
    }
}
