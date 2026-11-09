package com.example.lemacdatalab.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.Escala
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.textosDe

@Composable
fun BarrasEscala(registro: RegistroActividad) {
    val ambito = Ambito.desdeId(registro.idAmbito)

    if (ambito == Ambito.ANIMO) {
        BarraIntensidad(
            intensidad = registro.intensidad,
            etiqueta = "Depresión",
            icono = Icons.AutoMirrored.Rounded.TrendingDown
        )
        BarraIntensidad(
            intensidad = registro.elevacion ?: Escala.MINIMO,
            etiqueta = "Elevación",
            icono = Icons.AutoMirrored.Rounded.TrendingUp
        )
    } else {
        BarraIntensidad(intensidad = registro.intensidad, etiqueta = textosDe(ambito).intensidad)
    }
}

@Composable
fun BarraIntensidad(
    intensidad: Int,
    etiqueta: String = "Intensidad",
    icono: ImageVector = Icons.Rounded.Speed
) {
    val valor = intensidad.coerceIn(Escala.MINIMO, Escala.MAXIMO)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${Escala.describir(valor)} (de ${Escala.MAXIMO})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        LinearProgressIndicator(
            progress = { valor.toFloat() / Escala.MAXIMO },
            color = if (valor >= Escala.CRITICO) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.tertiary
            },
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            strokeCap = StrokeCap.Round,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}
