package com.example.lemacdatalab.ui.pantallas

import android.app.DatePickerDialog
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.model.Ambito
import com.example.lemacdatalab.util.formatearFecha
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_INICIAL
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_MAXIMA
import com.example.lemacdatalab.viewmodel.RegistroViewModel.Companion.INTENSIDAD_MINIMA
import java.util.Calendar
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PantallaRegistro(
    fechaHora: String,
    tipoActividad: String,
    contexto: String,
    intensidad: String,
    nota: String,
    ambito: String,
    editando: Boolean,
    errorFechaHora: String?,
    errorTipoActividad: String?,
    errorContexto: String?,
    errorIntensidad: String?,
    errorAmbito: String?,
    mensajeResultado: String,
    alCambiarFechaHora: (String) -> Unit,
    alCambiarTipoActividad: (String) -> Unit,
    alCambiarContexto: (String) -> Unit,
    alCambiarIntensidad: (String) -> Unit,
    alCambiarNota: (String) -> Unit,
    alCambiarAmbito: (String) -> Unit,
    alGuardar: () -> Unit,
    alCerrar: () -> Unit
) {
    val contextoAndroid = LocalContext.current

    fun abrirCalendario() {
        val calendario = Calendar.getInstance()
        val partes = fechaHora.split("/").map { it.toIntOrNull() }

        if (partes.size == 3 && partes.none { it == null } && partes[1] in 1..12) {
            calendario.set(partes[2]!!, partes[1]!! - 1, partes[0]!!)
        }

        DatePickerDialog(
            contextoAndroid,
            { _, anio, mes, dia ->
                alCambiarFechaHora(formatearFecha(dia, mes + 1, anio))
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
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
                text = if (editando) "Editar registro" else "Nuevo registro",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = if (editando) {
                "Modifica los datos de la actividad."
            } else {
                "Registra una actividad de prueba."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SeccionRegistro(titulo = "Ámbito") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Ambito.entries.forEach { opcion ->
                    val seleccionado = ambito == opcion.nombre

                    FilterChip(
                        selected = seleccionado,
                        onClick = { alCambiarAmbito(opcion.nombre) },
                        label = { Text(opcion.nombre) },
                        leadingIcon = if (seleccionado) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }

            if (errorAmbito != null) {
                Text(
                    text = errorAmbito,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        SeccionRegistro(titulo = "Actividad") {
            Text(
                text = "Fecha",
                style = MaterialTheme.typography.labelLarge
            )

            OutlinedButton(
                onClick = { abrirCalendario() },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (errorFechaHora != null) {
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
                        text = fechaHora.ifEmpty { "Selecciona una fecha" },
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null
                    )
                }
            }

            if (errorFechaHora != null) {
                Text(
                    text = errorFechaHora,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = tipoActividad,
                onValueChange = alCambiarTipoActividad,
                label = { Text("Actividad realizada") },
                placeholder = { Text("Ej.: caminata de prueba") },
                isError = errorTipoActividad != null,
                supportingText = {
                    if (errorTipoActividad != null) {
                        Text(errorTipoActividad)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contexto,
                onValueChange = alCambiarContexto,
                label = { Text("Contexto") },
                placeholder = { Text("Ej.: después de clases") },
                isError = errorContexto != null,
                supportingText = {
                    if (errorContexto != null) {
                        Text(errorContexto)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        SeccionRegistro(titulo = "Intensidad") {
            SelectorIntensidad(
                intensidad = intensidad,
                error = errorIntensidad,
                alCambiar = alCambiarIntensidad
            )
        }

        SeccionRegistro(titulo = "Nota opcional") {
            OutlinedTextField(
                value = nota,
                onValueChange = alCambiarNota,
                label = { Text("Nota") },
                placeholder = { Text("Agrega un detalle si lo necesitas") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = alGuardar,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Text(
                text = if (editando) "Guardar cambios" else "Guardar registro",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (mensajeResultado.isNotEmpty()) {
            Text(
                text = mensajeResultado,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
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