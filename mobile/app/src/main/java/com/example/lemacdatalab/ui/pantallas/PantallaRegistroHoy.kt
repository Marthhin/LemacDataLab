package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.CamposAmbito
import com.example.lemacdatalab.model.ContactoApoyo
import com.example.lemacdatalab.model.etiquetaResultado
import com.example.lemacdatalab.model.opcionesResultado
import com.example.lemacdatalab.model.textosDe
import com.example.lemacdatalab.ui.componentes.AvisoCritico
import com.example.lemacdatalab.ui.componentes.IconoAmbito
import com.example.lemacdatalab.ui.componentes.SelectorCatalogo
import com.example.lemacdatalab.ui.componentes.SelectorEscala
import com.example.lemacdatalab.ui.componentes.SelectorOpciones
import com.example.lemacdatalab.viewmodel.BorradorHoy
import com.example.lemacdatalab.viewmodel.EstadoPaso
import com.example.lemacdatalab.viewmodel.RegistroHoyViewModel

@Composable
fun PantallaRegistroHoy(
    viewModel: RegistroHoyViewModel,
    contactoApoyo: ContactoApoyo?,
    alTerminar: () -> Unit
) {
    viewModel.alertaPendiente?.let { alerta ->
        AvisoCritico(
            alerta = alerta,
            contacto = contactoApoyo,
            alCerrar = viewModel::descartarAlerta
        )
    }

    val total = viewModel.ambitos.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = alTerminar) {
                Icon(Icons.Rounded.Close, contentDescription = "Salir del registro de hoy")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Registro de hoy", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = if (viewModel.terminado) "Listo" else "Paso ${viewModel.paso + 1} de $total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LinearProgressIndicator(
            progress = { viewModel.paso.toFloat() / total },
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(6.dp)
        )

        if (viewModel.cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        AnimatedContent(
            targetState = viewModel.paso,
            transitionSpec = {
                val avanza = targetState > initialState
                (slideInHorizontally { if (avanza) it / 3 else -it / 3 } + fadeIn()) togetherWith
                    (slideOutHorizontally { if (avanza) -it / 3 else it / 3 } + fadeOut())
            },
            label = "pasoRegistroHoy",
            modifier = Modifier.weight(1f)
        ) { paso ->
            val ambito = viewModel.ambitos.getOrNull(paso)
            if (ambito == null) {
                ResumenHoy(viewModel = viewModel, alTerminar = alTerminar)
            } else {
                PasoAmbito(
                    ambito = ambito,
                    borrador = viewModel.borradores[paso],
                    yaRegistrado = viewModel.yaRegistrados[paso],
                    alCambiar = viewModel::actualizar
                )
            }
        }

        if (!viewModel.terminado) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = viewModel::omitir,
                        enabled = !viewModel.guardando,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 52.dp)
                    ) {
                        Icon(Icons.Rounded.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Omitir")
                    }
                    Button(
                        onClick = viewModel::guardarPaso,
                        enabled = !viewModel.guardando,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 52.dp)
                    ) {
                        Text(if (viewModel.paso == total - 1) "Guardar y terminar" else "Guardar y seguir")
                    }
                }
                if (viewModel.paso > 0) {
                    TextButton(
                        onClick = viewModel::anterior,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("Volver al ámbito anterior") }
                }
            }
        }
    }
}

