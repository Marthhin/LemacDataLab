package com.example.lemacdatalab.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.util.desfaseDesdeLunes
import com.example.lemacdatalab.util.diasDelMes
import com.example.lemacdatalab.util.esFechaFutura
import com.example.lemacdatalab.util.fechaDeHoy
import com.example.lemacdatalab.util.formatearFecha
import com.example.lemacdatalab.util.nombreMesAnio

private val INICIALES_SEMANA = listOf("L", "M", "M", "J", "V", "S", "D")

@Composable
fun CalendarioRegistros(
    anio: Int,
    mes: Int,
    registrosPorFecha: Map<String, List<RegistroActividad>>,
    diaSeleccionado: String,
    puedeAvanzar: Boolean,
    alMesAnterior: () -> Unit,
    alMesSiguiente: () -> Unit,
    alSeleccionarDia: (String) -> Unit
) {
    val desfase = desfaseDesdeLunes(anio, mes)
    val totalDias = diasDelMes(anio, mes)
    val filas = (desfase + totalDias + 6) / 7
    val hoy = fechaDeHoy()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = alMesAnterior) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "Mes anterior")
                }
                Text(
                    text = nombreMesAnio(anio, mes),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = alMesSiguiente, enabled = puedeAvanzar) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Mes siguiente")
                }
            }

            Row {
                INICIALES_SEMANA.forEach { inicial ->
                    Text(
                        text = inicial,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            repeat(filas) { fila ->
                Row {
                    repeat(7) { columna ->
                        val dia = fila * 7 + columna - desfase + 1
                        if (dia in 1..totalDias) {
                            val fecha = formatearFecha(dia, mes, anio)
                            CeldaDia(
                                dia = dia,
                                registros = registrosPorFecha[fecha].orEmpty(),
                                esHoy = fecha == hoy,
                                seleccionado = fecha == diaSeleccionado,
                                esFuturo = esFechaFutura(fecha),
                                alPresionar = { alSeleccionarDia(fecha) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CeldaDia(
    dia: Int,
    registros: List<RegistroActividad>,
    esHoy: Boolean,
    seleccionado: Boolean,
    esFuturo: Boolean,
    alPresionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ambitos = registros.mapNotNull { Ambito.desdeId(it.idAmbito) }.distinct().take(4)
    val forma = RoundedCornerShape(12.dp)
    val colorPrimario = MaterialTheme.colorScheme.primary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(forma)
            .background(if (seleccionado) colorPrimario else Color.Transparent)
            .then(
                if (esHoy && !seleccionado) Modifier.border(1.dp, colorPrimario, forma) else Modifier
            )
            .clickable(enabled = !esFuturo, onClick = alPresionar)
            .semantics {
                contentDescription = "Día $dia, ${registros.size} " +
                        if (registros.size == 1) "registro" else "registros"
            }
    ) {
        Text(
            text = dia.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                seleccionado -> MaterialTheme.colorScheme.onPrimary
                esFuturo -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .padding(top = 2.dp)
                .height(5.dp)
        ) {
            ambitos.forEach { ambito ->
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(
                            color = if (seleccionado) MaterialTheme.colorScheme.onPrimary else colorAmbito(ambito),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LeyendaAmbitos() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Ambito.entries.forEach { ambito ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(colorAmbito(ambito), CircleShape)
                )
                Text(
                    text = ambito.nombre,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}
