package com.example.lemacdatalab.ui.componentes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.Escala
import com.example.lemacdatalab.model.EvolucionAmbito
import kotlin.math.roundToInt

@Composable
fun TarjetaEvolucion(evolucion: EvolucionAmbito) {
    val esAnimo = evolucion.ambito == Ambito.ANIMO
    val total = evolucion.fechas.size

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoAmbito(ambito = evolucion.ambito, tamano = 36.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(evolucion.ambito.nombre, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "${evolucion.diasRegistrados} de $total días registrados",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (evolucion.diasRegistrados == 0) {
                Text(
                    text = "Sin registros en este período.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                GraficoEvolucion(
                    evolucion = evolucion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (esAnimo) 180.dp else 140.dp)
                )

                Row(modifier = Modifier.padding(start = EJE_IZQUIERDO)) {
                    TextoEje(evolucion.fechas.first().take(5))
                    Spacer(modifier = Modifier.weight(1f))
                    TextoEje("Hoy")
                }

                if (esAnimo) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Leyenda(color = colorElevacion(), texto = "Elevación (+)")
                        Leyenda(color = colorDepresion(), texto = "Depresión (−)")
                    }
                }

                if (evolucion.diasRegistrados < 2) {
                    Text(
                        text = "Registra más días para ver la tendencia.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaEvolucionMini(
    evolucion: EvolucionAmbito,
    alPresionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ultimoIndice = evolucion.valores.indexOfLast { it != null }
    val ultimo = evolucion.valores.getOrNull(ultimoIndice)
    val ultimaElevacion = evolucion.elevacion?.getOrNull(ultimoIndice)

    Card(
        onClick = alPresionar,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoAmbito(ambito = evolucion.ambito, tamano = 30.dp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = nombreCorto(evolucion.ambito),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )

            Text(
                text = when {
                    ultimo == null -> "Sin registros"
                    ultimaElevacion != null ->
                        "↓${ultimo.roundToInt()}  ↑${ultimaElevacion.roundToInt()}"
                    else -> Escala.describir(ultimo.roundToInt())
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (ultimo != null && ultimo >= Escala.CRITICO) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 1
            )

            GraficoEvolucion(
                evolucion = evolucion,
                compacto = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            )
        }
    }
}

private fun nombreCorto(ambito: Ambito): String = when (ambito) {
    Ambito.ANIMO -> "Ánimo"
    else -> ambito.nombre
}

private val EJE_IZQUIERDO = 24.dp

@Composable
private fun colorDepresion() = MaterialTheme.colorScheme.primary

@Composable
private fun colorElevacion() = MaterialTheme.colorScheme.tertiary

@Composable
private fun TextoEje(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun Leyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        TextoEje(texto)
    }
}

@Composable
private fun GraficoEvolucion(
    evolucion: EvolucionAmbito,
    modifier: Modifier = Modifier,
    compacto: Boolean = false
) {
    val esAnimo = evolucion.ambito == Ambito.ANIMO
    val principal = colorDepresion()
    val secundario = colorElevacion()
    val critico = MaterialTheme.colorScheme.error
    val guia = MaterialTheme.colorScheme.outlineVariant
    val estiloEje = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val medidor = rememberTextMeasurer()

    val progreso = remember { Animatable(0f) }
    LaunchedEffect(evolucion) {
        progreso.snapTo(0f)
        progreso.animateTo(1f, tween(durationMillis = 800, easing = FastOutSlowInEasing))
    }

    val ultimo = evolucion.valores.lastOrNull { it != null }
    val descripcion = buildString {
        append("${evolucion.ambito.nombre}: ${evolucion.diasRegistrados} días con registro. ")
        if (ultimo != null) append("Último valor ${Escala.describir(ultimo.toInt())}.")
    }

    Canvas(modifier = modifier.semantics { contentDescription = descripcion }) {
        val izquierda = if (compacto) 4.dp.toPx() else EJE_IZQUIERDO.toPx()
        val margen = if (compacto) 4.dp.toPx() else 6.dp.toPx()
        val ancho = size.width - izquierda - margen
        val alto = size.height - margen * 2
        val minimo = if (esAnimo) -Escala.MAXIMO.toFloat() else Escala.MINIMO.toFloat()
        val maximo = Escala.MAXIMO.toFloat()
        val n = evolucion.fechas.size

        fun y(valor: Float) = margen + alto * (1f - (valor - minimo) / (maximo - minimo))
        fun x(indice: Int) = if (n == 1) izquierda + ancho / 2 else izquierda + ancho * indice / (n - 1)

        val marcas = when {
            compacto && esAnimo -> listOf(0)
            compacto -> listOf(0)
            esAnimo -> listOf(-4, 0, 4)
            else -> listOf(0, 2, 4)
        }
        marcas.forEach { valor ->
            val posY = y(valor.toFloat())
            drawLine(
                color = guia,
                start = Offset(izquierda, posY),
                end = Offset(size.width - margen, posY),
                strokeWidth = if (esAnimo && valor == 0) 1.5.dp.toPx() else 1.dp.toPx(),
                pathEffect = if (esAnimo && valor == 0) null else PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
            if (!compacto) {
                dibujarEtiqueta(medidor, if (valor > 0 && esAnimo) "+$valor" else "$valor", estiloEje, posY)
            }
        }

        clipRect(right = izquierda + (ancho + margen) * progreso.value) {
            if (esAnimo) {
                dibujarSerie(evolucion.elevacion.orEmpty(), secundario, critico, ::x, y = { y(it) })
                dibujarSerie(evolucion.valores, principal, critico, ::x, y = { y(-it) })
            } else {
                dibujarSerie(evolucion.valores, principal, critico, ::x, y = { y(it) })
            }
        }
    }
}

private fun DrawScope.dibujarEtiqueta(medidor: TextMeasurer, texto: String, estilo: TextStyle, posY: Float) {
    val medida = medidor.measure(texto, estilo)
    drawText(medida, topLeft = Offset(0f, posY - medida.size.height / 2f))
}

private fun DrawScope.dibujarSerie(
    valores: List<Float?>,
    color: Color,
    colorCritico: Color,
    x: (Int) -> Float,
    y: (Float) -> Float
) {
    var tramo: Path? = null

    valores.forEachIndexed { indice, valor ->
        if (valor == null) {
            tramo?.let { drawPath(it, color, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)) }
            tramo = null
            return@forEachIndexed
        }
        val punto = Offset(x(indice), y(valor))
        tramo = (tramo ?: Path().apply { moveTo(punto.x, punto.y) }).apply { lineTo(punto.x, punto.y) }
    }
    tramo?.let { drawPath(it, color, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)) }

    val radio = if (valores.size > 14) 2.5.dp.toPx() else 4.dp.toPx()
    valores.forEachIndexed { indice, valor ->
        if (valor != null) {
            drawCircle(
                color = if (valor >= Escala.CRITICO) colorCritico else color,
                radius = radio,
                center = Offset(x(indice), y(valor))
            )
        }
    }
}
