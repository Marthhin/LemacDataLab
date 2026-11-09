package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FilterAltOff
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.ui.componentes.FiltrosAmbito
import com.example.lemacdatalab.ui.componentes.TarjetaRegistro
import com.example.lemacdatalab.util.mesDeFecha

@Composable
fun PantallaHistorial(
    registros: List<RegistroActividad>,
    hayRegistros: Boolean,
    filtro: Ambito?,
    busqueda: String,
    cargando: Boolean,
    mensajeError: String,
    mensajeOperacion: String,
    registroEliminado: RegistroActividad?,
    alCambiarFiltro: (Ambito?) -> Unit,
    alCambiarBusqueda: (String) -> Unit,
    alLimpiarFiltros: () -> Unit,
    alVerDetalle: (Int) -> Unit,
    alEliminar: (RegistroActividad) -> Unit,
    alDeshacer: () -> Unit,
    alDescartar: () -> Unit,
    alLimpiarMensaje: () -> Unit,
    alVolver: () -> Unit
) {
    val estadoSnackbar = remember { SnackbarHostState() }
    val descartarActual by rememberUpdatedState(alDescartar)

    LaunchedEffect(registroEliminado) {
        val eliminado = registroEliminado ?: return@LaunchedEffect
        val resultado = estadoSnackbar.showSnackbar(
            message = "“${eliminado.tipoActividad}” eliminado",
            actionLabel = "Deshacer",
            duration = SnackbarDuration.Short
        )
        if (resultado == SnackbarResult.ActionPerformed) alDeshacer() else alDescartar()
    }

    LaunchedEffect(mensajeOperacion) {
        if (mensajeOperacion.isNotEmpty()) {
            estadoSnackbar.showSnackbar(mensajeOperacion)
            alLimpiarMensaje()
        }
    }

    DisposableEffect(Unit) {
        onDispose { descartarActual() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                        val cantidad = "${registros.size} " +
                                if (registros.size == 1) "registro" else "registros"
                        Text(
                            text = if (registros.isNotEmpty()) {
                                "$cantidad · Desliza a la izquierda para eliminar"
                            } else {
                                cantidad
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (hayRegistros) {
                CampoBusqueda(
                    texto = busqueda,
                    alCambiar = alCambiarBusqueda
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                        imageVector = if (busqueda.isNotBlank()) Icons.Rounded.SearchOff else Icons.Rounded.FilterAltOff,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = if (busqueda.isNotBlank()) {
                            "Sin resultados para “${busqueda.trim()}”"
                        } else {
                            "No hay registros de ${filtro?.nombre ?: "este ámbito"}"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = alLimpiarFiltros) {
                        Text("Ver todos")
                    }
                }

                else -> ListaPorMes(
                    registros = registros,
                    alVerDetalle = alVerDetalle,
                    alEliminar = alEliminar,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        SnackbarHost(
            hostState = estadoSnackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
        )
    }
}

@Composable
private fun CampoBusqueda(
    texto: String,
    alCambiar: (String) -> Unit
) {
    OutlinedTextField(
        value = texto,
        onValueChange = alCambiar,
        placeholder = { Text("Buscar en tus registros") },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = if (texto.isNotEmpty()) {
            {
                IconButton(onClick = { alCambiar("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Borrar búsqueda")
                }
            }
        } else {
            null
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )
}

@Composable
private fun ListaPorMes(
    registros: List<RegistroActividad>,
    alVerDetalle: (Int) -> Unit,
    alEliminar: (RegistroActividad) -> Unit,
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
                    modifier = Modifier
                        .animateItem()
                        .padding(top = 8.dp)
                )
            }

            items(items = lista, key = { it.idRegistro }) { registro ->
                TarjetaDeslizable(
                    registro = registro,
                    alPresionar = { alVerDetalle(registro.idRegistro) },
                    alEliminar = { alEliminar(registro) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun TarjetaDeslizable(
    registro: RegistroActividad,
    alPresionar: () -> Unit,
    alEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado = rememberSwipeToDismissBoxState()

    LaunchedEffect(estado.currentValue) {
        if (estado.currentValue == SwipeToDismissBoxValue.EndToStart) {
            alEliminar()
        }
    }

    SwipeToDismissBox(
        state = estado,
        enableDismissFromStartToEnd = false,
        modifier = modifier,
        backgroundContent = { FondoEliminar() }
    ) {
        TarjetaRegistro(registro = registro, alPresionar = alPresionar)
    }
}

@Composable
private fun FondoEliminar() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Eliminar",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
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