package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.CamposAmbito
import com.example.lemacdatalab.model.etiquetaResultado
import com.example.lemacdatalab.model.opcionesResultado
import com.example.lemacdatalab.model.textosDe
import com.example.lemacdatalab.util.fechaAMillisUtc
import com.example.lemacdatalab.util.fechaDeHoy
import com.example.lemacdatalab.util.millisUtcAFecha
import com.example.lemacdatalab.viewmodel.RegistroViewModel
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_INICIAL
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_MAXIMA
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_MINIMA
import kotlin.math.roundToInt

@Composable
fun PantallaRegistro(
    viewModel: RegistroViewModel,
    alGuardar: () -> Unit,
    alCerrar: () -> Unit
) {
    var mostrarCalendario by rememberSaveable { mutableStateOf(false) }
    val ambito = viewModel.ambitoSeleccionado
    val textos = textosDe(ambito)

    if (mostrarCalendario) {
        SelectorFecha(
            fecha = viewModel.fechaHora,
            alElegir = viewModel::cambiarFechaHora,
            alCerrar = { mostrarCalendario = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = alCerrar,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Cerrar sin guardar")
            }
            Text(
                text = if (viewModel.editando) "Editar registro" else "Nuevo registro",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = if (viewModel.editando) {
                "Modifica los datos de la actividad."
            } else {
                "Registra cómo te fue, en pocos pasos."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SeccionRegistro(titulo = "Ámbito") {
            SelectorOpciones(
                opciones = Ambito.entries.map { it.nombre },
                seleccionada = viewModel.ambito,
                alElegir = viewModel::cambiarAmbito,
                permitirQuitar = false
            )
            TextoError(viewModel.errorAmbito)
        }

        SeccionRegistro(titulo = "Actividad") {
            Text(
                text = "Fecha",
                style = MaterialTheme.typography.labelLarge
            )

            OutlinedButton(
                onClick = { mostrarCalendario = true },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (viewModel.errorFechaHora != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (viewModel.fechaHora) {
                            "" -> "Selecciona una fecha"
                            fechaDeHoy() -> "Hoy · ${viewModel.fechaHora}"
                            else -> viewModel.fechaHora
                        },
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
                }
            }
            TextoError(viewModel.errorFechaHora)

            OutlinedTextField(
                value = viewModel.tipoActividad,
                onValueChange = viewModel::cambiarTipoActividad,
                label = { Text(textos.actividad) },
                placeholder = { Text(textos.ejemploActividad) },
                isError = viewModel.errorTipoActividad != null,
                supportingText = viewModel.errorTipoActividad?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.contexto,
                onValueChange = viewModel::cambiarContexto,
                label = { Text("Contexto") },
                placeholder = { Text("Ej.: después de clases") },
                isError = viewModel.errorContexto != null,
                supportingText = viewModel.errorContexto?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(visible = ambito != null) {
            if (ambito != null) {
                SeccionRegistro(titulo = "Detalles de ${ambito.nombre.lowercase()}") {
                    CamposDelAmbito(ambito = ambito, viewModel = viewModel)
                }
            }
        }

        SeccionRegistro(titulo = textos.intensidad) {
            Text(
                text = textos.ayudaIntensidad,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SelectorIntensidad(
                intensidad = viewModel.intensidad,
                error = viewModel.errorIntensidad,
                alCambiar = viewModel::cambiarIntensidad
            )
        }

        SeccionRegistro(titulo = "Nota opcional") {
            OutlinedTextField(
                value = viewModel.nota,
                onValueChange = viewModel::cambiarNota,
                label = { Text("Nota") },
                placeholder = { Text("Agrega un detalle si lo necesitas") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = alGuardar,
            enabled = !viewModel.guardando,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Text(
                text = if (viewModel.editando) "Guardar cambios" else "Guardar registro",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (viewModel.mensajeResultado.isNotEmpty()) {
            Text(
                text = viewModel.mensajeResultado,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CamposDelAmbito(ambito: Ambito, viewModel: RegistroViewModel) {
    when (ambito) {
        Ambito.ANIMO -> {
            EtiquetaCampo("¿Qué sentiste?")
            SelectorOpciones(
                opciones = CamposAmbito.EMOCIONES,
                seleccionada = viewModel.emocion,
                alElegir = viewModel::cambiarEmocion,
                permitirQuitar = false
            )
            TextoError(viewModel.errorEmocion)
        }

        Ambito.DBT -> {
            EtiquetaCampo("Habilidad practicada")
            SelectorOpciones(
                opciones = CamposAmbito.HABILIDADES_DBT,
                seleccionada = viewModel.habilidad,
                alElegir = viewModel::cambiarHabilidad,
                permitirQuitar = false
            )
            TextoError(viewModel.errorHabilidad)
        }

        Ambito.NEURODESARROLLO -> {
            OutlinedTextField(
                value = viewModel.estrategia,
                onValueChange = viewModel::cambiarEstrategia,
                label = { Text("Estrategia utilizada (opcional)") },
                placeholder = { Text("Ej.: usé un temporizador") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Ambito.ADICCIONES -> {
            OutlinedTextField(
                value = viewModel.desencadenante,
                onValueChange = viewModel::cambiarDesencadenante,
                label = { Text("Desencadenante (opcional)") },
                placeholder = { Text("Ej.: estrés después del trabajo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.accionProtectora,
                onValueChange = viewModel::cambiarAccionProtectora,
                label = { Text("Acción protectora (opcional)") },
                placeholder = { Text("Ej.: salí a caminar") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    val opciones = opcionesResultado(ambito)
    if (opciones.isNotEmpty()) {
        EtiquetaCampo("${etiquetaResultado(ambito)} (opcional)")
        SelectorOpciones(
            opciones = opciones,
            seleccionada = viewModel.resultado,
            alElegir = viewModel::cambiarResultado,
            permitirQuitar = true
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectorOpciones(
    opciones: List<String>,
    seleccionada: String,
    alElegir: (String) -> Unit,
    permitirQuitar: Boolean
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        opciones.forEach { opcion ->
            val elegida = opcion == seleccionada
            FilterChip(
                selected = elegida,
                onClick = {
                    when {
                        !elegida -> alElegir(opcion)
                        permitirQuitar -> alElegir("")
                    }
                },
                label = { Text(opcion) },
                leadingIcon = if (elegida) {
                    { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else {
                    null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorFecha(
    fecha: String,
    alElegir: (String) -> Unit,
    alCerrar: () -> Unit
) {
    val hoy = remember { fechaAMillisUtc(fechaDeHoy()) ?: Long.MAX_VALUE }
    val estado = rememberDatePickerState(
        initialSelectedDateMillis = fechaAMillisUtc(fecha) ?: hoy,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= hoy
        }
    )

    DatePickerDialog(
        onDismissRequest = alCerrar,
        confirmButton = {
            TextButton(
                onClick = {
                    estado.selectedDateMillis?.let { alElegir(millisUtcAFecha(it)) }
                    alCerrar()
                },
                enabled = estado.selectedDateMillis != null
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = alCerrar) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(
            state = estado,
            title = {
                Text(
                    text = "Fecha del registro",
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                )
            }
        )
    }
}

@Composable
private fun SelectorIntensidad(
    intensidad: String,
    error: String?,
    alCambiar: (String) -> Unit
) {
    val valor = intensidad.toIntOrNull()
        ?.coerceIn(INTENSIDAD_MINIMA, INTENSIDAD_MAXIMA)
        ?: INTENSIDAD_INICIAL

    val descripcion = when (valor) {
        in 1..3 -> "Baja"
        in 4..7 -> "Media"
        else -> "Alta"
    }

    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = valor.toString(),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = " / $INTENSIDAD_MAXIMA",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = descripcion.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp)
        )
    }

    Slider(
        value = valor.toFloat(),
        onValueChange = { alCambiar(it.roundToInt().toString()) },
        valueRange = INTENSIDAD_MINIMA.toFloat()..INTENSIDAD_MAXIMA.toFloat(),
        steps = INTENSIDAD_MAXIMA - INTENSIDAD_MINIMA - 1
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$INTENSIDAD_MINIMA · Baja",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Alta · $INTENSIDAD_MAXIMA",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    TextoError(error)
}

@Composable
private fun EtiquetaCampo(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun TextoError(error: String?) {
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SeccionRegistro(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            contenido()
        }
    }
}
