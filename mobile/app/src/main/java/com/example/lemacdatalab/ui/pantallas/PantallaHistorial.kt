package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FilterAltOff
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.ui.componentes.CalendarioRegistros
import com.example.lemacdatalab.ui.componentes.FiltrosAmbito
import com.example.lemacdatalab.ui.componentes.LeyendaAmbitos
import com.example.lemacdatalab.ui.componentes.TarjetaRegistro
import com.example.lemacdatalab.util.fechaDeHoy
import com.example.lemacdatalab.util.mesDeFecha
import com.example.lemacdatalab.viewmodel.HistorialViewModel
import com.example.lemacdatalab.viewmodel.VistaHistorial

@Composable
fun PantallaHistorial(
    viewModel: HistorialViewModel,
    alVerDetalle: (Int) -> Unit,
    alRegistrar: () -> Unit
) {
    val estadoSnackbar = remember { SnackbarHostState() }
    val descartarActual by rememberUpdatedState(viewModel::descartarEliminacion)
    val registros = viewModel.registrosVisibles
    val hayRegistros = viewModel.registros.isNotEmpty()
    var mostrarBusqueda by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel.registroEliminado) {
        val eliminado = viewModel.registroEliminado ?: return@LaunchedEffect
        val resultado = estadoSnackbar.showSnackbar(
            message = "“${eliminado.tipoActividad}” eliminado",
            actionLabel = "Deshacer",
            duration = SnackbarDuration.Short
        )
        if (resultado == SnackbarResult.ActionPerformed) {
            viewModel.deshacerEliminacion()
        } else {
            viewModel.descartarEliminacion()
        }
    }

    LaunchedEffect(viewModel.mensajeOperacion) {
        if (viewModel.mensajeOperacion.isNotEmpty()) {
            estadoSnackbar.showSnackbar(viewModel.mensajeOperacion)
            viewModel.limpiarMensaje()
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
                    .padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Historial",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (!viewModel.cargando && viewModel.mensajeError.isEmpty()) {
                        val cantidad = "${registros.size} " +
                                if (registros.size == 1) "registro" else "registros"
                        Text(
                            text = if (registros.isNotEmpty()) "$cantidad · desliza para eliminar" else cantidad,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (hayRegistros) {
                    IconButton(
                        onClick = {
                            if (mostrarBusqueda) viewModel.cambiarBusqueda("")
                            mostrarBusqueda = !mostrarBusqueda
                        }
                    ) {
                        Icon(
                            imageVector = if (mostrarBusqueda) Icons.Rounded.SearchOff else Icons.Rounded.Search,
                            contentDescription = if (mostrarBusqueda) "Cerrar búsqueda" else "Buscar"
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.cambiarVista(
                                if (viewModel.vista == VistaHistorial.LISTA) VistaHistorial.CALENDARIO else VistaHistorial.LISTA
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if (viewModel.vista == VistaHistorial.LISTA) {
                                Icons.Rounded.CalendarMonth
                            } else {
                                Icons.AutoMirrored.Rounded.ViewList
                            },
                            contentDescription = if (viewModel.vista == VistaHistorial.LISTA) "Ver calendario" else "Ver lista"
                        )
                    }
                }
            }

            if (hayRegistros) {
                AnimatedVisibility(visible = mostrarBusqueda || viewModel.busqueda.isNotEmpty()) {
                    CampoBusqueda(
                        texto = viewModel.busqueda,
                        alCambiar = viewModel::cambiarBusqueda
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                FiltrosAmbito(filtro = viewModel.filtro, alCambiarFiltro = viewModel::cambiarFiltro)
            }

            when {
                viewModel.cargando -> EstadoHistorial(modifier = Modifier.weight(1f)) {
                    CircularProgressIndicator()
                    Text("Cargando registros...")
                }

                viewModel.mensajeError.isNotEmpty() -> EstadoHistorial(modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(text = viewModel.mensajeError, color = MaterialTheme.colorScheme.error)
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

                viewModel.vista == VistaHistorial.CALENDARIO -> VistaCalendario(
                    viewModel = viewModel,
                    registros = registros,
                    alVerDetalle = alVerDetalle,
                    modifier = Modifier.weight(1f)
                )

                registros.isEmpty() -> EstadoHistorial(modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = if (viewModel.busqueda.isNotBlank()) Icons.Rounded.SearchOff else Icons.Rounded.FilterAltOff,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = if (viewModel.busqueda.isNotBlank()) {
                            "Sin resultados para “${viewModel.busqueda.trim()}”"
                        } else {
                            "No hay registros de ${viewModel.filtro?.nombre ?: "este ámbito"}"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = viewModel::limpiarFiltros) {
                        Text("Ver todos")
                    }
                }

                else -> ListaPorMes(
                    registros = registros,
                    alVerDetalle = alVerDetalle,
                    alEliminar = viewModel::eliminarRegistro,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        FloatingActionButton(
            onClick = alRegistrar,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Registrar un ámbito")
        }

        SnackbarHost(
            hostState = estadoSnackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(start = 16.dp, end = 16.dp, bottom = 88.dp)
        )
    }
}

@Composable
private fun VistaCalendario(
    viewModel: HistorialViewModel,
    registros: List<RegistroActividad>,
    alVerDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val porFecha = registros.groupBy { it.fechaHora }
    val delDia = porFecha[viewModel.diaSeleccionado].orEmpty()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "calendario") {
            CalendarioRegistros(
                anio = viewModel.anioVisible,
                mes = viewModel.mesVisible,
                registrosPorFecha = porFecha,
                diaSeleccionado = viewModel.diaSeleccionado,
                puedeAvanzar = !viewModel.esMesActual,
                alMesAnterior = viewModel::mesAnterior,
                alMesSiguiente = viewModel::mesSiguiente,
                alSeleccionarDia = viewModel::seleccionarDia
            )
        }

        item(key = "leyenda") {
            LeyendaAmbitos()
        }

        item(key = "titulo_dia") {
            Text(
                text = if (viewModel.diaSeleccionado == fechaDeHoy()) {
                    "HOY · ${viewModel.diaSeleccionado}"
                } else {
                    viewModel.diaSeleccionado
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (delDia.isEmpty()) {
            item(key = "sin_registros_dia") {
                Text(
                    text = "No hay registros este día.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            tarjetasDeslizables(
                registros = delDia,
                alVerDetalle = alVerDetalle,
                alEliminar = viewModel::eliminarRegistro
            )
        }
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
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
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

            tarjetasDeslizables(
                registros = lista,
                alVerDetalle = alVerDetalle,
                alEliminar = alEliminar
            )
        }
    }
}

private fun LazyListScope.tarjetasDeslizables(
    registros: List<RegistroActividad>,
    alVerDetalle: (Int) -> Unit,
    alEliminar: (RegistroActividad) -> Unit
) {
    items(items = registros, key = { it.idRegistro }) { registro ->
        TarjetaDeslizable(
            registro = registro,
            alPresionar = { alVerDetalle(registro.idRegistro) },
            alEliminar = { alEliminar(registro) },
            modifier = Modifier.animateItem()
        )
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
