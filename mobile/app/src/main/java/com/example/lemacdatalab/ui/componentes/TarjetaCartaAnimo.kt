package com.example.lemacdatalab.ui.componentes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.PuntoGrafico
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_MAXIMA
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_MINIMA
import kotlin.math.roundToInt

@Composable
fun TarjetaCartaAnimo(puntos: List<PuntoGrafico>) {
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
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "TU CARTA DEL ÁNIMO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Intensidad promedio por día",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (puntos.size < 2) {
                Text(
                    text = "Registra actividades en al menos dos días distintos para ver tu evolución.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                GraficoLinea(
                    puntos = puntos,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )

                Row(modifier = Modifier.padding(start = 28.dp)) {
                    Text(
                        text = puntos.first().fecha.take(5),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = puntos.last().fecha.take(5),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun GraficoLinea(
    puntos: List<PuntoGrafico>,
    modifier: Modifier = Modifier
) {
    val colorLinea = MaterialTheme.colorScheme.primary
    val colorRelleno = colorLinea.copy(alpha = 0.10f)
    val colorGuia = MaterialTheme.colorScheme.outlineVariant
    val colorPunto = MaterialTheme.colorScheme.tertiary
    val colorBordePunto = MaterialTheme.colorScheme.surfaceContainerLow
    val estiloEje = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val medidorTexto = rememberTextMeasurer()

    val progreso = remember { Animatable(0f) }
    LaunchedEffect(puntos) {
        progreso.snapTo(0f)
        progreso.animateTo(1f, tween(durationMillis = 900, easing = FastOutSlowInEasing))
    }

    val descripcion = "Intensidad promedio de ${puntos.size} días. " +
            "Último valor ${puntos.last().valor.roundToInt()} de $INTENSIDAD_MAXIMA."

    Canvas(modifier = modifier.semantics { contentDescription = descripcion }) {
        val margenIzquierdo = 28.dp.toPx()
        val margenDerecho = 8.dp.toPx()
        val margenVertical = 8.dp.toPx()
        val ancho = size.width - margenIzquierdo - margenDerecho
        val alto = size.height - margenVertical * 2
        val rango = (INTENSIDAD_MAXIMA - INTENSIDAD_MINIMA).toFloat()

        fun posicionY(valor: Float) =
            margenVertical + alto * (1f - (valor - INTENSIDAD_MINIMA) / rango)

        fun posicionX(indice: Int) =
            margenIzquierdo + ancho * indice / (puntos.size - 1)

        listOf(INTENSIDAD_MINIMA, (INTENSIDAD_MINIMA + INTENSIDAD_MAXIMA) / 2, INTENSIDAD_MAXIMA).forEach { valor ->
            val y = posicionY(valor.toFloat())
            drawLine(
                color = colorGuia,
                start = Offset(margenIzquierdo, y),
                end = Offset(size.width - margenDerecho, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
            val texto = medidorTexto.measure(valor.toString(), estiloEje)
            drawText(texto, topLeft = Offset(0f, y - texto.size.height / 2f))
        }

        val posiciones = puntos.mapIndexed { indice, punto ->
            Offset(posicionX(indice), posicionY(punto.valor))
        }

        val linea = Path().apply {
            moveTo(posiciones.first().x, posiciones.first().y)
            posiciones.drop(1).forEach { lineTo(it.x, it.y) }
        }

        val relleno = Path().apply {
            addPath(linea)
            lineTo(posiciones.last().x, posicionY(INTENSIDAD_MINIMA.toFloat()))
            lineTo(posiciones.first().x, posicionY(INTENSIDAD_MINIMA.toFloat()))
            close()
        }

        clipRect(right = margenIzquierdo + (ancho + margenDerecho) * progreso.value) {
            drawPath(path = relleno, color = colorRelleno)
            drawPath(
                path = linea,
                color = colorLinea,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            posiciones.forEach { posicion ->
                drawCircle(color = colorBordePunto, radius = 6.dp.toPx(), center = posicion)
                drawCircle(color = colorPunto, radius = 4.dp.toPx(), center = posicion)
            }
        }
    }
}