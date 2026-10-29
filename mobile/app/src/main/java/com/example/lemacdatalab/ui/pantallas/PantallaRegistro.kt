package com.example.lemacdatalab.ui.pantallas

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.ui.componentes.BotonPrincipal
import com.example.lemacdatalab.ui.componentes.EncabezadoApp
import java.util.Calendar

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EncabezadoApp(
            titulo = if (editando) {
                "Editar registro"
            } else {
                "Nuevo registro"
            },
            descripcion = if (editando) {
                "Modifica los datos de la actividad."
            } else {
                "Ingresa una actividad de prueba."
            }
        )

        Text("Elige un ámbito")

        ambitos.forEach { opcion ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = ambito == opcion,
                    onClick = { alCambiarAmbito(opcion) }
                )
                Text(opcion)
            }
        }

        if (errorAmbito != null) {
            Text(
                text = errorAmbito,
                color = MaterialTheme.colorScheme.error
            )
        }

        Text("Fecha: ${fechaHora.ifEmpty { "Sin seleccionar" }}")

        Button(
            onClick = {
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
                        val fechaElegida = "%02d/%02d/%04d"
                            .format(dia, mes + 1, anio)

                        alCambiarFechaHora(fechaElegida)
                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        ) {
            Text("Elegir fecha")
        }

        if (errorFechaHora != null) {
            Text(
                text = errorFechaHora,
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = tipoActividad,
            onValueChange = alCambiarTipoActividad,
            label = { Text("Actividad realizada") },
            isError = errorTipoActividad != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorTipoActividad != null) {
            Text(
                text = errorTipoActividad,
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = intensidad,
            onValueChange = alCambiarIntensidad,
            label = { Text("Intensidad") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = errorIntensidad != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorIntensidad != null) {
            Text(
                text = errorIntensidad,
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = contexto,
            onValueChange = alCambiarContexto,
            label = { Text("Contexto") },
            isError = errorContexto != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorContexto != null) {
            Text(
                text = errorContexto,
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = nota,
            onValueChange = alCambiarNota,
            label = { Text("Nota opcional") },
            modifier = Modifier.fillMaxWidth()
        )

        BotonPrincipal(
            texto = if (editando) {
                "Guardar cambios"
            } else {
                "Guardar registro"
            },
            alPresionar = alGuardar
        )

        if (mensajeResultado.isNotEmpty()) {
            Text(mensajeResultado)
        }
    }
}