@Composable
private fun PasoAmbito(
    ambito: Ambito,
    borrador: BorradorHoy,
    yaRegistrado: Boolean,
    alCambiar: ((BorradorHoy) -> BorradorHoy) -> Unit
) {
    val textos = textosDe(ambito)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconoAmbito(ambito = ambito, tamano = 52.dp)
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(ambito.nombre, style = MaterialTheme.typography.headlineSmall)
                if (yaRegistrado) {
                    Text(
                        text = "Ya lo registraste hoy: puedes corregirlo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        SeccionPaso(titulo = textos.intensidad, ayuda = textos.ayudaIntensidad) {
            SelectorEscala(
                valor = borrador.intensidad,
                alCambiar = { valor -> alCambiar { it.copy(intensidad = valor) } }
            )
        }

        when (ambito) {
            Ambito.ANIMO -> {
                SeccionPaso(titulo = "Elevación", ayuda = "¿Qué tan acelerado o acelerada, eufórico o eufórica te sentiste?") {
                    SelectorEscala(
                        valor = borrador.elevacion,
                        alCambiar = { valor -> alCambiar { it.copy(elevacion = valor) } }
                    )
                }
                SeccionPaso(titulo = "Sueño y riesgo") {
                    OutlinedTextField(
                        value = borrador.horasSueno,
                        onValueChange = { texto ->
                            val limpio = texto.replace(',', '.').filter { c -> c.isDigit() || c == '.' }.take(4)
                            alCambiar { it.copy(horasSueno = limpio) }
                        },
                        label = { Text("Horas de sueño (opcional)") },
                        suffix = { Text("h") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = borrador.altoRiesgo,
                                role = Role.Checkbox,
                                onValueChange = { valor -> alCambiar { it.copy(altoRiesgo = valor) } }
                            )
                    ) {
                        Checkbox(checked = borrador.altoRiesgo, onCheckedChange = null)
                        Text(
                            text = "Me sentí en alto riesgo o tuve experiencias inusuales",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
                SeccionPaso(titulo = "¿Qué sentiste? (opcional)") {
                    SelectorOpciones(
                        opciones = CamposAmbito.EMOCIONES,
                        seleccionada = borrador.opcion,
                        alElegir = { valor -> alCambiar { it.copy(opcion = valor) } },
                        permitirQuitar = true
                    )
                }
            }

            Ambito.DBT -> SeccionPaso(titulo = "Habilidad practicada (opcional)") {
                SelectorCatalogo(
                    opciones = CamposAmbito.HABILIDADES_DBT,
                    seleccionada = borrador.opcion,
                    otro = borrador.otro,
                    alElegir = { valor -> alCambiar { it.copy(opcion = valor) } },
                    alCambiarOtro = { valor -> alCambiar { it.copy(otro = valor) } },
                    permitirQuitar = true
                )
            }

            Ambito.ADICCIONES -> SeccionPaso(titulo = "Desencadenante (opcional)") {
                SelectorCatalogo(
                    opciones = CamposAmbito.DESENCADENANTES,
                    seleccionada = borrador.opcion,
                    otro = borrador.otro,
                    alElegir = { valor -> alCambiar { it.copy(opcion = valor) } },
                    alCambiarOtro = { valor -> alCambiar { it.copy(otro = valor) } },
                    permitirQuitar = true
                )
            }

            Ambito.NEURODESARROLLO -> SeccionPaso(titulo = "Estrategia (opcional)") {
                OutlinedTextField(
                    value = borrador.otro,
                    onValueChange = { valor -> alCambiar { it.copy(otro = valor) } },
                    placeholder = { Text("Ej.: usé un temporizador") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        val opciones = opcionesResultado(ambito)
        if (opciones.isNotEmpty()) {
            SeccionPaso(titulo = "${etiquetaResultado(ambito)} (opcional)") {
                SelectorOpciones(
                    opciones = opciones,
                    seleccionada = borrador.resultado,
                    alElegir = { valor -> alCambiar { it.copy(resultado = valor) } },
                    permitirQuitar = true
                )
            }
        }

        OutlinedTextField(
            value = borrador.nota,
            onValueChange = { valor -> alCambiar { it.copy(nota = valor) } },
            label = { Text("Nota (opcional)") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ResumenHoy(viewModel: RegistroHoyViewModel, alTerminar: () -> Unit) {
    val guardados = viewModel.estados.count { it == EstadoPaso.GUARDADO }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(44.dp)
            )
        }

        Text(
            text = if (guardados == 0) "No guardaste ningún ámbito" else "¡Registro de hoy listo!",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                viewModel.ambitos.forEachIndexed { indice, ambito ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconoAmbito(ambito = ambito, tamano = 36.dp)
                        Text(
                            text = ambito.nombre,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        )
                        Text(
                            text = when (viewModel.estados[indice]) {
                                EstadoPaso.GUARDADO -> "Guardado"
                                EstadoPaso.OMITIDO -> "Omitido"
                                EstadoPaso.PENDIENTE -> "—"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (viewModel.estados[indice] == EstadoPaso.GUARDADO) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = alTerminar,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Text("Ver mi evolución", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun SeccionPaso(
    titulo: String,
    ayuda: String? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            if (ayuda != null) {
                Text(ayuda, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            contenido()
        }
    }
}
