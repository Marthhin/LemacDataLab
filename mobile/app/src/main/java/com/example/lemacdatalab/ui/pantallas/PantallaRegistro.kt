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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

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
    alGuardar: () -> Unit
) {
    val contextoAndroid = LocalContext.current

    val ambitos = listOf(
        "Neurodesarrollo",
        "DBT",
        "Estado de ánimo",
        "Adicciones"
    )

    fun abrirCalendario() {
        val calendario = Calendar.getInstance()
        val partes = fechaHora.split("/")

        if (partes.size == 3) {
            val dia = partes[0].toIntOrNull()
            val mes = partes[1].toIntOrNull()
            val anio = partes[2].toIntOrNull()

            if (
                dia != null &&
                mes != null &&
                anio != null &&
                mes in 1..12
            ) {
                calendario.set(anio, mes - 1, dia)
            }
        }

        DatePickerDialog(
            contextoAndroid,
            { _, anio, mes, dia ->
                val fechaElegida = String.format(
                    Locale.ROOT,
                    "%02d/%02d/%04d",
                    dia,
                    mes + 1,
                    anio
                )

                alCambiarFechaHora(fechaElegida)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (editando) "Editar registro" else "Nuevo registro",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

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
                ambitos.forEach { opcion ->
                    val seleccionado = ambito == opcion

                    FilterChip(
                        selected = seleccionado,
                        onClick = { alCambiarAmbito(opcion) },
                        label = { Text(opcion) },
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
                        text = fechaHora.ifEmpty {
                            "Selecciona una fecha"
                        },
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
            OutlinedTextField(
                value = intensidad,
                onValueChange = alCambiarIntensidad,
                label = { Text("Intensidad") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                isError = errorIntensidad != null,
                supportingText = {
                    Text(
                        text = errorIntensidad
                            ?: "Ingresa un número entero."
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
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
                text = if (editando) {
                    "Guardar cambios"
                } else {
                    "Guardar registro"
                },
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