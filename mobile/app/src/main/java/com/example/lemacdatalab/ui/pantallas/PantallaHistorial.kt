package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FilterAltOff
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.ui.componentes.TarjetaRegistro
import com.example.lemacdatalab.util.mesDeFecha

@Composable
fun PantallaHistorial(
    registros: List<RegistroActividad>,
    hayRegistros: Boolean,
    filtro: Ambito?,
    cargando: Boolean,
    mensajeError: String,
    alCambiarFiltro: (Ambito?) -> Unit,
    alVerDetalle: (Int) -> Unit,
    alVolver: () -> Unit
) {
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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Historial",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (!cargando && mensajeError.isEmpty()) {
                    Text(
                        text = "${registros.size} " +
                                if (registros.size == 1) "registro" else "registros",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (hayRegistros) {
            FiltrosAmbito(filtro = filtro, alCambiarFiltro = alCambiarFiltro)
        }

        when {
            cargando -> EstadoHistorial(modifier = Modifier.weight(1f)) {
                CircularProgressIndicator()
                Text("Cargando registros...")
            }

            mensajeError.isNotEmpty() -> EstadoHistorial(modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
            }

            !hayRegistros -> EstadoHistorial(modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Aún no tienes registros",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Las actividades que guardes aparecerán aquí.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            registros.isEmpty() -> EstadoHistorial(modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Rounded.FilterAltOff,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "No hay registros de ${filtro?.nombre ?: "este ámbito"}",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { alCambiarFiltro(null) }) {
                    Text("Ver todos")
                }
            }

            else -> ListaPorMes(
                registros = registros,
                alVerDetalle = alVerDetalle,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FiltrosAmbito(
    filtro: Ambito?,
    alCambiarFiltro: (Ambito?) -> Unit
) {
    val opciones = listOf<Ambito?>(null) + Ambito.entries

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { opcion ->
            val seleccionado = filtro == opcion
            FilterChip(
                selected = seleccionado,
                onClick = { alCambiarFiltro(opcion) },
                label = { Text(opcion?.nombre ?: "Todos") },
                leadingIcon = if (seleccionado) {
                    { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
private fun ListaPorMes(
    registros: List<RegistroActividad>,
    alVerDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val grupos = registros.groupBy { mesDeFecha(it.fechaHora) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        grupos.forEach { (mes, lista) ->
            item(key = "mes_$mes") {
                Text(
                    text = mes.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(items = lista, key = { it.idRegistro }) { registro ->
                TarjetaRegistro(
                    registro = registro,
                    alPresionar = { alVerDetalle(registro.idRegistro) }
                )
            }
        }
    }
}

@Composable
private fun EstadoHistorial(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = contenido
    )
}