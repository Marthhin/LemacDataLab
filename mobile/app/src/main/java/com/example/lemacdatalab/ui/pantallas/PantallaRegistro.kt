package com.example.lemacdatalab.ui.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.model.CamposAmbito
import com.example.lemacdatalab.model.etiquetaResultado
import com.example.lemacdatalab.model.opcionesResultado
import com.example.lemacdatalab.model.textosDe
import com.example.lemacdatalab.ui.componentes.SelectorCatalogo
import com.example.lemacdatalab.ui.componentes.SelectorEscala
import com.example.lemacdatalab.ui.componentes.SelectorOpciones
import com.example.lemacdatalab.util.fechaAMillisUtc
import com.example.lemacdatalab.util.fechaDeHoy
import com.example.lemacdatalab.util.millisUtcAFecha
import com.example.lemacdatalab.viewmodel.RegistroViewModel

@Composable
fun PantallaRegistro(
    viewModel: RegistroViewModel,
    objetivo: String?,
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
                    CamposDelAmbito(ambito = ambito, viewModel = viewModel, objetivo = objetivo)
                }
            }
        }

        SeccionRegistro(titulo = textos.intensidad) {
            Text(
                text = textos.ayudaIntensidad,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SelectorEscala(
                valor = viewModel.intensidad,
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
private fun CamposDelAmbito(ambito: Ambito, viewModel: RegistroViewModel, objetivo: String?) {
    when (ambito) {
        Ambito.ANIMO -> {
            EtiquetaCampo("Elevación")
            Text(
                text = "¿Qué tan acelerado o acelerada, eufórico o eufórica te sentiste?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SelectorEscala(
                valor = viewModel.elevacion,
                alCambiar = viewModel::cambiarElevacion
            )

            OutlinedTextField(
                value = viewModel.horasSueno,
                onValueChange = viewModel::cambiarHorasSueno,
                label = { Text("Horas de sueño (opcional)") },
                placeholder = { Text("Ej.: 7,5") },
                suffix = { Text("h") },
                isError = viewModel.errorHorasSueno != null,
                supportingText = viewModel.errorHorasSueno?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            EtiquetaCampo("¿Qué sentiste? (opcional)")
            SelectorOpciones(
                opciones = CamposAmbito.EMOCIONES,
                seleccionada = viewModel.emocion,
                alElegir = viewModel::cambiarEmocion,
                permitirQuitar = true
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = viewModel.altoRiesgo,
                        role = Role.Checkbox,
                        onValueChange = viewModel::cambiarAltoRiesgo
                    )
            ) {
                Checkbox(checked = viewModel.altoRiesgo, onCheckedChange = null)
                Text(
                    text = "Me sentí en alto riesgo o tuve experiencias inusuales (oír o ver cosas que otros no)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        Ambito.DBT -> {
            EtiquetaCampo("Habilidad practicada")
            SelectorCatalogo(
                opciones = CamposAmbito.HABILIDADES_DBT,
                seleccionada = viewModel.habilidad,
                otro = viewModel.habilidadOtra,
                alElegir = viewModel::cambiarHabilidad,
                alCambiarOtro = viewModel::cambiarHabilidadOtra,
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
            EtiquetaCampo("Desencadenante (opcional)")
            SelectorCatalogo(
                opciones = CamposAmbito.DESENCADENANTES,
                seleccionada = viewModel.desencadenante,
                otro = viewModel.desencadenanteOtro,
                alElegir = viewModel::cambiarDesencadenante,
                alCambiarOtro = viewModel::cambiarDesencadenanteOtro,
                permitirQuitar = true
            )
            EtiquetaCampo("Acción protectora (opcional)")
            SelectorCatalogo(
                opciones = CamposAmbito.ACCIONES_PROTECTORAS,
                seleccionada = viewModel.accionProtectora,
                otro = viewModel.accionProtectoraOtra,
                alElegir = viewModel::cambiarAccionProtectora,
                alCambiarOtro = viewModel::cambiarAccionProtectoraOtra,
                permitirQuitar = true
            )
        }
    }

    TextoError(viewModel.errorOtro)

    val opciones = opcionesResultado(ambito)
    if (opciones.isNotEmpty()) {
        EtiquetaCampo("${etiquetaResultado(ambito)} (opcional)")
        if (ambito == Ambito.ADICCIONES && objetivo != null) {
            Text(
                text = "Tu objetivo: $objetivo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        SelectorOpciones(
            opciones = opciones,
            seleccionada = viewModel.resultado,
            alElegir = viewModel::cambiarResultado,
            permitirQuitar = true
        )
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
