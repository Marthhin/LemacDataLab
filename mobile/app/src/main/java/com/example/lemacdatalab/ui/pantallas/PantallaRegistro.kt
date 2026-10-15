package com.example.lemacdatalab.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.lemacdatalab.ui.componentes.BotonPrincipal
import com.example.lemacdatalab.ui.componentes.EncabezadoApp

@Composable
fun PantallaRegistro(
    fechaHora: String,
    tipoActividad: String,
    contexto: String,
    intensidad: String,
    nota: String,
    ambito: String,
    alCambiarFechaHora: (String) -> Unit,
    alCambiarTipoActividad: (String) -> Unit,
    alCambiarContexto: (String) -> Unit,
    alCambiarIntensidad: (String) -> Unit,
    alCambiarNota: (String) -> Unit,
    alCambiarAmbito: (String) -> Unit,
    alGuardar: () -> Unit
) {
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
            titulo = "Nuevo registro",
            descripcion = "Ingresa una actividad de prueba."
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

        OutlinedTextField(
            value = fechaHora,
            onValueChange = alCambiarFechaHora,
            label = { Text("Fecha y hora") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tipoActividad,
            onValueChange = alCambiarTipoActividad,
            label = { Text("Actividad realizada") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = intensidad,
            onValueChange = alCambiarIntensidad,
            label = { Text("Intensidad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contexto,
            onValueChange = alCambiarContexto,
            label = { Text("Contexto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nota,
            onValueChange = alCambiarNota,
            label = { Text("Nota opcional") },
            modifier = Modifier.fillMaxWidth()
        )

        BotonPrincipal(
            texto = "Guardar registro",
            alPresionar = alGuardar
        )
    }
}