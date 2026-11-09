package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private data class PaginaBienvenida(
    val icono: ImageVector,
    val etiqueta: String,
    val titulo: String,
    val tituloCursiva: String,
    val texto: String
)

private val PAGINAS = listOf(
    PaginaBienvenida(
        icono = Icons.Rounded.Spa,
        etiqueta = "BIENVENIDA",
        titulo = "Un espacio para",
        tituloCursiva = "seguir tu ánimo.",
        texto = "Registra en pocos pasos cómo te sientes y lo que haces por ti en neurodesarrollo, DBT, ánimo y adicciones."
    ),
    PaginaBienvenida(
        icono = Icons.Rounded.Lock,
        etiqueta = "PRIVACIDAD",
        titulo = "Tus datos se quedan",
        tituloCursiva = "en tu teléfono.",
        texto = "Tus registros se guardan solo en este dispositivo, y los recordatorios nunca muestran su contenido."
    ),
    PaginaBienvenida(
        icono = Icons.Rounded.HealthAndSafety,
        etiqueta = "IMPORTANTE",
        titulo = "Acompaña,",
        tituloCursiva = "no reemplaza.",
        texto = "No entrega diagnósticos ni es un servicio de urgencia. Si necesitas ayuda urgente en Chile: SAMU 131 · Línea de prevención del suicidio *4141."
    )
)

@Composable
fun PantallaBienvenida(alTerminar: () -> Unit) {
    val estadoPaginas = rememberPagerState { PAGINAS.size }
    val alcance = rememberCoroutineScope()
    val esUltima = estadoPaginas.currentPage == PAGINAS.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            if (!esUltima) {
                TextButton(onClick = alTerminar) {
                    Text("Omitir")
                }
            }
        }

        HorizontalPager(
            state = estadoPaginas,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { indice ->
            ContenidoPagina(PAGINAS[indice])
        }

        IndicadorPaginas(
            total = PAGINAS.size,
            actual = estadoPaginas.currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (esUltima) {
                    alTerminar()
                } else {
                    alcance.launch {
                        estadoPaginas.animateScrollToPage(estadoPaginas.currentPage + 1)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Text(
                text = if (esUltima) "Comenzar" else "Siguiente",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ContenidoPagina(pagina: PaginaBienvenida) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = pagina.icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            text = pagina.etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = buildAnnotatedString {
                append(pagina.titulo)
                append("\n")
                withStyle(
                    SpanStyle(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append(pagina.tituloCursiva)
                }
            },
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = pagina.texto,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun IndicadorPaginas(
    total: Int,
    actual: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        repeat(total) { indice ->
            val ancho by animateDpAsState(
                targetValue = if (indice == actual) 24.dp else 8.dp,
                label = "anchoIndicador"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(ancho)
                    .background(
                        color = if (indice == actual) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}
